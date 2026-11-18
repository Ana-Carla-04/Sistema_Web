import httpClient from './httpClient.js';

// ATENÇÃO: confira estes dois caminhos e os nomes dos campos abaixo com o seu
// back-end (Colheita e Custo). Tudo que depende deles está neste arquivo.
const COLHEITAS = '/SIPA/colheita/colheitas';
const CUSTOS = '/SIPA/custos';

// Aceita alguns nomes comuns de campo; ajuste aqui se o seu DTO usar outros.
function normalizarColheita(item) {
    return {
        id: item.id,
        lote: item.loteNome ?? item.plantioNome ?? item.lote?.nome ?? item.nomeLote ?? '—',
        producaoBruta: item.producaoBruta ?? item.pesoBruto ?? item.quantidadeKg ?? null,
        precoAlqueire: item.precoAlqueire ?? item.precoAlqueire ?? item.precoVendaPorAlqueire ?? item.preco ?? null,

        receitaLiquida: item.receitaLiquida ?? item.saldo ?? item.receita ?? null,
    };
}

export const colheitaService = {
    listar: async (signal) => {
        const data = await httpClient.get(COLHEITAS, { signal });
        return (data ?? []).map(normalizarColheita);
    },

    // Soma o campo `valor` dos custos do lote (usado em "Despesas Aplicadas").
    // Pressupõe que GET /SIPA/custos aceita ?plantioId= e devolve uma lista.
    totalDespesasDoLote: async (plantioId, signal) => {
        const custos = await httpClient.get(CUSTOS, { params: { plantioId }, signal });
        return (custos ?? []).reduce((total, custo) => total + Number(custo.valor ?? 0), 0);
    },

    adicionarColheita: async (plantioId, dadosColheita) => {
        // O endpoint do seu Controller Java é @PostMapping("/plantios/{plantioId}/colheitas")
        const data = await httpClient.post(`/SIPA/colheita/plantios/${plantioId}/colheitas`, dadosColheita);
        return data;
    },

    deletarColheita: async (id) => {
        // O seu Controller tem @DeleteMapping("/colheitas/{id}")
        await httpClient.delete(`${COLHEITAS}/${id}`);
    },
};