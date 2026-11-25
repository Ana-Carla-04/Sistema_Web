import { formatAlqueires, formatBRL, formatKg } from '../../utils/colheita.js';
import styles from './resultadoCalculo.module.css';

export default function ResultadoCalculo({ resultado }) {
    const saldoNegativo = resultado ? resultado.saldo < 0 : false;

    return (
        <aside className={styles.card} aria-live="polite">
            <h2 className={styles.title}>Resultado do Cálculo</h2>

            <dl className={styles.list}>
                <div className={styles.row}>
                    <dt>Produção Líquida:</dt>
                    <dd>{resultado ? formatKg(resultado.producaoLiquida) : '—'}</dd>
                </div>
                <div className={styles.row}>
                    <dt>Equivalente em Alqueire:</dt>
                    <dd>{resultado ? formatAlqueires(resultado.alqueires) : '—'}</dd>
                </div>
                <div className={styles.row}>
                    <dt>Receita Bruta Estimada:</dt>
                    <dd>{resultado ? formatBRL(resultado.receitaBruta) : '—'}</dd>
                </div>
                <div className={styles.row}>
                    <dt>Despesas Aplicadas:</dt>
                    <dd className={resultado ? styles.negative : undefined}>
                        {resultado ? `- ${formatBRL(resultado.despesas)}` : '—'}
                    </dd>
                </div>
            </dl>

            <div className={saldoNegativo ? `${styles.saldo} ${styles.saldoNegativo}` : styles.saldo}>
                <span>Saldo:</span>
                <strong>{resultado ? formatBRL(resultado.saldo) : '—'}</strong>
            </div>

            {resultado?.aviso && <p className={styles.aviso}>{resultado.aviso}</p>}
            {!resultado && (
                <p className={styles.aviso}>Preencha os dados e clique em calcular para ver a estimativa.</p>
            )}
        </aside>
    );
}