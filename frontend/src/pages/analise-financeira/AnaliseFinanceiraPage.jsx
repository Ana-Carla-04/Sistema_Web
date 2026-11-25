import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';

import Icon from '../../components/icons/Icon.jsx';
import { dashboardService } from '../../services/dashboardService.js';
import httpClient from '../../services/httpClient.js';
import styles from './AnaliseFinanceiraPage.module.css';

function formatBRL(value) {
  const num = Number(value) || 0;
  return `R$ ${Math.round(num).toLocaleString('pt-BR')}`;
}

export default function AnaliseFinanceiraPage() {
  const navigate = useNavigate();
  const outletContext = useOutletContext() || {};
  const globalSearch = (outletContext.globalSearch || '').trim().toLowerCase();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [analiseData, setAnaliseData] = useState(null);
  const [plantios, setPlantios] = useState([]);
  const [colheitas, setColheitas] = useState([]);
  const [custosList, setCustosList] = useState([]);

  async function carregarDados(signal) {
    setLoading(true);
    setError(null);

    try {
      const [afRes, plantiosRes, colheitasRes, custosRes] = await Promise.all([
        dashboardService.obterAnaliseFinanceira(signal),
        dashboardService.listarPlantios(signal).catch(() => []),
        dashboardService.listarColheitas(signal).catch(() => []),
        httpClient.get('/SIPA/custos', { signal }).catch(() => []),
      ]);

      setAnaliseData(afRes);
      setPlantios(Array.isArray(plantiosRes) ? plantiosRes : []);
      setColheitas(Array.isArray(colheitasRes) ? colheitasRes : []);
      setCustosList(Array.isArray(custosRes) ? custosRes : []);
    } catch (err) {
      if (err.name === 'AbortError') return;
      console.error('Erro ao carregar Análise Financeira:', err);
      setError(err.message || 'Não foi possível carregar os dados financeiros.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    const controller = new AbortController();
    carregarDados(controller.signal);
    return () => controller.abort();
  }, []);

  // Consolida o balanço detalhado por lote cruzando Plantios, Custos da Análise e Colheitas
  const lotesConsolidados = useMemo(() => {
    if (!Array.isArray(plantios) || plantios.length === 0) return [];

    const analiseMap = new Map(
      (analiseData?.analisePorPlantio || []).map((item) => [item.plantioId, item]),
    );

    return plantios.map((p) => {
      const afLote = analiseMap.get(p.id);
      const area = Number(p.area) || 0;

      // Soma custos vinculados ao plantio (via analisePorPlantio ou lista de custos)
      const custosDoLote =
        afLote?.custoTotal !== undefined
          ? Number(afLote.custoTotal)
          : custosList
              .filter((c) => c.plantioId === p.id)
              .reduce((acc, c) => acc + (Number(c.valor) || 0), 0);

      // Soma receitas reais das colheitas deste lote; se ainda não houver colheita, usa a estimativa padrão do backend (area * 5 * 700)
      const colheitasDoLote = colheitas.filter((c) => c.plantioId === p.id);
      const receitaColheitas = colheitasDoLote.reduce(
        (acc, c) => acc + (Number(c.receitaBruta) || 0),
        0,
      );
      const receita = receitaColheitas > 0 ? receitaColheitas : area * 5 * 700;

      const lucro = receita - custosDoLote;
      const rendimentoPorHa = area > 0 ? lucro / area : 0;
      const custoPorHa =
        afLote?.custoPorArea !== undefined && afLote.custoPorArea > 0
          ? Number(afLote.custoPorArea)
          : area > 0
            ? custosDoLote / area
            : 0;
      const margemLiquida = receita > 0 ? Math.round((lucro / receita) * 100) : 0;

      return {
        id: p.id,
        nome: p.nome,
        area,
        receita,
        custos: custosDoLote,
        lucro,
        rendimentoPorHa,
        custoPorHa,
        margemLiquida,
      };
    });
  }, [plantios, analiseData, colheitas, custosList]);

  // Filtro de busca global da barra superior
  const lotesFiltrados = useMemo(() => {
    if (!globalSearch) return lotesConsolidados;
    return lotesConsolidados.filter((lote) =>
      lote.nome.toLowerCase().includes(globalSearch),
    );
  }, [lotesConsolidados, globalSearch]);

  // Totais gerais dos 3 KPIs do topo
  const totais = useMemo(() => {
    const receitaTotal = lotesConsolidados.reduce((acc, l) => acc + l.receita, 0);
    const custoTotalBackend = Number(analiseData?.custoTotal) || 0;
    const custoTotalLotes = lotesConsolidados.reduce((acc, l) => acc + l.custos, 0);
    const custosTotais = Math.max(custoTotalBackend, custoTotalLotes);
    const lucroLiquido = receitaTotal - custosTotais;

    return {
      receitaTotal,
      custosTotais,
      lucroLiquido,
    };
  }, [lotesConsolidados, analiseData]);

  // Valor máximo de custo/ha para dimensionar as barras horizontais proporcionalmente
  const maxCustoPorHa = useMemo(() => {
    if (lotesFiltrados.length === 0) return 1;
    return Math.max(...lotesFiltrados.map((l) => l.custoPorHa), 1);
  }, [lotesFiltrados]);

  const isEmpty = !loading && plantios.length === 0 && (Number(analiseData?.custoTotal) || 0) === 0;

  if (loading) {
    return (
      <div className={styles.page}>
        <header className={styles.header}>
          <h1 className={styles.title}>Análise Financeira</h1>
          <p className={styles.subtitle}>
            Relatórios detalhados de lucro, custos por hectare e margens líquidas
          </p>
        </header>
        <div className={styles.loadingCard}>
          <div className={styles.spinner} />
          <p className={styles.loadingText}>Calculando balanço financeiro dos lotes...</p>
        </div>
      </div>
    );
  }

  if (isEmpty) {
    return (
      <div className={styles.page}>
        <header className={styles.header}>
          <h1 className={styles.title}>Análise Financeira</h1>
          <p className={styles.subtitle}>
            Relatórios detalhados de lucro, custos por hectare e margens líquidas
          </p>
        </header>
        <div className={styles.emptyCard}>
          <div className={styles.emptyIcon}>
            <Icon name="chart" size={32} />
          </div>
          <h2 className={styles.emptyTitle}>Nenhum dado financeiro encontrado</h2>
          <p className={styles.emptyText}>
            Cadastre seus lotes em <strong>Plantios</strong> e registre seus gastos em{' '}
            <strong>Custos</strong> para visualizar o balanço detalhado por unidade produtiva.
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
        <h1 className={styles.title}>Análise Financeira</h1>
        <p className={styles.subtitle}>
          Relatórios detalhados de lucro, custos por hectare e margens líquidas
        </p>
      </header>

      {error && (
        <div className={styles.errorBanner}>
          <span>{error}</span>
          <button type="button" className={styles.retryBtn} onClick={() => carregarDados()}>
            Tentar novamente
          </button>
        </div>
      )}

      {/* 1. Linha de 3 KPIs Coloridos */}
      <section className={styles.kpiRow} aria-label="Resumo financeiro">
        <article className={`${styles.kpiCard} ${styles.kpiReceita}`}>
          <span className={styles.kpiLabel}>RECEITA TOTAL</span>
          <strong className={styles.kpiValue}>{formatBRL(totais.receitaTotal)}</strong>
        </article>

        <article className={`${styles.kpiCard} ${styles.kpiCustos}`}>
          <span className={styles.kpiLabel}>CUSTOS TOTAIS</span>
          <strong className={styles.kpiValue}>{formatBRL(totais.custosTotais)}</strong>
        </article>

        <article className={`${styles.kpiCard} ${styles.kpiLucro}`}>
          <span className={styles.kpiLabel}>LUCRO LÍQUIDO REALIZADO</span>
          <strong className={styles.kpiValue}>{formatBRL(totais.lucroLiquido)}</strong>
        </article>
      </section>

      {/* 2. Tabela: Balanço Detalhado por Unidade Produtiva (Lotes) */}
      <section className={styles.card}>
        <h2 className={styles.cardTitle}>Balanço Detalhado por Unidade Produtiva (Lotes)</h2>

        <div className={styles.tableWrapper}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>Lote</th>
                <th>Área</th>
                <th>Receita</th>
                <th>Custos</th>
                <th>Lucro</th>
                <th>Rendimento/ha</th>
                <th className={styles.alignRight}>Margem Líquida</th>
              </tr>
            </thead>
            <tbody>
              {lotesFiltrados.length === 0 ? (
                <tr>
                  <td colSpan={7} className={styles.emptyTableMsg}>
                    Nenhum lote encontrado para o filtro pesquisado.
                  </td>
                </tr>
              ) : (
                lotesFiltrados.map((lote) => (
                  <tr key={lote.id}>
                    <td className={styles.cellLote}>{lote.nome}</td>
                    <td className={styles.cellMuted}>
                      {lote.area.toLocaleString('pt-BR', { maximumFractionDigits: 1 })} ha
                    </td>
                    <td className={styles.cellDark}>{formatBRL(lote.receita)}</td>
                    <td className={styles.cellCost}>
                      <button
                        type="button"
                        className={styles.linkCostBtn}
                        onClick={() => navigate(`/custos?plantioId=${lote.id}`)}
                        title={`Ver custos do lote ${lote.nome}`}
                      >
                        {formatBRL(lote.custos)}
                      </button>
                    </td>
                    <td className={lote.lucro >= 0 ? styles.cellProfit : styles.cellCost}>
                      {formatBRL(lote.lucro)}
                    </td>
                    <td className={styles.cellMuted}>{formatBRL(lote.rendimentoPorHa)}</td>
                    <td className={`${styles.alignRight} ${styles.cellMargin}`}>
                      {lote.margemLiquida}%
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </section>

      {/* 3. Barras Horizontais: Custos por Hectare por Lote */}
      <section className={styles.card}>
        <h2 className={styles.cardTitle}>Custos por Hectare por Lote</h2>

        <div className={styles.barsList}>
          {lotesFiltrados.map((lote) => {
            // Largura proporcional (mínimo de 6% quando > 0 para manter a pílula visível como no design)
            const ratio = maxCustoPorHa > 0 ? (lote.custoPorHa / maxCustoPorHa) * 42 : 0;
            const widthPercent = lote.custoPorHa > 0 ? Math.max(ratio, 8) : 2;

            return (
              <div
                key={lote.id}
                className={`${styles.barRow} ${styles.barRowClickable}`}
                onClick={() => navigate(`/custos?plantioId=${lote.id}`)}
                title={`Ver custos do lote ${lote.nome} (${formatBRL(lote.custoPorHa)}/ha)`}
                role="button"
                tabIndex={0}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    navigate(`/custos?plantioId=${lote.id}`);
                  }
                }}
              >
                <span className={styles.barLabel}>{lote.nome}</span>

                <div className={styles.barTrack}>
                  <div
                    className={styles.barFill}
                    style={{ width: `${ Math.min(widthPercent, 100) }%` }}
                  />
                </div>

                <strong className={styles.barValue}>
                  {formatBRL(lote.custoPorHa)}/ha
                </strong>
              </div>
            );
          })}
        </div>
      </section>
    </div>
  );
}
