
package br.edu.ufersa.SIPA.freatures.historico.domain;

import java.util.List;


// Domain Service responsável pelas regras de negócio puras do Histórico.
public class HistoricoDomainService {

    public String calcularTendencia(Double produtividadeAtual, Double produtividadeAnterior) {
        // Se não temos dados do ano anterior, não podemos calcular tendência
        if (produtividadeAnterior == null || produtividadeAnterior == 0.0) {
            return "SEM_DADOS";
        }

        if (produtividadeAtual == null) {
            return "SEM_DADOS";
        }

        // Calcula a variação percentual
        double variacao = ((produtividadeAtual - produtividadeAnterior) / produtividadeAnterior) * 100;

        // Define a tendência com base na variação
        if (variacao > 5.0) {
            return "ALTA";
        } else if (variacao < -5.0) {
            return "BAIXA";
        } else {
            return "ESTAVEL";
        }
    }

    // Calcula a produtividade (KG_POR_ALQUEIRE) de uma safra.

    public Double calcularProdutividade(Double producaoTotal, Double areaTotal) {
        if (areaTotal == null || areaTotal == 0.0) {
            return 0.0;
        }
        if (producaoTotal == null) {
            return 0.0;
        }
        return producaoTotal / areaTotal;
    }

  
    //Calcula o lucro estimado de uma safra.

    public Double calcularLucroEstimado(Double receitaTotal, Double custoTotal) {
        double receita = receitaTotal == null ? 0.0 : receitaTotal;
        double custo = custoTotal == null ? 0.0 : custoTotal;
        return receita - custo;
    }


    // Filtra uma lista de safras por ano.
     
    public <T> List<T> filtrarPorAno(List<T> safras, Integer ano) {
        if (ano == null) {
            return safras;
        }
        // A filtragem real será feita no Application Service,
        return safras;
    }
}