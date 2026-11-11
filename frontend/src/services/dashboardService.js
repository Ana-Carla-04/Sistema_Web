import httpClient from './httpClient.js';

export const dashboardService = {
  obterDashboard: (signal) => httpClient.get('/SIPA/dashboard', { signal }),
  listarPlantios: (signal) => httpClient.get('/SIPA/plantios', { signal }),
  listarTarefas: (signal) => httpClient.get('/SIPA/tarefas', { signal }),
  concluirTarefa: (id) => httpClient.patch(`/SIPA/tarefas/${id}/concluir`),
  obterHistorico: (ano, signal) => {
    const query = ano ? `?ano=${encodeURIComponent(ano)}` : '';
    return httpClient.get(`/SIPA/historico${query}`, { signal });
  },
  obterAnaliseFinanceira: (signal) => httpClient.get('/SIPA/analise-financeira', { signal }),
  listarColheitas: (signal) => httpClient.get('/SIPA/colheita/colheitas', { signal }),
};

