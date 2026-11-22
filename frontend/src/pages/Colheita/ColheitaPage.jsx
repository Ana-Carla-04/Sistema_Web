import { useState } from 'react';

import useColheitas from '../../hooks/useColheitas.js';
import usePlantios from '../../hooks/usePlantios.js';
import { colheitaService } from '../../services/colheitaService.js';
import { calcularRendimento, parseDecimal } from '../../utils/colheita.js';
import ColheitasRecentes from './colheitasRecentes.jsx';
import styles from './colheitaPage.module.css';
import ResultadoCalculo from './resultadoCalculo.jsx';
import SimuladorRendimento from './simuladorRendimento.jsx';

export default function ColheitaPage() {
    // Os lotes do select são os plantios do usuário.
    const { plantios: lotes, loading: loadingLotes, error: lotesError } = usePlantios({
        nome: '',
        status: '',
    });
    const { colheitas, loading: loadingColheitas, error: colheitasError, reload } = useColheitas();

    const [loteEscolhido, setLoteEscolhido] = useState('');
    const [resultado, setResultado] = useState(null);
    const [calculando, setCalculando] = useState(false);

    // Enquanto o usuário não escolher, vale o primeiro lote da lista.
    const loteId = loteEscolhido || (lotes[0] ? String(lotes[0].id) : '');

    function handleLoteChange(id) {
        setLoteEscolhido(id);
        setResultado(null); // o resultado antigo é de outro lote
    }

    async function handleCalcular(values) {
        setCalculando(true);

        const producaoDigitada = parseDecimal(values.producaoBruta);
        const jaExiste = colheitas.some(
            (c) => String(c.lote) === String(lotes.find(l => l.id === Number(loteId))?.nome) &&
                Number(c.producaoBruta) === producaoDigitada
        );

        if (jaExiste) {
            alert("Já existe um lançamento com este lote e esta produção bruta!");
            setCalculando(false);
            return; // Cancela o salvamento
        }

        let despesas = 0;
        let aviso = '';
        try {
            despesas = await colheitaService.totalDespesasDoLote(loteId);
        } catch {
            aviso = 'Não foi possível carregar as despesas deste lote. O saldo está sem despesas.';
        }

        // 1. Mostra o resultado na tela (como já fazia antes)
        setResultado({
            ...calcularRendimento({
                producaoBruta: parseDecimal(values.producaoBruta),
                descontoUmidade: parseDecimal(values.descontoUmidade),
                taxaMaquina: parseDecimal(values.taxaMaquina),
                precoAlqueiro: parseDecimal(values.precoAlqueiro),
                despesas,
            }),
            aviso,
        });

        // 2. Salva no banco de dados
        try {
            // Pega a data de hoje no formato YYYY-MM-DD para o Java
            const dataAtual = new Date().toISOString().split('T')[0];

            await colheitaService.adicionarColheita(loteId, {
                plantioId: Number(loteId),
                data: dataAtual,
                producaoBruta: parseDecimal(values.producaoBruta),
                umidade: parseDecimal(values.umidade),
                descontoUmidade: parseDecimal(values.descontoUmidade),
                precoAlqueire: parseDecimal(values.precoAlqueiro),
                taxaMaquina: parseDecimal(values.taxaMaquina)
            });

            // 3. Manda o componente recarregar a tabela de baixo
            await reload();

        } catch (error) {
            console.error("Erro ao salvar no banco:", error);
            alert("Erro ao salvar a colheita no servidor.");
            const mensagemErro = error.response?.data?.message || error.response?.data?.erro || error.message;
        } finally {
            setCalculando(false);
        }
    }

    async function handleDeletar(id) {
        if (window.confirm("Tem certeza que deseja apagar este lançamento?")) {
            try {
                await colheitaService.deletarColheita(id);
                await reload(); // Recarrega a tabela
            } catch (error) {
                console.error("Erro ao deletar:", error);
                alert("Erro ao deletar a colheita.");
            }
        }
    }

    return (
        <section>
            <h1 className={styles.title}>Colheita e Receita</h1>

            <div className={styles.topGrid}>
                <SimuladorRendimento
                    lotes={lotes}
                    loteId={loteId}
                    onLoteChange={handleLoteChange}
                    loadingLotes={loadingLotes}
                    lotesError={lotesError}
                    calculando={calculando}
                    onCalcular={handleCalcular}
                />
                <ResultadoCalculo resultado={resultado} />
            </div>

            <ColheitasRecentes
                colheitas={colheitas}
                loading={loadingColheitas}
                error={colheitasError}
                onRetry={reload}
                onDeletar={handleDeletar}
            />
        </section>
    );
}