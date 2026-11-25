import { formatDate } from './plantio.js';

// Categorias de custo (o back-end guarda a categoria como texto livre).
export const CATEGORIA_OPTIONS = [
  { value: 'Fertilizantes', label: 'Fertilizantes' },
  { value: 'Energia', label: 'Energia' },
  { value: 'Defensivos', label: 'Defensivos' },
  { value: 'Preparo do Solo', label: 'Preparo do Solo' },
];

// ---- formatação de valores (R$) ----

// R$ 34.500 (sem centavos quando o valor é inteiro)
export function formatCurrency(value) {
  return Number(value ?? 0).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  });
}

// R$ 175.250,00 (sempre com centavos)
export function formatCurrencyFull(value) {
  return Number(value ?? 0).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  });
}

// 2000 -> '2.000,00' (valor que aparece no campo do formulário)
export function formatValorInput(value) {
  if (value === null || value === undefined || value === '') return '';
  return Number(value).toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

// Aceita '2.000,50', '2000,5', '2000.5', '2.000' e 'R$ 2.000,00'. Devolve NaN se inválido.
export function parseValor(text) {
  const clean = String(text ?? '')
    .replace(/R\$/gi, '')
    .replace(/\s/g, '');
  if (!clean) return NaN;

  let normalized;
  if (clean.includes(',')) {
    normalized = clean.replace(/\./g, '').replace(',', '.'); // 2.000,50 -> 2000.50
  } else if (/^\d{1,3}(\.\d{3})+$/.test(clean)) {
    normalized = clean.replace(/\./g, ''); // 2.000 -> 2000
  } else {
    normalized = clean; // 2000.5
  }
  return /^\d+(\.\d+)?$/.test(normalized) ? Number(normalized) : NaN;
}

// Hoje em 'yyyy-MM-dd' no fuso local (o back-end exige a data do custo).
export function todayIso(date = new Date()) {
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${date.getFullYear()}-${month}-${day}`;
}

// ---- listagem, totais e busca ----

function normalize(text) {
  return String(text ?? '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase();
}

export function sumValores(custos) {
  return custos.reduce((total, custo) => total + Number(custo.valor ?? 0), 0);
}

// Total gasto em cada categoria conhecida: [{ value, label, total }]
export function totalsByCategoria(custos) {
  return CATEGORIA_OPTIONS.map((option) => ({
    ...option,
    total: sumValores(
      custos.filter((custo) => normalize(custo.categoria) === normalize(option.value))
    ),
  }));
}

// Mais recentes primeiro.
export function sortByDataDesc(custos) {
  return [...custos].sort((a, b) => {
    const byDate = String(b.data ?? '').localeCompare(String(a.data ?? ''));
    return byDate !== 0 ? byDate : (b.id ?? 0) - (a.id ?? 0);
  });
}

// Busca (sem acento e sem diferenciar maiúsculas) em lote, categoria, descrição,
// comprovante, data e valor.
export function matchesSearch(custo, loteNome, term) {
  const search = normalize(term).trim();
  if (!search) return true;
  const haystack = normalize(
    [
      loteNome,
      custo.categoria,
      custo.descricao,
      custo.comprovante,
      formatDate(custo.data),
      formatCurrency(custo.valor),
      custo.valor,
    ].join(' ')
  );
  return haystack.includes(search);
}

// ---- validação do formulário ----

export function validateCusto(values) {
  const errors = {};

  if (!values.plantioId) {
    errors.plantioId = 'Selecione o lote.';
  }

  if (!values.categoria) {
    errors.categoria = 'Selecione a categoria.';
  }

  const valor = parseValor(values.valor);
  if (!String(values.valor).trim()) {
    errors.valor = 'Informe o valor.';
  } else if (Number.isNaN(valor)) {
    errors.valor = 'Valor inválido. Ex.: 2.000,00';
  } else if (valor <= 0) {
    errors.valor = 'O valor deve ser maior que zero.';
  }

  return errors;
}
