import { formatArea, formatDate, getStatusLabel } from '../../utils/plantio.js';
import styles from './PlantioCard.module.css';

const STATUS_CLASS = {
  PLANTADO: styles.statusPlantado,
  CRESCENDO: styles.statusCrescendo,
  COLHIDO: styles.statusColhido,
};

// Variações do "campo visto de cima" usadas como imagem do lote.
const FIELD_VARIANTS = [styles.fieldA, styles.fieldB, styles.fieldC];

export default function PlantioCard({ plantio, onEdit }) {
  const statusClass = STATUS_CLASS[String(plantio.status).toUpperCase()] ?? styles.statusPlantado;
  const fieldClass = FIELD_VARIANTS[plantio.id % FIELD_VARIANTS.length];

  return (
    <article className={styles.card}>
      <div className={`${styles.field} ${fieldClass}`} role="img" aria-label={`Lote ${plantio.nome}`} />

      <div className={styles.body}>
        <header className={styles.header}>
          <h2 className={styles.title}>{plantio.nome}</h2>
          <span className={`${styles.badge} ${statusClass}`}>{getStatusLabel(plantio.status)}</span>
        </header>

        <dl className={styles.details}>
          <div className={styles.row}>
            <dt>Variedade:</dt>
            <dd>{plantio.variedade || '—'}</dd>
          </div>
          <div className={styles.row}>
            <dt>Área Total:</dt>
            <dd>{formatArea(plantio.area)}</dd>
          </div>
          <div className={styles.row}>
            <dt>Data de Plantio:</dt>
            <dd>{formatDate(plantio.dataPlantio)}</dd>
          </div>
        </dl>

        <button type="button" className={styles.editButton} onClick={() => onEdit(plantio)}>
          Editar
        </button>
      </div>
    </article>
  );
}
