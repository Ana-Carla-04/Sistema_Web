import { useEffect, useState } from 'react';

import useForm from '../../hooks/useForm.js';
import { tarefaService } from '../../services/tarefaService.js';
import {
  PRIORIDADE_OPTIONS,
  STATUS_OPTIONS,
  getTarefaStatus,
  isoToBR,
  maskDate,
  validateTarefa,
} from '../../utils/tarefa.js';
import styles from './TarefaFormModal.module.css';

function toFormValues(tarefa) {
  return {
    titulo: tarefa?.titulo ?? '',
    plantioId: tarefa ? String(tarefa.plantioId) : '',
    prazo: isoToBR(tarefa?.dataLimite),
    prioridade: tarefa?.prioridade ?? '',
    descricao: tarefa?.descricao ?? '',
    responsavel: tarefa?.responsavel ?? '',
    status: tarefa ? getTarefaStatus(tarefa) : '',
  };
}

// `tarefa` = null -> adicionar | `tarefa` = objeto -> editar
export default function TarefaFormModal({ tarefa, plantios, onClose, onSaved }) {
  const isEditing = Boolean(tarefa);
  const [submitError, setSubmitError] = useState('');
  const [saving, setSaving] = useState(false);

  const { values, errors, handleChange, handleBlur, handleSubmit } = useForm({
    initialValues: toFormValues(tarefa),
    validate: validateTarefa,
  });

  useEffect(() => {
    function onKeyDown(event) {
      if (event.key === 'Escape' && !saving) onClose();
    }
    document.addEventListener('keydown', onKeyDown);
    return () => document.removeEventListener('keydown', onKeyDown);
  }, [onClose, saving]);

  function handleFieldChange(event) {
    if (submitError) setSubmitError('');
    handleChange(event);
  }

  function handlePrazoChange(event) {
    handleFieldChange({ target: { name: 'prazo', value: maskDate(event.target.value) } });
  }

  const onSubmit = handleSubmit(async (formValues) => {
    setSaving(true);
    setSubmitError('');
    try {
      if (isEditing) {
        await tarefaService.atualizar(tarefa.id, formValues);
      } else {
        await tarefaService.criar(formValues.plantioId, formValues);
      }
      onSaved();
    } catch (error) {
      setSubmitError(error.message);
      setSaving(false);
    }
  });

  // Na edição o lote não muda (o back-end não permite trocar o plantio da tarefa).
  const loteOptions =
    isEditing && !plantios.some((plantio) => String(plantio.id) === values.plantioId)
      ? [{ id: tarefa.plantioId, nome: tarefa.plantioNome }, ...plantios]
      : plantios;

  const title = isEditing ? 'Editar Tarefa' : 'Adicionar Nova Tarefa';

  return (
    <div
      className={styles.overlay}
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !saving) onClose();
      }}
    >
      <div
        className={styles.modal}
        role="dialog"
        aria-modal="true"
        aria-labelledby="tarefa-modal-title"
      >
        <button
          type="button"
          className={styles.close}
          onClick={onClose}
          disabled={saving}
          aria-label="Fechar"
        >
          X
        </button>

        <h2 id="tarefa-modal-title" className={styles.title}>
          {title}
        </h2>

        <form className={styles.form} onSubmit={onSubmit} noValidate>
          <div className={styles.field}>
            <label htmlFor="titulo" className={styles.label}>
              Nome da Tarefa
            </label>
            <input
              id="titulo"
              name="titulo"
              className={styles.control}
              value={values.titulo}
              onChange={handleFieldChange}
              onBlur={handleBlur}
              aria-invalid={Boolean(errors.titulo)}
              placeholder="Ex: Aplicar fertilizante no Lote 1A"
              autoComplete="off"
              autoFocus
            />
            {errors.titulo && <span className={styles.error}>{errors.titulo}</span>}
          </div>

          <div className={styles.threeColumns}>
            <div className={styles.field}>
              <label htmlFor="plantioId" className={styles.label}>
                Lote
              </label>
              <select
                id="plantioId"
                name="plantioId"
                className={`${styles.control} ${styles.select} ${
                  values.plantioId ? '' : styles.empty
                }`}
                value={values.plantioId}
                onChange={handleFieldChange}
                onBlur={handleBlur}
                aria-invalid={Boolean(errors.plantioId)}
                disabled={isEditing}
              >
                <option value="">Selecionar lote ▾</option>
                {loteOptions.map((plantio) => (
                  <option key={plantio.id} value={String(plantio.id)}>
                    {plantio.nome}
                  </option>
                ))}
              </select>
              {errors.plantioId && (
                <span className={styles.error}>{errors.plantioId}</span>
              )}
              {!isEditing && plantios.length === 0 && (
                <span className={styles.hint}>
                  Cadastre um plantio antes de criar tarefas.
                </span>
              )}
            </div>

            <div className={styles.field}>
              <label htmlFor="prazo" className={styles.label}>
                Prazo
              </label>
              <input
                id="prazo"
                name="prazo"
                className={styles.control}
                inputMode="numeric"
                maxLength={10}
                value={values.prazo}
                onChange={handlePrazoChange}
                onBlur={handleBlur}
                aria-invalid={Boolean(errors.prazo)}
                placeholder="dd/mm/aaaa"
                autoComplete="off"
              />
              {errors.prazo && <span className={styles.error}>{errors.prazo}</span>}
            </div>

            <div className={styles.field}>
              <label htmlFor="prioridade" className={styles.label}>
                Prioridade
              </label>
              <select
                id="prioridade"
                name="prioridade"
                className={`${styles.control} ${styles.select} ${
                  values.prioridade ? '' : styles.empty
                }`}
                value={values.prioridade}
                onChange={handleFieldChange}
                onBlur={handleBlur}
              >
                <option value="">Selecionar ▾</option>
                {PRIORIDADE_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className={styles.field}>
            <label htmlFor="descricao" className={styles.label}>
              Descrição
            </label>
            <textarea
              id="descricao"
              name="descricao"
              className={`${styles.control} ${styles.textarea}`}
              value={values.descricao}
              onChange={handleFieldChange}
              onBlur={handleBlur}
              placeholder="Descreva a tarefa em detalhes..."
            />
          </div>

          <div className={`${styles.field} ${styles.responsavel}`}>
            <label htmlFor="responsavel" className={styles.label}>
              Responsável
            </label>
            <input
              id="responsavel"
              name="responsavel"
              className={styles.control}
              value={values.responsavel}
              onChange={handleFieldChange}
              onBlur={handleBlur}
              aria-invalid={Boolean(errors.responsavel)}
              placeholder="Nome do responsável"
              autoComplete="off"
            />
            {errors.responsavel && (
              <span className={styles.error}>{errors.responsavel}</span>
            )}
          </div>

          <div className={`${styles.field} ${styles.status}`}>
            <label htmlFor="status" className={styles.label}>
              Status
            </label>
            <select
              id="status"
              name="status"
              className={`${styles.control} ${styles.select} ${values.status ? '' : styles.empty}`}
              value={values.status}
              onChange={handleFieldChange}
              onBlur={handleBlur}
            >
              <option value="">Selecionar ▾</option>
              {STATUS_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>

          {submitError && (
            <p className={styles.submitError} role="alert">
              {submitError}
            </p>
          )}

          <footer className={styles.actions}>
            <button
              type="button"
              className={styles.cancel}
              onClick={onClose}
              disabled={saving}
            >
              Cancelar
            </button>
            <button type="submit" className={styles.save} disabled={saving}>
              {saving ? 'Salvando...' : 'Salvar Tarefa'}
            </button>
          </footer>
        </form>
      </div>
    </div>
  );
}