import httpClient from './httpClient.js';

const BASE = '/SIPA/plantios';

// Converte o estado do formulário (tudo string) no que o PlantioRequestDTO espera.
function toPayload(values) {
  return {
    nome: values.nome.trim(),
    variedade: values.variedade.trim() || null,
    area: values.area === '' ? null : Number(String(values.area).replace(',', '.')),
    dataPlantio: values.dataPlantio, // yyyy-MM-dd
    status: values.status,
  };
}

export const plantioService = {
  listar: ({ nome, status, data } = {}, signal) =>
    httpClient.get(BASE, { params: { nome, status, data }, signal }),

  buscarPorId: (id, signal) => httpClient.get(`${BASE}/${id}`, { signal }),

  criar: (values) => httpClient.post(BASE, toPayload(values)),

  atualizar: (id, values) => httpClient.put(`${BASE}/${id}`, toPayload(values)),

  deletar: (id) => httpClient.delete(`${BASE}/${id}`),
};
