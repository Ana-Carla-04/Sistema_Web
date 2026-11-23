import { useCallback, useEffect, useMemo, useState } from 'react';
import { useOutletContext } from 'react-router-dom';

import useCustos from '../../hooks/useCustos.js';
import usePlantios from '../../hooks/usePlantios.js';
import { custoService } from '../../services/custoService.js';
import {
  formatCurrency,
  formatCurrencyFull,
  matchesSearch,
  sortByDataDesc,
  sumValores,
  totalsByCategoria,
} from '../../utils/custo.js';
import { formatDate } from '../../utils/plantio.js';
import CustoFormModal from './CustoFormModal.jsx';
import styles from './CustosPage.module.css';

const CATEGORIA_CLASS = {
  Fertilizantes: styles.catFertilizantes,
  Energia: styles.catEnergia,
  Defensivos: styles.catDefensivos,
  'Preparo do Solo': styles.catPreparo,
};

function FileIcon() {
  return (
    <svg
      viewBox="0 0 24 24"
      width="14"
      height="14"
      fill="currentColor"
      aria-hidden="true"
    >
      <path d="M6 2h8l5 5v13a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2Z" />
      <path d="M14 2v5h5" fill="#ffffff" fillOpacity="0.45" />
      <path d="M8 12h8M8 15h8M8 18h5" stroke="#ffffff" strokeWidth="1.5" />
    </svg>
  );
}

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

// Comprovante: o back-end guarda só o nome do arquivo. Se for um endereço (http), vira link.
function Comprovante({ value }) {
  if (!value) return <span className={styles.muted}>—</span>;

  const content = (
    <>
      <FileIcon />
      <span className={styles.fileLabel}>{value}</span>
    </>
  );

  if (/^https?:\/\//i.test(value)) {
    return (
      <a className={styles.file} href={value} target="_blank" rel="noreferrer">
        {content}
      </a>
    );
  }
  return <span className={styles.file}>{content}</span>;
}

// Confirmação antes de excluir. Fica na frente da tela, como o pop-up do formulário.
function DeleteDialog({ custo, loteNome, onCancel, onDeleted }) {
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
      await custoService.deletar(custo.id);
      onDeleted();
    } catch (err) {
      setError(err.message);
      setDeleting(false);
    }
  }

  const resumo = [custo.descricao || custo.categoria, formatCurrency(custo.valor)].join(
    ' — '
  );

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
        aria-labelledby="excluir-custo-title"
        aria-describedby="excluir-custo-text"
      >
        <h2 id="excluir-custo-title" className={styles.dialogTitle}>
          Excluir custo
        </h2>
        <p id="excluir-custo-text" className={styles.dialogText}>
          Tem certeza que deseja excluir <strong>{resumo}</strong> ({loteNome})? Essa ação
          não pode ser desfeita.
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

export default function CustosPage() {
  // null = fechado | { custo: null } = adicionar | { custo } = editar
  const [modal, setModal] = useState(null);
  const [toDelete, setToDelete] = useState(null);

  // A busca é a da barra superior ("Pesquisar no sistema...").
  const outletContext = useOutletContext() || {};
  const search = (outletContext.globalSearch || '').trim();

  const { custos, loading, error, reload } = useCustos();
  const { plantios } = usePlantios({});

  const loteNomes = useMemo(
    () => new Map(plantios.map((plantio) => [plantio.id, plantio.nome])),
    [plantios]
  );
  const getLoteNome = useCallback(
    (custo) => loteNomes.get(custo.plantioId) ?? `Plantio #${custo.plantioId}`,
    [loteNomes]
  );

  // Os cartões mostram o total de cada categoria; a tabela e o "Custo Total" seguem a busca.
  const categoriaTotals = useMemo(() => totalsByCategoria(custos), [custos]);

  const visible = useMemo(
    () =>
      sortByDataDesc(custos).filter((custo) =>
        matchesSearch(custo, getLoteNome(custo), search)
      ),
    [custos, getLoteNome, search]
  );
  const total = useMemo(() => sumValores(visible), [visible]);

  const initialLoading = loading && custos.length === 0;

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

  return (
    <section>
      <header className={styles.header}>
        <h1 className={styles.title}>Custos</h1>
        <button
          type="button"
          className={styles.newButton}
          onClick={() => setModal({ custo: null })}
        >
          <span aria-hidden="true">+</span> Adicionar Custo
        </button>
      </header>

      <div className={styles.summary}>
        {categoriaTotals.map((item) => (
          <div key={item.value} className={styles.summaryCard}>
            <span className={`${styles.summaryLabel} ${CATEGORIA_CLASS[item.value]}`}>
              {item.label}
            </span>
            <strong className={styles.summaryValue}>{formatCurrency(item.total)}</strong>
          </div>
        ))}
      </div>

      <div className={styles.tableCard}>
        <h2 className={styles.tableTitle}>Detalhamento de Gastos</h2>

        {initialLoading && <p className={styles.feedback}>Carregando custos...</p>}

        {!initialLoading && error && (
          <div className={styles.feedback} role="alert">
            <p>{error}</p>
            <button type="button" className={styles.retry} onClick={reload}>
              Tentar novamente
            </button>
          </div>
        )}

        {!initialLoading && !error && visible.length === 0 && (
          <div className={styles.feedback}>
            <p>
              {custos.length === 0
                ? 'Você ainda não registrou custos.'
                : `Nenhum custo encontrado para "${search}".`}
            </p>
            {custos.length === 0 && (
              <button
                type="button"
                className={styles.retry}
                onClick={() => setModal({ custo: null })}
              >
                Adicionar custo
              </button>
            )}
          </div>
        )}

        {!initialLoading && !error && visible.length > 0 && (
          <div className={styles.scroll}>
            <table className={styles.table}>
              <colgroup>
                <col className={styles.colData} />
                <col className={styles.colLote} />
                <col className={styles.colCategoria} />
                <col className={styles.colDescricao} />
                <col className={styles.colValor} />
                <col className={styles.colComprovante} />
                <col className={styles.colAcoes} />
              </colgroup>
              <thead>
                <tr>
                  <th scope="col">Data</th>
                  <th scope="col">Lote</th>
                  <th scope="col">Categoria</th>
                  <th scope="col">Descrição</th>
                  <th scope="col">Valor</th>
                  <th scope="col" className={styles.alignRight}>
                    Comprovante
                  </th>
                  <th scope="col">
                    <span className={styles.srOnly}>Ações</span>
                  </th>
                </tr>
              </thead>
              <tbody>
                {visible.map((custo) => {
                  const loteNome = getLoteNome(custo);
                  const label = custo.descricao || custo.categoria;
                  return (
                    <tr key={custo.id}>
                      <td className={styles.muted}>{formatDate(custo.data)}</td>
                      <td className={`${styles.strong} ${styles.clip}`}>{loteNome}</td>
                      <td
                        className={`${styles.categoria} ${CATEGORIA_CLASS[custo.categoria] ?? ''}`}
                      >
                        {custo.categoria}
                      </td>
                      <td className={styles.clip}>{custo.descricao || '—'}</td>
                      <td className={styles.strong}>{formatCurrency(custo.valor)}</td>
                      <td className={styles.alignRight}>
                        <Comprovante value={custo.comprovante} />
                      </td>
                      <td>
                        <div className={styles.actions}>
                          <button
                            type="button"
                            className={styles.iconButton}
                            onClick={() => setModal({ custo })}
                            aria-label={`Editar custo ${label}`}
                            title="Editar"
                          >
                            <PencilIcon />
                          </button>
                          <button
                            type="button"
                            className={`${styles.iconButton} ${styles.trash}`}
                            onClick={() => setToDelete(custo)}
                            aria-label={`Excluir custo ${label}`}
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
        )}

        {!initialLoading && !error && (
          <p className={styles.total}>
            Custo Total: <strong>{formatCurrencyFull(total)}</strong>
            {search && <span className={styles.totalNote}> (resultado da busca)</span>}
          </p>
        )}
      </div>

      {modal && (
        <CustoFormModal
          key={modal.custo?.id ?? 'novo'}
          custo={modal.custo}
          plantios={plantios}
          onClose={closeModal}
          onSaved={handleSaved}
        />
      )}

      {toDelete && (
        <DeleteDialog
          custo={toDelete}
          loteNome={getLoteNome(toDelete)}
          onCancel={closeDelete}
          onDeleted={handleDeleted}
        />
      )}
    </section>
  );
}
