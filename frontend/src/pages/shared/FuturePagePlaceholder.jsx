import { useNavigate, useSearchParams } from 'react-router-dom';
import styles from './FuturePagePlaceholder.module.css';

export default function FuturePagePlaceholder({ title, subtitle, endpoint }) {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const queryEntries = Array.from(searchParams.entries());

  return (
    <section className={styles.wrapper}>
      <header className={styles.header}>
        <div>
          <h1 className={styles.title}>{title}</h1>
          <p className={styles.subtitle}>{subtitle}</p>
        </div>
        <button
          type="button"
          className={styles.backBtn}
          onClick={() => navigate('/dashboard')}
        >
          ← Voltar ao Dashboard
        </button>
      </header>

      <div className={styles.card}>
        <p className={styles.info}>
          Esta página já está conectada às rotas e botões do <strong>Dashboard</strong> e do menu lateral.
          {endpoint && (
            <>
              {' '}
              Endpoint do backend: <code>{endpoint}</code>.
            </>
          )}
        </p>

        {queryEntries.length > 0 && (
          <div className={styles.filterBox}>
            <span className={styles.filterTitle}>Filtro recebido pelo clique no Dashboard:</span>
            <div className={styles.tags}>
              {queryEntries.map(([key, val]) => (
                <span key={key} className={styles.tag}>
                  {key}: <strong>{val}</strong>
                </span>
              ))}
            </div>
          </div>
        )}
      </div>
    </section>
  );
}
