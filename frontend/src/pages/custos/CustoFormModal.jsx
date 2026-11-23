import { useEffect, useState } from 'react';

import Icon from '../../components/icons/Icon.jsx';
import useForm from '../../hooks/useForm.js';
import { custoService } from '../../services/custoService.js';
import { CATEGORIA_OPTIONS, formatValorInput, validateCusto } from '../../utils/custo.js';
import styles from './CustoFormModal.module.css';

function toFormValues(custo) {
  return {
    plantioId: custo ? String(custo.plantioId) : '',
    categoria: custo?.categoria ?? '',
    descricao: custo?.descricao ?? '',
    valor: formatValorInput(custo?.valor),
    comprovante: custo?.comprovante ?? '',
  };
}

// `custo` = null -> adicionar | `custo` = objeto -> editar
export default function CustoFormModal({ custo, plantios, onClose, onSaved }) {
  const isEditing = Boolean(custo);
  const [submitError, setSubmitError] = useState('');
  const [saving, setSaving] = useState(false);

  const { values, errors, handleChange, handleBlur, handleSubmit } = useForm({
    initialValues: toFormValues(custo),
    validate: validateCusto,
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

  // O valor aceita só dígitos, ponto e vírgula (ex.: 2.000,00).
  function handleValorChange(event) {
    handleFieldChange({
      target: { name: 'valor', value: event.target.value.replace(/[^\d.,]/g, '') },
    });
  }

  // Só o nome do arquivo é guardado (o back-end ainda não recebe o arquivo em si).
  function handleFileChange(event) {
    const file = event.target.files?.[0];
    if (file) handleFieldChange({ target: { name: 'comprovante', value: file.name } });
  }

  const onSubmit = handleSubmit(async (formValues) => {
    setSaving(true);
    setSubmitError('');
    try {
      if (isEditing) {
        await custoService.atualizar(custo.id, formValues, custo.data);
      } else {
        await custoService.criar(formValues.plantioId, formValues);
      }
      onSaved();
    } catch (error) {
      setSubmitError(error.message);
      setSaving(false);
    }
  });

  // Na edição o lote não muda (o back-end não permite trocar o plantio do custo).
  const loteOptions =
    isEditing && !plantios.some((plantio) => String(plantio.id) === values.plantioId)
      ? [{ id: custo.plantioId, nome: `Plantio #${custo.plantioId}` }, ...plantios]
      : plantios;

  // Categoria antiga que não está na lista padrão continua aparecendo na edição.
  const categoriaOptions =
    values.categoria &&
    !CATEGORIA_OPTIONS.some((option) => option.value === values.categoria)
      ? [{ value: values.categoria, label: values.categoria }, ...CATEGORIA_OPTIONS]
      : CATEGORIA_OPTIONS;

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
        aria-labelledby="custo-modal-title"
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

        <h2 id="custo-modal-title" className={styles.title}>
          {isEditing ? 'Edite o custo:' : 'Registre um custo:'}
        </h2>

        <form onSubmit={onSubmit} noValidate>
          <div className={styles.grid}>
            <div className={styles.field}>
              <label htmlFor="plantioId" className={styles.label}>
                Lote Selecionado
              </label>
              <div className={styles.selectWrap}>
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
                  <option value="">Selecione o lote</option>
                  {loteOptions.map((plantio) => (
                    <option key={plantio.id} value={String(plantio.id)}>
                      {plantio.nome}
                    </option>
                  ))}
                </select>
                <Icon name="chevron" size={14} className={styles.chevron} />
              </div>
              {errors.plantioId && (
                <span className={styles.error}>{errors.plantioId}</span>
              )}
              {!isEditing && plantios.length === 0 && (
                <span className={styles.hint}>
                  Cadastre um plantio antes de registrar custos.
                </span>
              )}
            </div>

            <div className={styles.field}>
              <label htmlFor="categoria" className={styles.label}>
                Selecione a Categoria
              </label>
              <div className={styles.selectWrap}>
                <select
                  id="categoria"
                  name="categoria"
                  className={`${styles.control} ${styles.select} ${
                    values.categoria ? '' : styles.empty
                  }`}
                  value={values.categoria}
                  onChange={handleFieldChange}
                  onBlur={handleBlur}
                  aria-invalid={Boolean(errors.categoria)}
                >
                  <option value="">Selecione a categoria</option>
                  {categoriaOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
                <Icon name="chevron" size={14} className={styles.chevron} />
              </div>
              {errors.categoria && (
                <span className={styles.error}>{errors.categoria}</span>
              )}
            </div>

            <div className={styles.field}>
              <label htmlFor="descricao" className={styles.label}>
                Descrição
              </label>
              <input
                id="descricao"
                name="descricao"
                className={styles.control}
                value={values.descricao}
                onChange={handleFieldChange}
                onBlur={handleBlur}
                placeholder="Ex.: Adubo Fórmula Base"
                autoComplete="off"
              />
            </div>

            <div className={styles.field}>
              <label htmlFor="valor" className={styles.label}>
                Valor
              </label>
              <input
                id="valor"
                name="valor"
                className={styles.control}
                inputMode="decimal"
                value={values.valor}
                onChange={handleValorChange}
                onBlur={handleBlur}
                aria-invalid={Boolean(errors.valor)}
                placeholder="0,00"
                autoComplete="off"
              />
              {errors.valor && <span className={styles.error}>{errors.valor}</span>}
            </div>

            <div className={styles.field}>
              <label htmlFor="comprovante" className={styles.label}>
                Adicione o comprovante
              </label>
              <label className={styles.fileBox}>
                <span
                  className={
                    values.comprovante ? styles.fileName : styles.filePlaceholder
                  }
                >
                  {values.comprovante || 'adicionar arquivo'}
                </span>
                <input
                  id="comprovante"
                  type="file"
                  className={styles.fileInput}
                  accept=".pdf,.png,.jpg,.jpeg"
                  onChange={handleFileChange}
                />
              </label>
            </div>
          </div>

          {submitError && (
            <p className={styles.submitError} role="alert">
              {submitError}
            </p>
          )}

          <button type="submit" className={styles.save} disabled={saving}>
            {saving ? 'Salvando...' : 'Salvar Custo'}
          </button>
        </form>
      </div>
    </div>
  );
}
