import { useCallback, useEffect, useMemo, useState } from 'react';

import Icon from '../../components/icons/Icon.jsx';
import useDebounce from '../../hooks/useDebounce.js';
import usePlantios from '../../hooks/usePlantios.js';
import useTarefas from '../../hooks/useTarefas.js';
import { tarefaService } from '../../services/tarefaService.js';
import { formatDate } from '../../utils/plantio.js';
import {
  FILTER_OPTIONS,
  PRIORIDADE_OPTIONS,
  STATUS_OPTIONS,
  countByStatus,
  getLabel,
  getTarefaStatus,
  matchesSearch,
  sortByPrazo,
} from '../../utils/tarefa.js';
import TarefaFormModal from './TarefaFormModal.jsx';
import styles from './TarefasPage.module.css';

// Quantidade de linhas antes de clicar em "Ver todas".
const PAGE_SIZE = 8;

const STATUS_CLASS = {
  PENDENTE: styles.statusPendente,
  EM_ANDAMENTO: styles.statusAndamento,
  CONCLUIDA: styles.statusConcluida,
};

const PRIORIDADE_CLASS = {
  PRIORITARIA: styles.prioPrioritaria,
  AGENDADO: styles.prioAgendado,
  PLANEJADO: styles.prioPlanejado,
};

function PencilIcon() {
  return (
    <svg viewBox="0 0 24 24" width="17" height="17" fill="none" aria-hidden="true">
      <path d="M4 20l1-4L16.5 4.5a2.1 2.1 0 0 1 3 3L8 19l-4 1Z" fill="#f2b441" />
      <path d="M14.5 6.5l3 3" stroke="#c8861b" strokeWidth="1.6" strokeLinecap="round" />
      <path d="M4 20l1-4 3 3-4 1Z" fill="#e8c9a0" />
    </svg>
  );
}

function TrashIcon() {
  return (
    <svg
      viewBox="0 0 24 24"
      width="17"
      height="17"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.7"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <path d="M4 7h16 M9 7V4h6v3 M6 7l1 13h10l1-13 M10 11v6 M14 11v6" />
    </svg>
  );
}

// Confirmação antes de excluir. Fica na frente da tela, como o pop-up do formulário.
function DeleteDialog({ tarefa, onCancel, onDeleted }) {
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    function onKeyDown(event) {
      if (event.key === 'Escape' && !deleting) onCancel();
    }
    document.addEventListener('keydown', onKeyDown);
    return () => document.removeEventListener('keydown', onKeyDown);
  }, [onCancel, deleting]);

  async function handleConfirm() {
    setDeleting(true);
    setError('');
    try {
      await tarefaService.deletar(tarefa.id);
      onDeleted();
    } catch (err) {
      setError(err.message);
      setDeleting(false);
    }
  }

  return (
    <div
      className={styles.overlay}
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !deleting) onCancel();
      }}
    >
      <div
        className={styles.dialog}
        role="alertdialog"
        aria-modal="true"
        aria-labelledby="excluir-tarefa-title"
        aria-describedby="excluir-tarefa-text"
      >
        <h2 id="excluir-tarefa-title" className={styles.dialogTitle}>
          Excluir tarefa
        </h2>
        <p id="excluir-tarefa-text" className={styles.dialogText}>
          Tem certeza que deseja excluir <strong>{tarefa.titulo}</strong>? Essa ação não
          pode ser desfeita.
        </p>

        {error && (
          <p className={styles.dialogError} role="alert">
            {error}
          </p>
        )}

        <div className={styles.dialogActions}>
          <button
            type="button"
            className={styles.cancel}
            onClick={onCancel}
            disabled={deleting}
          >
            Cancelar
          </button>
          <button
            type="button"
            className={styles.danger}
            onClick={handleConfirm}
            disabled={deleting}
            autoFocus
          >
            {deleting ? 'Excluindo...' : 'Excluir'}
          </button>
        </div>
      </div>
    </div>
  );
}

export default function TarefasPage() {
  const [filter, setFilter] = useState('');
  const [search, setSearch] = useState('');
  const [showAll, setShowAll] = useState(false);
  // null = fechado | { tarefa: null } = adicionar | { tarefa } = editar
  const [modal, setModal] = useState(null);
  const [toDelete, setToDelete] = useState(null);
  const [actionError, setActionError] = useState('');

  const debouncedSearch = useDebounce(search, 300);
  const { tarefas, loading, error, reload } = useTarefas();
  const { plantios } = usePlantios({});

  const counts = useMemo(() => countByStatus(tarefas), [tarefas]);

  const filtered = useMemo(
    () =>
      sortByPrazo(tarefas).filter(
        (tarefa) =>
          (!filter || getTarefaStatus(tarefa) === filter) &&
          matchesSearch(tarefa, debouncedSearch)
      ),
    [tarefas, filter, debouncedSearch]
  );

  const visible = showAll ? filtered : filtered.slice(0, PAGE_SIZE);
  const initialLoading = loading && tarefas.length === 0;

  const closeModal = useCallback(() => setModal(null), []);
  const closeDelete = useCallback(() => setToDelete(null), []);

  const handleSaved = useCallback(() => {
    setModal(null);
    reload();
  }, [reload]);

  const handleDeleted = useCallback(() => {
    setToDelete(null);
    reload();
  }, [reload]);

  async function handleConcluir(tarefa) {
    setActionError('');
    try {
      await tarefaService.concluir(tarefa.id);
      reload();
    } catch (err) {
      setActionError(err.message);
    }
  }

  const summary = [
    {
      key: 'total',
      label: 'Total de Tarefas',
      value: counts.total,
      className: styles.cardTotal,
    },
    {
      key: 'pend',
      label: 'Pendentes',
      value: counts.PENDENTE,
      className: styles.cardPendente,
    },
    {
      key: 'and',
      label: 'Em Andamento',
      value: counts.EM_ANDAMENTO,
      className: styles.cardAndamento,
    },
    {
      key: 'conc',
      label: 'Concluídas',
      value: counts.CONCLUIDA,
      className: styles.cardConcluida,
    },
  ];

  return (
    <section>
      <h1 className={styles.title}>Tarefas</h1>

      <div className={styles.toolbar}>
        <div className={styles.chips} role="group" aria-label="Filtrar por status">
          {FILTER_OPTIONS.map((option) => (
            <button
              key={option.value || 'todas'}
              type="button"
              className={
                filter === option.value
                  ? `${styles.chip} ${styles.chipActive}`
                  : styles.chip
              }
              aria-pressed={filter === option.value}
              onClick={() => {
                setFilter(option.value);
                setShowAll(false);
              }}
            >
              {option.label}
            </button>
          ))}
        </div>

        <div className={styles.toolbarRight}>
          <label className={styles.search}>
            <Icon name="search" size={14} />
            <input
              type="search"
              value={search}
              onChange={(event) => {
                setSearch(event.target.value);
                setShowAll(false);
              }}
              placeholder="Buscar tarefa..."
              aria-label="Buscar tarefa"
            />
          </label>

          <button
            type="button"
            className={styles.newButton}
            onClick={() => setModal({ tarefa: null })}
          >
            + Nova Tarefa
          </button>
        </div>
      </div>

      <div className={styles.summary}>
        {summary.map((item) => (
          <div key={item.key} className={`${styles.summaryCard} ${item.className}`}>
            <span className={styles.summaryLabel}>{item.label}</span>
            <strong className={styles.summaryValue}>{item.value}</strong>
          </div>
        ))}
      </div>

      {actionError && (
        <p className={styles.actionError} role="alert">
          {actionError}
        </p>
      )}

      <div className={styles.tableCard}>
        {initialLoading && <p className={styles.feedback}>Carregando tarefas...</p>}

        {!initialLoading && error && (
          <div className={styles.feedback} role="alert">
            <p>{error}</p>
            <button type="button" className={styles.retry} onClick={reload}>
              Tentar novamente
            </button>
          </div>
        )}

        {!initialLoading && !error && filtered.length === 0 && (
          <div className={styles.feedback}>
            <p>
              {tarefas.length === 0
                ? 'Você ainda não tem tarefas.'
                : 'Nenhuma tarefa encontrada para esses filtros.'}
            </p>
            {tarefas.length === 0 && (
              <button
                type="button"
                className={styles.retry}
                onClick={() => setModal({ tarefa: null })}
              >
                Adicionar tarefa
              </button>
            )}
          </div>
        )}

        {!initialLoading && !error && filtered.length > 0 && (
          <>
            <div className={styles.scroll}>
              <table className={styles.table}>
                <colgroup>
                  <col className={styles.colTarefa} />
                  <col className={styles.colLote} />
                  <col className={styles.colPrazo} />
                  <col className={styles.colPrioridade} />
                  <col className={styles.colStatus} />
                  <col className={styles.colAcoes} />
                </colgroup>
                <thead>
                  <tr>
                    <th scope="col">Tarefa</th>
                    <th scope="col">Lote</th>
                    <th scope="col">Prazo</th>
                    <th scope="col">Prioridade</th>
                    <th scope="col">Status</th>
                    <th scope="col">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {visible.map((tarefa) => {
                    const status = getTarefaStatus(tarefa);
                    const concluida = status === 'CONCLUIDA';
                    return (
                      <tr key={tarefa.id}>
                        <td>
                          <label className={styles.taskCell}>
                            <input
                              type="checkbox"
                              className={styles.check}
                              checked={concluida}
                              disabled={concluida}
                              onChange={() => handleConcluir(tarefa)}
                              aria-label={`Concluir tarefa ${tarefa.titulo}`}
                            />
                            <span
                              className={concluida ? styles.taskDone : styles.taskName}
                            >
                              {tarefa.titulo}
                            </span>
                          </label>
                        </td>
                        <td className={`${styles.muted} ${styles.clip}`}>
                          {tarefa.plantioNome || '—'}
                        </td>
                        <td className={styles.muted}>{formatDate(tarefa.dataLimite)}</td>
                        <td>
                          {tarefa.prioridade ? (
                            <span
                              className={`${styles.pill} ${PRIORIDADE_CLASS[tarefa.prioridade]}`}
                            >
                              {getLabel(PRIORIDADE_OPTIONS, tarefa.prioridade)}
                            </span>
                          ) : (
                            <span className={styles.muted}>—</span>
                          )}
                        </td>
                        <td>
                          <span className={`${styles.pill} ${STATUS_CLASS[status]}`}>
                            {getLabel(STATUS_OPTIONS, status)}
                          </span>
                        </td>
                        <td>
                          <div className={styles.actions}>
                            <button
                              type="button"
                              className={styles.iconButton}
                              onClick={() => setModal({ tarefa })}
                              aria-label={`Editar tarefa ${tarefa.titulo}`}
                              title="Editar"
                            >
                              <PencilIcon />
                            </button>
                            <button
                              type="button"
                              className={`${styles.iconButton} ${styles.trash}`}
                              onClick={() => setToDelete(tarefa)}
                              aria-label={`Excluir tarefa ${tarefa.titulo}`}
                              title="Excluir"
                            >
                              <TrashIcon />
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>

            <footer className={styles.footer}>
              <span className={styles.muted}>
                Mostrando {visible.length} de {filtered.length} tarefas
              </span>
              {filtered.length > PAGE_SIZE && (
                <button
                  type="button"
                  className={styles.seeAll}
                  onClick={() => setShowAll((v) => !v)}
                >
                  {showAll ? 'Ver menos' : 'Ver todas →'}
                </button>
              )}
            </footer>
          </>
        )}
      </div>

      {modal && (
        <TarefaFormModal
          key={modal.tarefa?.id ?? 'nova'}
          tarefa={modal.tarefa}
          plantios={plantios}
          onClose={closeModal}
          onSaved={handleSaved}
        />
      )}

      {toDelete && (
        <DeleteDialog
          tarefa={toDelete}
          onCancel={closeDelete}
          onDeleted={handleDeleted}
        />
      )}
    </section>
  );
}