// Valores de status aceitos pelo back-end (campo String livre no Plantio).
// O DashboardService usa 'PLANTADO', então mantenha esses valores em maiúsculas.
export const STATUS_OPTIONS = [
  { value: 'PLANTADO', label: 'Plantio' },
  { value: 'CRESCENDO', label: 'Crescendo' },
  { value: 'COLHIDO', label: 'Colhido' },
];

export function getStatusLabel(status) {
  const found = STATUS_OPTIONS.find((option) => option.value === String(status).toUpperCase());
  return found ? found.label : status;
}

// 'yyyy-MM-dd' -> 'dd/MM/yyyy' (sem passar por Date, evita bug de fuso horário)
export function formatDate(isoDate) {
  if (!isoDate) return '—';
  const [year, month, day] = String(isoDate).split('-');
  return `${day}/${month}/${year}`;
}

export function formatArea(area) {
  if (area === null || area === undefined) return '—';
  return `${Number(area).toLocaleString('pt-BR')} ha`;
}

// Safra vai de julho a junho: a "2025/2026" começa em 01/07/2025.
export function getCurrentSafraStartYear(date = new Date()) {
  return date.getMonth() >= 6 ? date.getFullYear() : date.getFullYear() - 1;
}

export function formatSafra(startYear) {
  return `${startYear}/${startYear + 1}`;
}

export function buildSafraOptions(count = 4) {
  const current = getCurrentSafraStartYear();
  return Array.from({ length: count }, (_, index) => {
    const startYear = current - index;
    return { value: String(startYear), label: `Safra: ${formatSafra(startYear)}` };
  });
}

// Retorna true se a data (yyyy-MM-dd) pertence à safra que começa em startYear.
export function isInSafra(isoDate, startYear) {
  if (!isoDate) return false;
  const start = `${startYear}-07-01`;
  const end = `${Number(startYear) + 1}-06-30`;
  return isoDate >= start && isoDate <= end;
}
