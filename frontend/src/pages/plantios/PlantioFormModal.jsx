import { useEffect, useState } from 'react';

import useForm from '../../hooks/useForm.js';
import { plantioService } from '../../services/plantioService.js';
import { STATUS_OPTIONS } from '../../utils/plantio.js';
import { validatePlantio } from '../../utils/validators.js';
import Icon from '../../components/Icons/Icon.jsx';
import styles from './PlantioFormModal.module.css';

function toFormValues(plantio) {
  return {
    nome: plantio?.nome ?? '',
    variedade: plantio?.variedade ?? '',
    area: plantio?.area ?? '',
    dataPlantio: plantio?.dataPlantio ?? '',
    status: plantio?.status ?? STATUS_OPTIONS[0].value,
  };
}

// `plantio` = null  -> criar | `plantio` = objeto -> editar
export default function PlantioFormModal({ plantio, onClose, onSaved }) {
  const isEditing = Boolean(plantio);
  const [submitError, setSubmitError] = useState('');
  const [saving, setSaving] = useState(false);
  const [confirmingDelete, setConfirmingDelete] = useState(false);

  const { values, errors, handleChange, handleBlur, handleSubmit } = useForm({
    initialValues: toFormValues(plantio),
    validate: validatePlantio,
  });

  useEffect(() => {
    function onKeyDown(event) {
      if (event.key === 'Escape' && !saving) onClose();
    }
    document.addEventListener('keydown', onKeyDown);
    return () => document.removeEventListener('keydown', onKeyDown);
  }, [onClose, saving]);

  async function runRequest(request) {
    setSaving(true);
    setSubmitError('');
    try {
      await request();
      onSaved();
    } catch (error) {
      setSubmitError(error.message);
      setSaving(false);
    }
  }

  const onSubmit = handleSubmit((formValues) =>
    runRequest(() =>
      isEditing
        ? plantioService.atualizar(plantio.id, formValues)
        : plantioService.criar(formValues),
    ),
  );

  function handleDelete() {
    runRequest(() => plantioService.deletar(plantio.id));
  }

  return (
    <div
      className={styles.overlay}
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !saving) onClose();
      }}
    >
      <div className={styles.modal} role="dialog" aria-modal="true" aria-labelledby="plantio-modal-title">
        <header className={styles.header}>
          <h2 id="plantio-modal-title" className={styles.title}>
            {isEditing ? 'Editar plantio' : 'Novo plantio'}
          </h2>
          <button type="button" className={styles.close} onClick={onClose} aria-label="Fechar">
            <Icon name="close" size={16} />
          </button>
        </header>

        <form className={styles.form} onSubmit={onSubmit} noValidate>
          <div className={styles.field}>
            <label htmlFor="nome">Nome do lote</label>
            <input
              id="nome"
              name="nome"
              value={values.nome}
              onChange={handleChange}
              onBlur={handleBlur}
              aria-invalid={Boolean(errors.nome)}
              placeholder="Ex.: Lote 10A"
              autoFocus
            />
            {errors.nome && <span className={styles.error}>{errors.nome}</span>}
          </div>

          <div className={styles.field}>
            <label htmlFor="variedade">Variedade</label>
            <input
              id="variedade"
              name="variedade"
              value={values.variedade}
              onChange={handleChange}
              onBlur={handleBlur}
              placeholder="Ex.: Arroz Agulhinha"
            />
          </div>

          <div className={styles.twoColumns}>
            <div className={styles.field}>
              <label htmlFor="area">Área total (ha)</label>
              <input
                id="area"
                name="area"
                inputMode="decimal"
                value={values.area}
                onChange={handleChange}
                onBlur={handleBlur}
                aria-invalid={Boolean(errors.area)}
                placeholder="120"
              />
              {errors.area && <span className={styles.error}>{errors.area}</span>}
            </div>

            <div className={styles.field}>
              <label htmlFor="dataPlantio">Data de plantio</label>
              <input
                id="dataPlantio"
                name="dataPlantio"
                type="date"
                value={values.dataPlantio}
                onChange={handleChange}
                onBlur={handleBlur}
                aria-invalid={Boolean(errors.dataPlantio)}
              />
              {errors.dataPlantio && <span className={styles.error}>{errors.dataPlantio}</span>}
            </div>
          </div>

          <div className={styles.field}>
            <label htmlFor="status">Status</label>
            <select
              id="status"
              name="status"
              value={values.status}
              onChange={handleChange}
              onBlur={handleBlur}
              aria-invalid={Boolean(errors.status)}
            >
              {STATUS_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
            {errors.status && <span className={styles.error}>{errors.status}</span>}
          </div>

          {submitError && (
            <p className={styles.submitError} role="alert">
              {submitError}
            </p>
          )}

          {confirmingDelete && (
            <p className={styles.deleteWarning} role="alert">
              Excluir este plantio também apaga os custos e as colheitas ligados a ele. Essa ação não
              pode ser desfeita.
            </p>
          )}

          <footer className={styles.actions}>
            {isEditing && !confirmingDelete && (
              <button
                type="button"
                className={styles.dangerGhost}
                onClick={() => setConfirmingDelete(true)}
                disabled={saving}
              >
                Excluir
              </button>
            )}
            {isEditing && confirmingDelete && (
              <button type="button" className={styles.danger} onClick={handleDelete} disabled={saving}>
                {saving ? 'Excluindo...' : 'Confirmar exclusão'}
              </button>
            )}

            <span className={styles.spacer} />

            <button type="button" className={styles.secondary} onClick={onClose} disabled={saving}>
              Cancelar
            </button>
            <button type="submit" className={styles.primary} disabled={saving}>
              {saving && !confirmingDelete ? 'Salvando...' : 'Salvar'}
            </button>
          </footer>
        </form>
      </div>
    </div>
  );
}
