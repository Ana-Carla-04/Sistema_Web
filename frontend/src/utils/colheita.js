// 1 alqueire = 115 kg (o mesmo valor que aparece no rótulo do preço na tela).
export const KG_POR_ALQUEIRE = 115;

// Converte texto digitado em número. Aceita "35.000", "16,5", "R$ 220,00", "15 %".
// Devolve NaN quando o texto está vazio ou inválido.
export function parseDecimal(value) {
    if (typeof value === 'number') return value;

    let text = String(value ?? '').replace(/[^\d.,-]/g, '');
    if (!text) return NaN;

    if (text.includes(',')) {
        text = text.replace(/\./g, '').replace(',', '.');
    } else if (/^-?\d{1,3}(\.\d{3})+$/.test(text)) {
        text = text.replace(/\./g, ''); // "35.000" = trinta e cinco mil
    }

    const number = Number(text);
    return Number.isFinite(number) ? number : NaN;
}

// Regras do simulador (conferidas com os números da tela):
//   líquida  = bruta × (1 − desconto de umidade) × (1 − taxa da máquina)
//   alqueires = líquida ÷ 115
//   receita bruta = alqueires × preço por alqueire
//   saldo = receita bruta − despesas do lote
// A umidade (%) é informativa; quem entra na conta é o desconto por umidade.
export function calcularRendimento({
                                       producaoBruta,
                                       descontoUmidade,
                                       taxaMaquina,
                                       precoAlqueiro,
                                       despesas,
                                   }) {
    const producaoLiquida =
        producaoBruta * (1 - descontoUmidade / 100) * (1 - taxaMaquina / 100);
    const alqueires = producaoLiquida / KG_POR_ALQUEIRE;
    const receitaBruta = alqueires * precoAlqueiro;
    const saldo = receitaBruta - despesas;

    return { producaoLiquida, alqueires, receitaBruta, despesas, saldo };
}

const brl = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

export function formatBRL(value) {
    if (value === null || value === undefined || Number.isNaN(Number(value))) return '—';
    return brl.format(Number(value));
}

export function formatKg(value) {
    if (value === null || value === undefined || Number.isNaN(Number(value))) return '—';
    return `${Number(value).toLocaleString('pt-BR', { maximumFractionDigits: 2 })} kg`;
}

export function formatAlqueires(value) {
    return Number(value).toLocaleString('pt-BR', { maximumFractionDigits: 1 });
}