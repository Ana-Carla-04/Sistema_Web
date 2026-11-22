// Valores aceitos pelo back-end (TarefaRequestDTO). Mantenha em maiúsculas.
export const STATUS_OPTIONS = [
  { value: 'PENDENTE', label: 'Pendente' },
  { value: 'EM_ANDAMENTO', label: 'Em andamento' },
  { value: 'CONCLUIDA', label: 'Concluída' },
];

export const PRIORIDADE_OPTIONS = [
  { value: 'PRIORITARIA', label: 'Prioritária' },
  { value: 'AGENDADO', label: 'Agendado' },
  { value: 'PLANEJADO', label: 'Planejado' },
];

// Botões de filtro do topo da tela ('' = todas).
export const FILTER_OPTIONS = [
  { value: '', label: 'Todas' },
  { value: 'PENDENTE', label: 'Pendentes' },
  { value: 'EM_ANDAMENTO', label: 'Em andamento' },
  { value: 'CONCLUIDA', label: 'Concluídas' },
];

export function getLabel(options, value) {
  const found = options.find((option) => option.value === value);
  return found ? found.label : '—';
}

// Tarefas antigas podem não ter status gravado: concluída -> CONCLUIDA, senão PENDENTE.
export function getTarefaStatus(tarefa) {
  if (tarefa.concluida) return 'CONCLUIDA';
  return tarefa.status || 'PENDENTE';
}

export function countByStatus(tarefas) {
  const counts = { total: tarefas.length, PENDENTE: 0, EM_ANDAMENTO: 0, CONCLUIDA: 0 };
  tarefas.forEach((tarefa) => {
    const status = getTarefaStatus(tarefa);
    if (status in counts) counts[status] += 1;
  });
  return counts;
}

// Busca por nome, descrição, lote ou responsável (sem diferenciar maiúsculas e acentos).
function normalize(text) {
  return String(text ?? '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase();
}

export function matchesSearch(tarefa, term) {
  const query = normalize(term).trim();
  if (!query) return true;
  return [tarefa.titulo, tarefa.descricao, tarefa.plantioNome, tarefa.responsavel].some(
    (field) => normalize(field).includes(query)
  );
}

// Mais próximas primeiro; tarefas sem prazo vão para o fim.
export function sortByPrazo(tarefas) {
  return [...tarefas].sort((a, b) => {
    if (a.dataLimite && b.dataLimite && a.dataLimite !== b.dataLimite) {
      return a.dataLimite < b.dataLimite ? -1 : 1;
    }
    if (a.dataLimite && !b.dataLimite) return -1;
    if (!a.dataLimite && b.dataLimite) return 1;
    return a.id - b.id;
  });
}

// ---- datas no formato dd/mm/aaaa (o back-end usa yyyy-MM-dd) ----

// Vai formatando enquanto digita: "23082025" -> "23/08/2025".
export function maskDate(value) {
  const digits = String(value).replace(/\D/g, '').slice(0, 8);
  if (digits.length <= 2) return digits;
  if (digits.length <= 4) return `${digits.slice(0, 2)}/${digits.slice(2)}`;
  return `${digits.slice(0, 2)}/${digits.slice(2, 4)}/${digits.slice(4)}`;
}

export function isValidDateBR(value) {
  if (!/^\d{2}\/\d{2}\/\d{4}$/.test(value)) return false;
  const [day, month, year] = value.split('/').map(Number);
  const date = new Date(year, month - 1, day);
  return (
    date.getFullYear() === year && date.getMonth() === month - 1 && date.getDate() === day
  );
}

// 'yyyy-MM-dd' -> 'dd/MM/yyyy' (sem passar por Date, evita bug de fuso horário)
export function isoToBR(isoDate) {
  if (!isoDate) return '';
  const [year, month, day] = String(isoDate).split('-');
  return `${day}/${month}/${year}`;
}

// 'dd/MM/yyyy' -> 'yyyy-MM-dd' ('' se vazio)
export function brToIso(value) {
  if (!value) return '';
  const [day, month, year] = value.split('/');
  return `${year}-${month}-${day}`;
}

// Validação do formulário de tarefa (espelha o TarefaRequestDTO do back-end).
export function validateTarefa(values) {
  const errors = {};

  if (!values.titulo.trim()) {
    errors.titulo = 'Informe o nome da tarefa.';
  }

  if (!values.plantioId) {
    errors.plantioId = 'Selecione o lote.';
  }

  if (values.prazo && !isValidDateBR(values.prazo)) {
    errors.prazo = 'Use uma data válida (dd/mm/aaaa).';
  }

  if (values.responsavel.length > 120) {
    errors.responsavel = 'O responsável deve ter no máximo 120 caracteres.';
  }

  return errors;
}