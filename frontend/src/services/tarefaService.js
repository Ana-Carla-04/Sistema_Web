import { brToIso } from '../utils/tarefa.js';
import httpClient from './httpClient.js';

const BASE = '/SIPA/tarefas';

// Converte o estado do formulário (tudo string) no que o TarefaRequestDTO espera.
function toPayload(values) {
  return {
    titulo: values.titulo.trim(),
    descricao: values.descricao.trim() || null,
    dataLimite: brToIso(values.prazo) || null, // yyyy-MM-dd
    responsavel: values.responsavel.trim() || null,
    prioridade: values.prioridade || null,
    status: values.status || null,
  };
}

export const tarefaService = {
  // Todas as tarefas do usuário logado.
  listar: (signal) => httpClient.get(BASE, { signal }),

  // A tarefa nasce ligada a um plantio (lote).
  criar: (plantioId, values) =>
    httpClient.post(`/SIPA/plantios/${plantioId}/tarefas`, toPayload(values)),

  atualizar: (id, values) => httpClient.put(`${BASE}/${id}`, toPayload(values)),

  concluir: (id) => httpClient.patch(`${BASE}/${id}/concluir`),

  deletar: (id) => httpClient.delete(`${BASE}/${id}`),
};