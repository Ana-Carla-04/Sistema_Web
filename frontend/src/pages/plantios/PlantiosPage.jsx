import { useCallback, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';

import Icon from '../../components/icons/Icon.jsx';
import useDebounce from '../../hooks/useDebounce.js';
import usePlantios from '../../hooks/usePlantios.js';
import { STATUS_OPTIONS, buildSafraOptions, getCurrentSafraStartYear, isInSafra } from '../../utils/plantio.js';
import PlantioCard from './PlantioCard.jsx';
import PlantioFormModal from './PlantioFormModal.jsx';
import styles from './PlantiosPage.module.css';

const SAFRA_OPTIONS = buildSafraOptions(4);
const ALL_SAFRAS = 'todas';

export default function PlantiosPage() {
  const [searchParams] = useSearchParams();
  const initialQuery = searchParams.get('lote') || searchParams.get('busca') || '';
  const [search, setSearch] = useState(initialQuery);
  const [safra, setSafra] = useState(String(getCurrentSafraStartYear()));
  const [status, setStatus] = useState('');
  // null = fechado | { plantio: null } = novo | { plantio } = editar
  const [modal, setModal] = useState(null);

  const debouncedSearch = useDebounce(search.trim(), 400);
  const { plantios, loading, error, reload } = usePlantios({
    nome: debouncedSearch,
    status,
  });

  // A API não filtra por safra, então filtramos aqui pela data de plantio.
  const visiblePlantios = useMemo(
    () =>
      safra === ALL_SAFRAS
        ? plantios
        : plantios.filter((plantio) => isInSafra(plantio.dataPlantio, safra)),
    [plantios, safra],
  );

  const closeModal = useCallback(() => setModal(null), []);

  const handleSaved = useCallback(() => {
    setModal(null);
    reload();
  }, [reload]);

  return (
    <section>
      <header className={styles.header}>
        <h1 className={styles.title}>Meus Plantios</h1>
        <button type="button" className={styles.newButton} onClick={() => setModal({ plantio: null })}>
          <Icon name="plus" size={16} />
          Novo Plantio
        </button>
      </header>

      <div className={styles.filters}>
        <label className={styles.search}>
          <Icon name="search" size={14} />
          <input
            type="search"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Buscar lote..."
            aria-label="Buscar lote"
          />
        </label>

        <div className={styles.select}>
          <select value={safra} onChange={(event) => setSafra(event.target.value)} aria-label="Safra">
            {SAFRA_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
            <option value={ALL_SAFRAS}>Safra: Todas</option>
          </select>
          <Icon name="chevron" size={14} />
        </div>

        <div className={styles.select}>
          <select value={status} onChange={(event) => setStatus(event.target.value)} aria-label="Status">
            <option value="">Status: Todos</option>
            {STATUS_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                Status: {option.label}
              </option>
            ))}
          </select>
          <Icon name="chevron" size={14} />
        </div>
      </div>

      {loading && <p className={styles.feedback}>Carregando plantios...</p>}

      {!loading && error && (
        <div className={styles.feedback} role="alert">
          <p>{error}</p>
          <button type="button" className={styles.retry} onClick={reload}>
            Tentar novamente
          </button>
        </div>
      )}

      {!loading && !error && visiblePlantios.length === 0 && (
        <div className={styles.feedback}>
          <p>Nenhum plantio encontrado para esses filtros.</p>
          <button type="button" className={styles.retry} onClick={() => setModal({ plantio: null })}>
            Cadastrar plantio
          </button>
        </div>
      )}

      {!loading && !error && visiblePlantios.length > 0 && (
        <div className={styles.grid}>
          {visiblePlantios.map((plantio) => (
            <PlantioCard key={plantio.id} plantio={plantio} onEdit={(item) => setModal({ plantio: item })} />
          ))}
        </div>
      )}

      {modal && (
        <PlantioFormModal
          key={modal.plantio?.id ?? 'novo'}
          plantio={modal.plantio}
          onClose={closeModal}
          onSaved={handleSaved}
        />
      )}
    </section>
  );
}
