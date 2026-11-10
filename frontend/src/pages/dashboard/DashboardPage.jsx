import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';

import Icon from '../../components/icons/Icon.jsx';
import { dashboardService } from '../../services/dashboardService.js';
import { weatherService } from '../../services/weatherService.js';
import { storage } from '../../utils/storage.js';
import styles from './DashboardPage.module.css';

// Widget meteorológico estático da região de Mossoró, RN
const CLIMA_MOSSORO = {
  cidade: 'MOSSORÓ, RN',
  min: 22,
  max: 33,
  atual: 28,
  condicao: 'Parcialmente Nublado',
  resumo: 'Tempo ideal para irrigação controlada',
  metricas: [
    { key: 'umidade', valor: '72%', label: 'Umidade' },
    { key: 'vento', valor: '14 km/h', label: 'Vento' },
    { key: 'chuva', valor: '8 mm', label: 'Chuva/dia' },
    { key: 'uv', valor: 'UV 7', label: 'Índice UV' },
  ],
  proximosDias: [
    { dia: 'Qui', temp: 25, condicao: 'Chuva', resumoDia: 'Previsão de chuva moderada — reduza a irrigação artificial.' },
    { dia: 'Sex', temp: 31, condicao: 'Ensolarado', resumoDia: 'Dia quente e seco — monitore a lâmina de água nos lotes.' },
    { dia: 'Sáb', temp: 28, condicao: 'Ideal', resumoDia: 'Tempo ideal para irrigação controlada' },
    { dia: 'Dom', temp: 26, condicao: 'Nublado', resumoDia: 'Nebulosidade estável — boas condições para manejo no campo.' },
  ],
  alerta: {
    prefixo: 'Condições favoráveis para irrigação ',
    destaque: 'Sábado',
    sufixo: ' – umidade e vento ideais.',
  },
};

function mapPlantioStatus(rawStatus) {
  const upper = String(rawStatus || '').toUpperCase();
  if (upper.includes('CRESC')) {
    return { statusKey: 'CRESCIMENTO', statusLabel: 'Em crescimento' };
  }
  if (upper.includes('COLH')) {
    return { statusKey: 'COLHEITA', statusLabel: 'Colheita' };
  }
  if (upper.includes('PREP')) {
    return { statusKey: 'PREPARACAO', statusLabel: 'Preparação' };
  }
  return { statusKey: 'PLANTADO', statusLabel: 'Plantado' };
}

function formatDateBR(isoDate) {
  if (!isoDate) return '—';
  const parts = String(isoDate).split('-');
  if (parts.length !== 3) return isoDate;
  return `${parts[2]}/${parts[1]}/${parts[0]}`;
}

function formatCurrencyBRL(value) {
  if (value == null || isNaN(value)) return 'R$ 0';
  return `R$ ${Math.round(Number(value)).toLocaleString('pt-BR')}`;
}

export default function DashboardPage() {
  const navigate = useNavigate();
  const outletContext = useOutletContext() || {};
  const globalSearch = (outletContext.globalSearch || '').trim().toLowerCase();

  // Estados de controle da API
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Estados dos dados reais vindos do backend
  const [dashboardData, setDashboardData] = useState(null);
  const [plantios, setPlantios] = useState([]);
  const [tarefas, setTarefas] = useState([]);
  const [safras, setSafras] = useState([]);
  const [colheitas, setColheitas] = useState([]);

  // Estado meteorológico com dados reais obtidos via Open-Meteo
  const [clima, setClima] = useState(CLIMA_MOSSORO);

  // Estados interativos de visualização
  const [filtroStatusLote, setFiltroStatusLote] = useState(null);
  const [diaClimaAtivo, setDiaClimaAtivo] = useState(null);
  const [safraDestacada, setSafraDestacada] = useState(null);
  const [categoriaDestacada, setCategoriaDestacada] = useState(null);

  // Função centralizada para carregar todos os dados reais do backend e serviços
  async function carregarDashboard(signal) {
    setLoading(true);
    setError(null);

    try {
      const token = storage.getToken();
      const headers = {
        'Content-Type': 'application/json',
        ...(token && { Authorization: `Bearer ${token}` }),
      };

      // 1. Chamada obrigatória ao endpoint principal (/SIPA/dashboard) com Bearer Token
      const dashPromise = fetch('http://localhost:8080/SIPA/dashboard', {
        headers,
        signal,
      }).then(async (res) => {
        if (!res.ok) {
          throw new Error(`Erro ao obter dashboard (HTTP ${res.status})`);
        }
        return res.json();
      });

      // 2. Chamadas complementares aos demais módulos do backend para alimentar áreas, safras, colheitas e tarefas reais
      const plantiosPromise = dashboardService.listarPlantios(signal).catch(() => []);
      const tarefasPromise = dashboardService.listarTarefas(signal).catch(() => []);
      const historicoPromise = dashboardService.obterHistorico(null, signal).catch(() => null);
      const colheitasPromise = dashboardService.listarColheitas(signal).catch(() => []);

      // 3. Chamada ao serviço de clima em tempo real para Mossoró, RN
      const climaPromise = weatherService.obterClimaAtual(signal).catch((err) => {
        console.warn('Usando fallback meteorológico:', err);
        return null;
      });

      const [dashResult, plantiosResult, tarefasResult, histResult, colheitasResult, climaResult] =
        await Promise.all([
          dashPromise,
          plantiosPromise,
          tarefasPromise,
          historicoPromise,
          colheitasPromise,
          climaPromise,
        ]);

      setDashboardData(dashResult);
      setPlantios(Array.isArray(plantiosResult) ? plantiosResult : []);
      setTarefas(Array.isArray(tarefasResult) ? tarefasResult : []);
      setSafras(Array.isArray(histResult?.safras) ? histResult.safras : []);
      setColheitas(Array.isArray(colheitasResult) ? colheitasResult : []);

      if (climaResult) {
        setClima(climaResult);
        if (climaResult.proximosDias?.length > 0) {
          setDiaClimaAtivo(climaResult.proximosDias[0].dia);
        }
      }
    } catch (err) {
      if (err.name === 'AbortError') return;
      console.error('Erro na requisição do dashboard:', err);
      setError(err.message || 'Falha na comunicação com o backend.');
    } finally {
      setLoading(false);
    }
  }

  // Dispara a busca dos dados reais assim que a tela carregar
  useEffect(() => {
    const controller = new AbortController();
    carregarDashboard(controller.signal);
    return () => controller.abort();
  }, []);

  // Verifica se o usuário não possui dados cadastrados no banco
  const isEmpty = useMemo(() => {
    if (!dashboardData) return false;
    const plantiosZerados = Number(dashboardData.plantiosTotal || 0) === 0 && plantios.length === 0;
    const custosZerados =
      Number(dashboardData.custosTotal || 0) === 0 &&
      Number(dashboardData.custosTotais || 0) === 0;
    const colheitasZeradas = colheitas.length === 0;
    return plantiosZerados && custosZerados && colheitasZeradas;
  }, [dashboardData, plantios, colheitas]);

  // KPIs calculados com base nos dados do banco de dados
  const kpis = useMemo(() => {
    // 1. Produção Total (em Tons ou kg)
    const totalProd = colheitas.reduce(
      (acc, c) => acc + (Number(c.producaoLiquida || c.producaoBruta) || 0),
      0,
    );
    const isTons = totalProd >= 1000;
    const producaoTotal = isTons
      ? (totalProd / 1000).toLocaleString('pt-BR', { maximumFractionDigits: 1 })
      : totalProd.toLocaleString('pt-BR');
    const producaoUnidade = isTons ? 'Tons' : 'kg';

    // 2. Área Plantada (em ha)
    const totalArea = plantios.reduce((acc, p) => acc + (Number(p.area) || 0), 0);
    const areaPlantada = totalArea.toLocaleString('pt-BR', { maximumFractionDigits: 1 });

    // 3. Receita Bruta
    const recColheitas = colheitas.reduce((acc, c) => acc + (Number(c.receitaBruta) || 0), 0);
    const recSafras = safras.reduce((acc, s) => acc + (Number(s.receitaTotal) || 0), 0);
    const receitaBruta = formatCurrencyBRL(recColheitas > 0 ? recColheitas : recSafras);

    // 4. Lucro Líquido
    const saldoColheitas = colheitas.reduce((acc, c) => acc + (Number(c.saldo) || 0), 0);
    const lucroSafras = safras.reduce((acc, s) => acc + (Number(s.lucroEstimado) || 0), 0);
    const custosTotais = Number(dashboardData?.custosTotais || 0);
    const lucroCalc =
      saldoColheitas !== 0
        ? saldoColheitas
        : lucroSafras !== 0
          ? lucroSafras
          : (recColheitas || recSafras) - custosTotais;
    const lucroLiquido = formatCurrencyBRL(lucroCalc);

    return {
      producaoTotal,
      producaoUnidade,
      areaPlantada,
      areaUnidade: 'ha',
      receitaBruta,
      lucroLiquido,
    };
  }, [colheitas, plantios, safras, dashboardData]);

  // Gráfico 1: Produção por Safra real
  const producaoPorSafra = useMemo(() => {
    if (safras.length > 0) {
      return safras.map((s) => ({
        id: `safra-${s.ano}`,
        label: `Safra ${s.ano}`,
        value: Math.round(s.produtividade || (s.receitaTotal ? s.receitaTotal / 1000 : 0) || 0),
        ano: s.ano,
      }));
    }
    return [];
  }, [safras]);

  const maxSafraValue = useMemo(
    () => Math.max(...producaoPorSafra.map((item) => item.value), 1),
    [producaoPorSafra],
  );

  // Gráfico 2: Distribuição de Custos real
  const custosTotaisNum = Number(dashboardData?.custosTotais || 0);
  const custoTotalLabel = useMemo(() => {
    const totalK = Math.round(custosTotaisNum / 1000);
    return `R$ ${totalK > 0 ? `${totalK}k` : custosTotaisNum.toLocaleString('pt-BR')}`;
  }, [custosTotaisNum]);

  const distribuicaoCustos = useMemo(() => {
    const categorias = dashboardData?.custosPorCategoria || [];
    if (!Array.isArray(categorias) || categorias.length === 0) return [];

    const totalBase = custosTotaisNum || 1;
    return categorias.slice(0, 4).map((c, idx) => {
      const pct = Math.round(((Number(c.total || 0)) / totalBase) * 100);
      return {
        id: `cat-${idx}`,
        categoria: c.categoria,
        percent: pct,
        lines: [`${c.categoria} (${pct}%)`],
      };
    });
  }, [dashboardData, custosTotaisNum]);

  const maxCustoPercent = useMemo(
    () => Math.max(...distribuicaoCustos.map((item) => item.percent), 1),
    [distribuicaoCustos],
  );

  // Status dos Lotes reais
  const lotes = useMemo(() => {
    const base = plantios.length > 0 ? plantios : (dashboardData?.ultimosPlantios || []);
    return base.slice(0, 4).map((p) => {
      const { statusKey, statusLabel } = mapPlantioStatus(p.status);
      return {
        id: p.id,
        nome: p.nome,
        area: p.area ?? 0,
        statusKey,
        statusLabel,
      };
    });
  }, [plantios, dashboardData]);

  const lotesVisiveis = useMemo(() => {
    return lotes.filter((lote) => {
      const matchStatus = !filtroStatusLote || lote.statusKey === filtroStatusLote;
      const matchSearch =
        !globalSearch ||
        lote.nome.toLowerCase().includes(globalSearch) ||
        lote.statusLabel.toLowerCase().includes(globalSearch);
      return matchStatus && matchSearch;
    });
  }, [lotes, filtroStatusLote, globalSearch]);

  // Próximas Tarefas reais
  const plantioMap = useMemo(
    () => new Map(plantios.map((p) => [p.id, p.nome])),
    [plantios],
  );

  const tarefasFormatadas = useMemo(() => {
    const variants = [
      { tag: 'Prioritário', tagVariant: 'prioritario' },
      { tag: 'Pendente', tagVariant: 'pendente' },
      { tag: 'Agendado', tagVariant: 'agendado' },
      { tag: 'Planejado', tagVariant: 'planejado' },
    ];
    return tarefas.slice(0, 4).map((t, index) => {
      const v = variants[index % variants.length];
      const loteLabel = t.plantioId
        ? (plantioMap.get(t.plantioId) ? `Lote ${plantioMap.get(t.plantioId)}` : `Lote #${t.plantioId}`)
        : 'Geral';
      return {
        id: t.id,
        titulo: t.titulo,
        data: formatDateBR(t.dataLimite),
        lote: loteLabel,
        tag: t.concluida ? 'Concluída' : v.tag,
        tagVariant: v.tagVariant,
        concluida: Boolean(t.concluida),
      };
    });
  }, [tarefas, plantioMap]);

  const tarefasVisiveis = useMemo(() => {
    if (!globalSearch) return tarefasFormatadas;
    return tarefasFormatadas.filter(
      (t) =>
        t.titulo.toLowerCase().includes(globalSearch) ||
        t.lote.toLowerCase().includes(globalSearch) ||
        t.tag.toLowerCase().includes(globalSearch),
    );
  }, [tarefasFormatadas, globalSearch]);

  const tarefasPendentesCount = useMemo(
    () => tarefasFormatadas.filter((t) => !t.concluida).length,
    [tarefasFormatadas],
  );

  const diaSelecionadoInfo = useMemo(() => {
    if (!clima?.proximosDias || clima.proximosDias.length === 0) {
      return { resumoDia: clima?.resumo || 'Tempo ideal para irrigação controlada' };
    }
    return (
      clima.proximosDias.find((d) => d.dia === diaClimaAtivo) ||
      clima.proximosDias[0]
    );
  }, [clima, diaClimaAtivo]);

  // Ação de concluir tarefa com persistência no backend
  async function handleToggleTarefa(tarefa) {
    const novoStatus = !tarefa.concluida;
    setTarefas((prev) =>
      prev.map((item) => (item.id === tarefa.id ? { ...item, concluida: novoStatus } : item)),
    );

    if (novoStatus) {
      try {
        await dashboardService.concluirTarefa(tarefa.id);
      } catch (err) {
        console.error('Erro ao concluir tarefa no backend:', err);
      }
    }
  }

  function handleToggleFiltroStatus(statusKey) {
    setFiltroStatusLote((prev) => (prev === statusKey ? null : statusKey));
  }

  // 1. Renderiza indicador de carregamento (loading)
  if (loading) {
    return (
      <div className={styles.dashboard}>
        <h1 className={styles.pageTitle}>Painel de Controle</h1>
        <div className={styles.loadingContainer}>
          <div className={styles.spinner} />
          <p className={styles.loadingText}>Carregando informações do dashboard...</p>
          <span className={styles.loadingSubtext}>Conectando ao backend SIPA...</span>
        </div>
      </div>
    );
  }

  // 2. Renderiza mensagem amigável de estado vazio caso a API retorne dados zerados ou vazios
  if (isEmpty) {
    return (
      <div className={styles.dashboard}>
        <h1 className={styles.pageTitle}>Painel de Controle</h1>
        <div className={styles.emptyDashboardCard}>
          <div className={styles.emptyDashboardIcon}>
            <Icon name="sprout" size={32} />
          </div>
          <h2 className={styles.emptyDashboardTitle}>Nenhuma informação encontrada</h2>
          <p className={styles.emptyDashboardText}>
            Nenhuma informação encontrada. Por favor, vá até a tela de <strong>Plantios</strong> ou{' '}
            <strong>Custos</strong> e adicione seus primeiros dados para visualizar o Dashboard.
          </p>
          <div className={styles.emptyDashboardActions}>
            <button
              type="button"
              className={styles.primaryActionBtn}
              onClick={() => navigate('/plantios')}
            >
              <Icon name="sprout" size={16} />
              <span>Ir para Plantios</span>
            </button>
            <button
              type="button"
              className={styles.secondaryActionBtn}
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

  // 3. Renderiza o Dashboard completo alimentado 100% com dados reais do banco
  return (
    <div className={styles.dashboard}>
      <h1 className={styles.pageTitle}>Painel de Controle</h1>

      {error && (
        <div className={styles.errorBanner}>
          <span>{error}</span>
          <button
            type="button"
            className={styles.retryBtn}
            onClick={() => carregarDashboard()}
          >
            Tentar novamente
          </button>
        </div>
      )}

      {/* 1. Linha de KPIs (clicáveis para navegar às respectivas páginas) */}
      <section className={styles.kpiGrid} aria-label="Indicadores principais">
        <button
          type="button"
          className={styles.kpiCard}
          onClick={() => navigate('/colheita')}
          title="Ir para Colheita e Produção"
        >
          <span className={styles.kpiLabel}>PRODUÇÃO TOTAL</span>
          <div className={styles.kpiValueRow}>
            <strong className={styles.kpiNumber}>{kpis.producaoTotal}</strong>
            <span className={styles.kpiUnit}>{kpis.producaoUnidade}</span>
          </div>
        </button>

        <button
          type="button"
          className={styles.kpiCard}
          onClick={() => navigate('/plantios')}
          title="Ir para Meus Plantios"
        >
          <span className={styles.kpiLabel}>ÁREA PLANTADA</span>
          <div className={styles.kpiValueRow}>
            <strong className={styles.kpiNumber}>{kpis.areaPlantada}</strong>
            <span className={styles.kpiUnit}>{kpis.areaUnidade}</span>
          </div>
        </button>

        <button
          type="button"
          className={styles.kpiCard}
          onClick={() => navigate('/colheita')}
          title="Ir para Colheita e Receita"
        >
          <span className={styles.kpiLabel}>RECEITA BRUTA</span>
          <div className={styles.kpiValueRow}>
            <strong className={styles.kpiNumber}>{kpis.receitaBruta}</strong>
          </div>
        </button>

        <button
          type="button"
          className={styles.kpiCard}
          onClick={() => navigate('/analise-financeira')}
          title="Ir para Análise Financeira"
        >
          <span className={styles.kpiLabel}>LUCRO LÍQUIDO</span>
          <div className={styles.kpiValueRow}>
            <strong className={styles.kpiNumber}>{kpis.lucroLiquido}</strong>
          </div>
        </button>
      </section>

      {/* 2. Linha de Gráficos: Produção por Safra + Distribuição de Custos */}
      <section className={styles.chartsGrid}>
        {/* Gráfico 1: Produção por Safra */}
        <article className={styles.card}>
          <header className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>Produção por Safra (Alqueiro(115kg))</h2>
          </header>

          {producaoPorSafra.length === 0 ? (
            <div className={styles.chartEmptyNotice}>
              Nenhuma safra consolidada registrada no momento.
            </div>
          ) : (
            <div className={styles.safraChartArea}>
              {producaoPorSafra.map((item) => {
                const heightPx = Math.round(52 + (item.value / maxSafraValue) * 56);
                const isSelected = safraDestacada === item.id;

                return (
                  <button
                    key={item.id}
                    type="button"
                    className={`${styles.chartColumnBtn} ${isSelected ? styles.chartColumnSelected : ''}`}
                    onClick={() => {
                      setSafraDestacada(item.id);
                      navigate(`/historico?ano=${item.ano}`);
                    }}
                    title={`Ver histórico da ${item.label} (${item.value} alqueires)`}
                  >
                    <div className={styles.barTrack}>
                      <div
                        className={styles.barFill}
                        style={{ height: `${heightPx}px` }}
                      >
                        <span className={styles.barValueInside}>{item.value}</span>
                      </div>
                    </div>
                    <span className={styles.barCaption}>{item.label}</span>
                  </button>
                );
              })}
            </div>
          )}
        </article>

        {/* Gráfico 2: Distribuição de Custos */}
        <article className={styles.card}>
          <header className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>
              Distribuição de Custos – {custoTotalLabel}
            </h2>
          </header>

          {distribuicaoCustos.length === 0 ? (
            <div className={styles.chartEmptyNotice}>
              Nenhum custo por categoria cadastrado no momento.
            </div>
          ) : (
            <div className={styles.custosChartArea}>
              {distribuicaoCustos.map((item) => {
                const heightPx = Math.round(48 + (item.percent / maxCustoPercent) * 48);
                const isSelected = categoriaDestacada === item.id;

                return (
                  <button
                    key={item.id}
                    type="button"
                    className={`${styles.chartColumnBtn} ${isSelected ? styles.chartColumnSelected : ''}`}
                    onClick={() => {
                      setCategoriaDestacada(item.id);
                      navigate(`/custos?categoria=${encodeURIComponent(item.categoria)}`);
                    }}
                    title={`Ver custos de ${item.categoria} (${item.percent}%)`}
                  >
                    <div className={styles.barTrack}>
                      <div
                        className={styles.barFill}
                        style={{ height: `${heightPx}px` }}
                      >
                        <span className={styles.barValueInside}>{item.percent}</span>
                      </div>
                    </div>
                    <span className={styles.barCaptionMultiline}>
                      {item.lines.map((line, idx) => (
                        <span key={idx} className={styles.captionLine}>
                          {line}
                        </span>
                      ))}
                    </span>
                  </button>
                );
              })}
            </div>
          )}
        </article>
      </section>

      {/* 3. Linha Intermediária: Status dos Lotes + Widget de Clima */}
      <section className={styles.middleGrid}>
        {/* Card Status dos Lotes */}
        <article className={styles.card}>
          <header className={styles.cardHeaderWithBadge}>
            <h2 className={styles.cardTitle}>Status dos Lotes</h2>
            <button
              type="button"
              className={styles.grayPillBadge}
              onClick={() => navigate('/plantios')}
              title="Ver todos os lotes em Plantios"
            >
              {lotes.length} Lotes
            </button>
          </header>

          <div className={styles.lotesList}>
            {lotesVisiveis.length === 0 ? (
              <div className={styles.emptyStateContainer}>
                <p className={styles.emptyStateText}>Nenhum lote encontrado no momento.</p>
                <button
                  type="button"
                  className={styles.emptyStateBtn}
                  onClick={() => navigate('/plantios')}
                >
                  + Cadastrar Plantio
                </button>
              </div>
            ) : (
              lotesVisiveis.map((lote) => (
                <button
                  key={lote.id}
                  type="button"
                  className={`${styles.loteItem} ${styles[`loteItem_${lote.statusKey}`]}`}
                  onClick={() => navigate(`/plantios?lote=${encodeURIComponent(lote.nome)}`)}
                  title={`Abrir detalhes do ${lote.nome}`}
                >
                  <div className={styles.loteLeft}>
                    <span className={`${styles.statusDot} ${styles[`dot_${lote.statusKey}`]}`} />
                    <div className={styles.loteTexts}>
                      <strong className={styles.loteName}>{lote.nome}</strong>
                      <span className={styles.loteArea}>
                        Área: {Number(lote.area).toLocaleString('pt-BR')} ha
                      </span>
                    </div>
                  </div>

                  <span className={`${styles.loteStatusText} ${styles[`text_${lote.statusKey}`]}`}>
                    {lote.statusLabel}
                  </span>
                </button>
              ))
            )}
          </div>

          {/* Legenda interativa no rodapé do card */}
          <footer className={styles.lotesLegend}>
            <button
              type="button"
              className={`${styles.legendBtn} ${filtroStatusLote === 'PLANTADO' ? styles.legendBtnActive : ''}`}
              onClick={() => handleToggleFiltroStatus('PLANTADO')}
            >
              <span className={`${styles.legendDot} ${styles.dot_PLANTADO}`} />
              Plantado
            </button>
            <button
              type="button"
              className={`${styles.legendBtn} ${filtroStatusLote === 'CRESCIMENTO' ? styles.legendBtnActive : ''}`}
              onClick={() => handleToggleFiltroStatus('CRESCIMENTO')}
            >
              <span className={`${styles.legendDot} ${styles.dot_CRESCIMENTO}`} />
              Crescimento
            </button>
            <button
              type="button"
              className={`${styles.legendBtn} ${filtroStatusLote === 'COLHEITA' ? styles.legendBtnActive : ''}`}
              onClick={() => handleToggleFiltroStatus('COLHEITA')}
            >
              <span className={`${styles.legendDot} ${styles.dot_COLHEITA}`} />
              Colheita
            </button>
            <button
              type="button"
              className={`${styles.legendBtn} ${filtroStatusLote === 'PREPARACAO' ? styles.legendBtnActive : ''}`}
              onClick={() => handleToggleFiltroStatus('PREPARACAO')}
            >
              <span className={`${styles.legendDot} ${styles.dot_PREPARACAO}`} />
              Preparação
            </button>
          </footer>
        </article>

        {/* Card de Clima em Tempo Real — Mossoró, RN */}
        <article className={styles.card}>
          <div className={styles.weatherTopRow}>
            <span className={styles.weatherLocation}>
              <Icon name="pin" size={13} strokeWidth={2} />
              {clima.cidade}
            </span>

            <span className={styles.weatherMinMax}>
              <span className={styles.tempMin}>Min {clima.min}°</span>
              <span className={styles.tempDivider}> | </span>
              <span className={styles.tempMax}>Max {clima.max}°</span>
            </span>
          </div>

          <div className={styles.weatherMainTemp}>
            {clima.atual}°C
          </div>

          <div className={styles.weatherCondition}>
            <span className={styles.weatherConditionDot} />
            <span>{clima.condicao}</span>
          </div>

          <p className={styles.weatherSubtitle}>
            {diaSelecionadoInfo.resumoDia || clima.resumo}
          </p>

          <hr className={styles.weatherDivider} />

          <div className={styles.weatherMetricsGrid}>
            {clima.metricas.map((m) => (
              <div key={m.key} className={styles.weatherMetricBox}>
                <strong className={styles.weatherMetricValue}>{m.valor}</strong>
                <span className={styles.weatherMetricLabel}>{m.label}</span>
              </div>
            ))}
          </div>

          <div className={styles.forecastSection}>
            <span className={styles.forecastHeading}>PRÓXIMOS DIAS</span>

            <div className={styles.forecastGrid}>
              {clima.proximosDias.map((item) => {
                const isSelected = diaClimaAtivo === item.dia;
                return (
                  <button
                    key={item.dia}
                    type="button"
                    className={`${styles.forecastDayBox} ${isSelected ? styles.forecastDayActive : ''}`}
                    onClick={() => setDiaClimaAtivo(item.dia)}
                    title={`Ver condição de ${item.diaCompleto || item.dia}`}
                  >
                    <span className={styles.forecastDayName}>{item.dia}</span>
                    <strong className={styles.forecastDayTemp}>{item.temp}°</strong>
                    <span className={styles.forecastDayCond}>{item.condicao}</span>
                  </button>
                );
              })}
            </div>
          </div>

          <div className={styles.weatherAlertBanner}>
            {clima.alerta.prefixo}
            <strong className={styles.weatherAlertHighlight}>
              {clima.alerta.destaque}
            </strong>
            {clima.alerta.sufixo}
          </div>
        </article>
      </section>

      {/* 4. Card Inferior: Próximas Tarefas */}
      <section className={styles.tasksSection}>
        <article className={styles.card}>
          <header className={styles.cardHeaderWithBadge}>
            <h2 className={styles.cardTitle}>Próximas Tarefas</h2>
            <button
              type="button"
              className={styles.greenPillBadge}
              onClick={() => navigate('/tarefas')}
              title="Ir para a página de Tarefas"
            >
              {tarefasPendentesCount} tarefas
            </button>
          </header>

          <div className={styles.tasksList}>
            {tarefasVisiveis.length === 0 ? (
              <div className={styles.emptyStateContainer}>
                <p className={styles.emptyStateText}>Nenhuma tarefa pendente no momento.</p>
                <button
                  type="button"
                  className={styles.emptyStateBtn}
                  onClick={() => navigate('/tarefas')}
                >
                  + Nova Tarefa
                </button>
              </div>
            ) : (
              tarefasVisiveis.map((tarefa) => (
                <div
                  key={tarefa.id}
                  className={`${styles.taskRow} ${tarefa.concluida ? styles.taskRowDone : ''}`}
                >
                  <div className={styles.taskLeft}>
                    <button
                      type="button"
                      className={`${styles.taskCheckBtn} ${tarefa.concluida ? styles.taskCheckBtnActive : ''}`}
                      onClick={() => handleToggleTarefa(tarefa)}
                      aria-label={`Marcar "${tarefa.titulo}" como concluída`}
                      title={tarefa.concluida ? 'Desmarcar tarefa' : 'Concluir tarefa'}
                    >
                      <Icon name="check" size={14} strokeWidth={2.2} />
                    </button>

                    <button
                      type="button"
                      className={styles.taskInfoBtn}
                      onClick={() => navigate('/tarefas')}
                    >
                      <strong className={styles.taskTitle}>{tarefa.titulo}</strong>
                      <span className={styles.taskMeta}>
                        <span className={styles.taskDate}>{tarefa.data}</span>
                        <span className={styles.taskLote}>{tarefa.lote}</span>
                      </span>
                    </button>
                  </div>

                  <button
                    type="button"
                    className={`${styles.taskBadge} ${styles[`badge_${tarefa.tagVariant}`]}`}
                    onClick={() => navigate(`/tarefas?status=${encodeURIComponent(tarefa.tag)}`)}
                  >
                    {tarefa.tag}
                  </button>
                </div>
              ))
            )}
          </div>

          <footer className={styles.tasksFooter}>
            <button
              type="button"
              className={styles.verTodasBtn}
              onClick={() => navigate('/tarefas')}
            >
              <span>Ver todas</span>
              <Icon name="arrowRight" size={13} strokeWidth={2.2} />
            </button>
          </footer>
        </article>
      </section>
    </div>
  );
}
