import { formatBRL, formatKg } from '../../utils/colheita.js';
import styles from './colheitasRecentes.module.css';

const LIMITE = 10;

// Adicionei o onDeletar nas props
export default function ColheitasRecentes({ colheitas, loading, error, onRetry, onDeletar }) {
    return (
        <section className={styles.card}>
            <h2 className={styles.title}>Lançamentos de Colheitas Recentes</h2>

            {loading && <p className={styles.feedback}>Carregando lançamentos...</p>}

            {!loading && error && (
                <div className={styles.feedback} role="alert">
                    <p>{error}</p>
                    <button type="button" className={styles.retry} onClick={onRetry}>
                        Tentar novamente
                    </button>
                </div>
            )}

            {!loading && !error && colheitas.length === 0 && (
                <p className={styles.feedback}>Nenhuma colheita lançada ainda.</p>
            )}

            {!loading && !error && colheitas.length > 0 && (
                <div className={styles.tableWrap}>
                    <table className={styles.table}>
                        <thead>
                        <tr>
                            <th scope="col">Lote</th>
                            <th scope="col">Produção Bruta</th>
                            <th scope="col">Preço de Venda por Alqueire</th>
                            <th scope="col" className={styles.right}>
                                Receita Líquida
                            </th>
                            {/* 👇 Nova coluna para as ações */}
                            <th scope="col" className={styles.right} style={{ width: '60px' }}>
                                Ações
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        {colheitas.slice(0, LIMITE).map((colheita) => (
                            <tr key={colheita.id}>
                                <th scope="row">{colheita.lote}</th>
                                <td>{formatKg(colheita.producaoBruta)}</td>
                                {/* 👇 Corrigido para precoAlqueire */}
                                <td>{formatBRL(colheita.precoAlqueire)}</td>
                                <td className={`${styles.right} ${styles.receita}`}>
                                    {formatBRL(colheita.receitaLiquida)}
                                </td>
                                {/* 👇 Novo botão de excluir */}
                                <td className={styles.right}>
                                    <button
                                        type="button"
                                        onClick={() => onDeletar(colheita.id)}
                                        title="Excluir lançamento"
                                        style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'red', fontSize: '1.2rem' }}
                                    >
                                        {/* Se você tiver o ícone de lixeira no seu componente Icon, use <Icon name="trash" size={16} />. Senão, use o emoji ou texto: */}
                                        🗑️
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </section>
    );
}