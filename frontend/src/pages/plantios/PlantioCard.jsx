import { formatArea, formatDate, getStatusLabel } from '../../utils/plantio.js';
import styles from './PlantioCard.module.css';

// 1. Importando as imagens .svg da pasta src/assets (caminho correto!)
import img1 from '../../assets/plantio1.svg';
import img2 from '../../assets/plantio2.svg';
import img3 from '../../assets/plantio3.svg';

const STATUS_CLASS = {
  PLANTADO: styles.statusPlantado,
  CRESCENDO: styles.statusCrescendo,
  COLHIDO: styles.statusColhido,
};

// 2. Nome da variável padronizado
const FIELD_IMAGES = [img1, img2, img3];

export default function PlantioCard({ plantio, onEdit }) {
  const statusClass = STATUS_CLASS[String(plantio.status).toUpperCase()] ?? styles.statusPlantado;

  // 3. Usando o nome correto da variável (FIELD_IMAGES)
  const backgroundImage = FIELD_IMAGES[plantio.id % FIELD_IMAGES.length];

  return (
      <article className={styles.card}>
        <div
            className={styles.field}
            role="img"
            aria-label={`Lote ${plantio.nome}`}
            style={{
              backgroundImage: `url(${backgroundImage})`,
              backgroundSize: 'cover',
              backgroundPosition: 'center'
            }}
        />

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