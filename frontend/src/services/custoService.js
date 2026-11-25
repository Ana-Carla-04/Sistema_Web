import { parseValor, todayIso } from '../utils/custo.js';
import httpClient from './httpClient.js';

const BASE = '/SIPA/custos';

// Converte o estado do formulário (tudo string) no que o CustoRequestDTO espera.
// O back-end exige a data do custo: o formulário não tem esse campo, então ela
// é a data de hoje ao criar e se mantém a original ao editar.
function toPayload(values, data) {
  return {
    data, // yyyy-MM-dd
    categoria: values.categoria,
    descricao: values.descricao.trim() || null,
    valor: parseValor(values.valor),
    // O back-end guarda só o nome do arquivo (não existe upload de arquivos ainda).
    comprovante: values.comprovante || null,
  };
}

export const custoService = {
  // Todos os custos do usuário logado.
  listar: (signal) => httpClient.get(BASE, { signal }),

  // O custo nasce ligado a um plantio (lote).
  criar: (plantioId, values) =>
    httpClient.post(`${BASE}/plantios/${plantioId}`, toPayload(values, todayIso())),

  atualizar: (id, values, data) =>
    httpClient.put(`${BASE}/${id}`, toPayload(values, data)),

  deletar: (id) => httpClient.delete(`${BASE}/${id}`),
};
