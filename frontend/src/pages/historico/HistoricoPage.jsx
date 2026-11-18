import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useOutletContext, useSearchParams } from 'react-router-dom';

import Icon from '../../components/icons/Icon.jsx';
import { dashboardService } from '../../services/dashboardService.js';
import styles from './HistoricoPage.module.css';

const DEFAULT_FILTER_YEARS = [2026, 2025, 2024, 2023];

function formatBRL(value) {
  const num = Number(value) || 0;
  return `R$ ${Math.round(num).toLocaleString('pt-BR')}`;
}

function formatCustoCompacto(value) {
  const num = Number(value) || 0;
  if (num >= 1000) {
    const k = Math.round(num / 1000);
    return `R$ ${k}K`;
  }
  return `R$ ${Math.round(num).toLocaleString('pt-BR')}`;
}

export default function HistoricoPage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const outletContext = useOutletContext() || {};
  const globalSearch = (outletContext.globalSearch || '').trim().toLowerCase();

  const anoParam = searchParams.get('ano');
  const anoSelecionado = anoParam ? Number(anoParam) : null;

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [todasSafras, setTodasSafras] = useState([]);
  const [safrasExibidas, setSafrasExibidas] = useState([]);
  const [colheitas, setColheitas] = useState([]);

  async function carregarHistorico(anoFiltro, signal) {
    setLoading(true);
    setError(null);

    try {
      const [todasRes, filtradoRes, colheitasRes] = await Promise.all([
        dashboardService.obterHistorico(null, signal),
        anoFiltro ? dashboardService.obterHistorico(anoFiltro, signal) : Promise.resolve(null),
        dashboardService.listarColheitas(signal).catch(() => []),
      ]);

      const listaCompleta = Array.isArray(todasRes?.safras) ? todasRes.safras : [];
      const listaFiltrada = anoFiltro
        ? Array.isArray(filtradoRes?.safras)
          ? filtradoRes.safras
          : listaCompleta.filter((s) => s.ano === anoFiltro)
        : listaCompleta;

      setTodasSafras(listaCompleta);
      setSafrasExibidas(listaFiltrada);
      setColheitas(Array.isArray(colheitasRes) ? colheitasRes : []);
    } catch (err) {
      if (err.name === 'AbortError') return;
      console.error('Erro ao carregar Histórico de Produção:', err);
      setError(err.message || 'Não foi possível carregar o histórico de produção.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    const controller = new AbortController();
    carregarHistorico(anoSelecionado, controller.signal);
    return () => controller.abort();
  }, [anoSelecionado]);

  function handleSelectAno(ano) {
    if (ano === null) {
      setSearchParams({});
    } else {
      setSearchParams({ ano: String(ano) });
    }
  }

  // Anos disponíveis nos botões de filtro (combina anos do banco + 2026..2023)
  const anosDisponiveis = useMemo(() => {
    const setAnos = new Set(DEFAULT_FILTER_YEARS);
    todasSafras.forEach((s) => {
      if (s.ano) setAnos.add(Number(s.ano));
    });
    return Array.from(setAnos).sort((a, b) => b - a);
  }, [todasSafras]);

  // Processa e enriquece cada safra com dados de colheita, produtividade em ton/ha e produção em toneladas
  const safrasProcessadas = useMemo(() => {
    return safrasExibidas.map((s) => {
      const ano = Number(s.ano);
      const areaTotal = Number(s.areaTotal) || 0;

      // Verifica se há colheitas reais lançadas naquele ano
      const colheitasDoAno = colheitas.filter((c) => {
        if (!c.data) return false;
        const cYear = Number(String(c.data).split('-')[0]);
        return cYear === ano;
      });

      const prodColheitaKg = colheitasDoAno.reduce(
        (acc, c) => acc + (Number(c.producaoLiquida || c.producaoBruta) || 0),
        0,
      );
      const recColheita = colheitasDoAno.reduce(
        (acc, c) => acc + (Number(c.receitaBruta) || 0),
        0,
      );

      const receitaTotal = recColheita > 0 ? recColheita : Number(s.receitaTotal) || 0;
      const custoTotal = Number(s.custoTotal) || 0;
      const lucroEstimado =
        recColheita > 0 ? receitaTotal - custoTotal : Number(s.lucroEstimado) || receitaTotal - custoTotal;

      // Produtividade em ton/ha (o backend retorna ~3500 em kg/ha equivalentes; converte para ton/ha)
      const rawProd = Number(s.produtividade) || 0;
      const prodTonHa =
        prodColheitaKg > 0 && areaTotal > 0
          ? prodColheitaKg / 1000 / areaTotal
          : rawProd >= 100
            ? rawProd / 1000
            : rawProd;

      // Produção total em toneladas (t)
      const producaoTon =
        prodColheitaKg > 0
          ? prodColheitaKg / 1000
          : Math.round(areaTotal * prodTonHa * 100) / 100;

      return {
        ano,
        safraTitulo: `Safra ${ano - 1}/${ano}`,
        areaTotal,
        producaoTon,
        receitaTotal,
        custoTotal,
        lucroEstimado,
        prodTonHa,
        tendencia: s.tendencia || 'ALTA',
      };
    });
  }, [safrasExibidas, colheitas]);

  // Filtro de busca global
  const safrasFiltradas = useMemo(() => {
    if (!globalSearch) return safrasProcessadas;
    return safrasProcessadas.filter(
      (s) =>
        s.safraTitulo.toLowerCase().includes(globalSearch) ||
        String(s.ano).includes(globalSearch),
    );
  }, [safrasProcessadas, globalSearch]);

  // Para os gráficos, ordena em ordem cronológica crescente (ex.: 2024 -> 2025 -> 2026)
  const safrasCronologicas = useMemo(
    () => [...safrasFiltradas].sort((a, b) => a.ano - b.ano),
    [safrasFiltradas],
  );

  const maxProdutividade = useMemo(() => {
    if (safrasCronologicas.length === 0) return 1;
    return Math.max(...safrasCronologicas.map((s) => s.prodTonHa), 1);
  }, [safrasCronologicas]);

  const maxCusto = useMemo(() => {
    if (safrasCronologicas.length === 0) return 1;
    return Math.max(...safrasCronologicas.map((s) => s.custoTotal), 1);
  }, [safrasCronologicas]);

  const isDatabaseEmpty = !loading && todasSafras.length === 0;

  if (loading) {
    return (
      <div className={styles.page}>
        <header className={styles.header}>
          <h1 className={styles.title}>Histórico de Produção</h1>
          <p className={styles.subtitle}>
            Acompanhe o desempenho do arrozal ao longo dos anos para decisões futuras
          </p>
        </header>
        <div className={styles.loadingCard}>
          <div className={styles.spinner} />
          <p className={styles.loadingText}>Carregando histórico de safras consolidadas...</p>
        </div>
      </div>
    );
  }

  if (isDatabaseEmpty) {
    return (
      <div className={styles.page}>
        <header className={styles.header}>
          <h1 className={styles.title}>Histórico de Produção</h1>
          <p className={styles.subtitle}>
            Acompanhe o desempenho do arrozal ao longo dos anos para decisões futuras
          </p>
        </header>
        <div className={styles.emptyCard}>
          <div className={styles.emptyIcon}>
            <Icon name="history" size={32} />
          </div>
          <h2 className={styles.emptyTitle}>Nenhuma safra registrada</h2>
          <p className={styles.emptyText}>
            Adicione seus lotes em <strong>Plantios</strong> e seus gastos em{' '}
            <strong>Custos</strong> para consolidar o histórico de produção por ano.
          </p>
          <div className={styles.emptyActions}>
            <button
              type="button"
              className={styles.primaryBtn}
              onClick={() => navigate('/plantios')}
            >
              <Icon name="sprout" size={16} />
              <span>Ir para Plantios</span>
            </button>
            <button
              type="button"
              className={styles.secondaryBtn}
              onClick={() => navigate('/custos')}
            >
              <Icon name="cost" size={16} />
              <span>Ir para Custos</span>
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h1 className={styles.title}>Histórico de Produção</h1>
        <p className={styles.subtitle}>
          Acompanhe o desempenho do arrozal ao longo dos anos para decisões futuras
        </p>
      </header>

      {error && (
        <div className={styles.errorBanner}>
          <span>{error}</span>
          <button
            type="button"
            className={styles.retryBtn}
            onClick={() => carregarHistorico(anoSelecionado)}
          >
            Tentar novamente
          </button>
        </div>
      )}

      {/* 1. Barra de Filtro por Ano */}
      <section className={styles.filterCard} aria-label="Filtrar por ano da safra">
        <div className={styles.filterPills}>
          <button
            type="button"
            className={`${styles.pillBtn} ${anoSelecionado === null ? styles.pillBtnActive : ''}`}
            onClick={() => handleSelectAno(null)}
          >
            Todos Anos
          </button>

          {anosDisponiveis.map((ano) => (
            <button
              key={ano}
              type="button"
              className={`${styles.pillBtn} ${anoSelecionado === ano ? styles.pillBtnActive : ''}`}
              onClick={() => handleSelectAno(ano)}
            >
              {ano}
            </button>
          ))}
        </div>
      </section>

      {/* 2. Linha de Gráficos: Evolução da Produtividade + Histórico de Custos Consolidados */}
      <section className={styles.chartsRow}>
        {/* Gráfico Esquerdo: Evolução da Produtividade */}
        <article className={styles.chartCard}>
          <h2 className={styles.chartTitle}>Evolução da Produtividade (ton/ha)</h2>

          {safrasCronologicas.length === 0 ? (
            <div className={styles.noDataNotice}>
              Nenhum registro encontrado para o ano selecionado.
            </div>
          ) : (
            <div className={styles.chartCanvas}>
              <div className={styles.gridLines} aria-hidden="true">
                <span />
                <span />
                <span />
                <span />
              </div>

              <div className={styles.barsContainer}>
                {safrasCronologicas.map((s) => {
                  const ratio = maxProdutividade > 0 ? s.prodTonHa / maxProdutividade : 0;
                  const heightPx = Math.round(54 + ratio * 74);

                  return (
                    <div key={s.ano} className={styles.barColumn}>
                      <div className={styles.barArea}>
                        <div
                          className={`${styles.barFill} ${styles.barGreen}`}
                          style={{ height: `${heightPx}px` }}
                        />
                      </div>
                      <strong className={styles.barValueLabel}>
                        {s.prodTonHa.toLocaleString('pt-BR', {
                          minimumFractionDigits: 2,
                          maximumFractionDigits: 2,
                        })}{' '}
                        ton/ha
                      </strong>
                      <span className={styles.barYearLabel}>{s.ano}</span>
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </article>

        {/* Gráfico Direito: Histórico de Custos Consolidados */}
        <article className={styles.chartCard}>
          <h2 className={styles.chartTitle}>Histórico de Custos Consolidados</h2>

          {safrasCronologicas.length === 0 ? (
            <div className={styles.noDataNotice}>
              Nenhum custo consolidado para o ano selecionado.
            </div>
          ) : (
            <div className={styles.chartCanvas}>
              <div className={styles.gridLines} aria-hidden="true">
                <span />
                <span />
                <span />
                <span />
              </div>

              <div className={styles.barsContainer}>
                {safrasCronologicas.map((s) => {
                  const ratio = maxCusto > 0 ? s.custoTotal / maxCusto : 0;
                  const heightPx = s.custoTotal > 0 ? Math.round(54 + ratio * 70) : 28;

                  return (
                    <div key={s.ano} className={styles.barColumn}>
                      <div className={styles.barArea}>
                        <div
                          className={`${styles.barFill} ${styles.barRed}`}
                          style={{ height: `${heightPx}px` }}
                        />
                      </div>
                      <strong className={styles.barValueLabel}>
                        {formatCustoCompacto(s.custoTotal)}
                      </strong>
                      <span className={styles.barYearLabel}>{s.ano}</span>
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </article>
      </section>

      {/* 3. Tabela de Safras Consolidadas */}
      <section className={styles.tableCard}>
        <h2 className={styles.tableTitle}>Tabela de Safras Consolidadas</h2>

        <div className={styles.tableWrapper}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>Safra</th>
                <th>Área Total</th>
                <th>Produção</th>
                <th>Receita</th>
                <th>Custo Total</th>
                <th>Lucro Estimado</th>
                <th className={styles.alignRight}>Produtividade</th>
              </tr>
            </thead>
            <tbody>
              {safrasFiltradas.length === 0 ? (
                <tr>
                  <td colSpan={7} className={styles.emptyTableMsg}>
                    Nenhuma safra consolidada para o filtro selecionado.
                  </td>
                </tr>
              ) : (
                safrasFiltradas.map((s, idx) => {
                  const isDown =
                    s.tendencia === 'BAIXA' ||
                    (idx === safrasFiltradas.length - 1 && safrasFiltradas.length > 1);

                  return (
                    <tr key={s.ano}>
                      <td className={styles.cellSafra}>
                        <span>Safra</span>
                        <span>
                          {s.ano - 1}/{s.ano}
                        </span>
                      </td>
                      <td className={styles.cellMuted}>
                        {s.areaTotal.toLocaleString('pt-BR', { maximumFractionDigits: 1 })} ha
                      </td>
                      <td className={styles.cellDark}>
                        {s.producaoTon.toLocaleString('pt-BR', { maximumFractionDigits: 1 })} t
                      </td>
                      <td className={styles.cellDark}>{formatBRL(s.receitaTotal)}</td>
                      <td className={styles.cellCost}>{formatBRL(s.custoTotal)}</td>
                      <td className={s.lucroEstimado >= 0 ? styles.cellProfit : styles.cellCost}>
                        {formatBRL(s.lucroEstimado)}
                      </td>
                      <td className={`${styles.alignRight} ${styles.cellProd}`}>
                        <span>
                          {s.prodTonHa.toLocaleString('pt-BR', {
                            minimumFractionDigits: 2,
                            maximumFractionDigits: 2,
                          })}{' '}
                          t/ha
                        </span>
                        <span
                          className={isDown ? styles.trendDown : styles.trendUp}
                          title={isDown ? 'Tendência de queda' : 'Tendência de alta'}
                        >
                          {isDown ? '↘' : '↗'}
                        </span>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}
