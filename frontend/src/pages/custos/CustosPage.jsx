import { useCallback, useEffect, useMemo, useState } from 'react';
import { useOutletContext, useSearchParams } from 'react-router-dom';

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
  const [searchParams, setSearchParams] = useSearchParams();
  const categoriaParam = searchParams.get('categoria') || '';
  const plantioIdParam = searchParams.get('plantioId') || '';
  const novoParam = searchParams.get('novo') === 'true' || searchParams.get('adicionar') === 'true';

  // null = fechado | { custo: null, defaultPlantioId } = adicionar | { custo } = editar
  const [modal, setModal] = useState(null);
  const [toDelete, setToDelete] = useState(null);

  // A busca é a da barra superior ("Pesquisar no sistema...").
  const outletContext = useOutletContext() || {};
  const search = (outletContext.globalSearch || '').trim();

  const { custos, loading, error, reload } = useCustos();
  const { plantios } = usePlantios({});

  // Abre automaticamente o modal se a URL tiver ?novo=true
  useEffect(() => {
    if (novoParam) {
      setModal({ custo: null, defaultPlantioId: plantioIdParam });
    }
  }, [novoParam, plantioIdParam]);

  const loteNomes = useMemo(
    () => new Map(plantios.map((plantio) => [plantio.id, plantio.nome])),
    [plantios]
  );
  const getLoteNome = useCallback(
    (custo) => loteNomes.get(custo.plantioId) ?? `Plantio #${custo.plantioId}`,
    [loteNomes]
  );

  // Os cartões mostram o total de cada categoria no universo do produtor
  const categoriaTotals = useMemo(() => totalsByCategoria(custos), [custos]);

  // Filtra por busca textual, categoria vinda de URL/clique, e plantioId
  const visible = useMemo(() => {
    let list = sortByDataDesc(custos);

    if (search) {
      list = list.filter((custo) =>
        matchesSearch(custo, getLoteNome(custo), search)
      );
    }

    if (categoriaParam) {
      list = list.filter(
        (custo) =>
          String(custo.categoria || '').toLowerCase().trim() ===
          categoriaParam.toLowerCase().trim()
      );
    }

    if (plantioIdParam) {
      list = list.filter((custo) => String(custo.plantioId) === String(plantioIdParam));
    }

    return list;
  }, [custos, getLoteNome, search, categoriaParam, plantioIdParam]);

  const total = useMemo(() => sumValores(visible), [visible]);
  const initialLoading = loading && custos.length === 0;

  // Alterna o filtro de categoria ao clicar nos cartões de resumo
  const handleCategoryClick = useCallback(
    (catValue) => {
      const next = new URLSearchParams(searchParams);
      if (categoriaParam.toLowerCase().trim() === catValue.toLowerCase().trim()) {
        next.delete('categoria');
      } else {
        next.set('categoria', catValue);
      }
      setSearchParams(next);
    },
    [categoriaParam, searchParams, setSearchParams]
  );

  // Limpa um filtro específico
  const removeFilter = useCallback(
    (key) => {
      const next = new URLSearchParams(searchParams);
      next.delete(key);
      setSearchParams(next);
    },
    [searchParams, setSearchParams]
  );

  // Limpa todos os filtros da URL
  const clearAllFilters = useCallback(() => {
    const next = new URLSearchParams();
    setSearchParams(next);
  }, [setSearchParams]);

  const closeModal = useCallback(() => {
    setModal(null);
    if (novoParam) {
      const next = new URLSearchParams(searchParams);
      next.delete('novo');
      next.delete('adicionar');
      setSearchParams(next, { replace: true });
    }
  }, [novoParam, searchParams, setSearchParams]);

  const closeDelete = useCallback(() => setToDelete(null), []);

  const handleSaved = useCallback(() => {
    closeModal();
    reload();
  }, [closeModal, reload]);

  const handleDeleted = useCallback(() => {
    setToDelete(null);
    reload();
  }, [reload]);

  const nomeLoteFiltro = plantioIdParam
    ? loteNomes.get(Number(plantioIdParam)) || `Lote #${plantioIdParam}`
    : '';

  return (
    <section>
      <header className={styles.header}>
        <h1 className={styles.title}>Custos</h1>
        <button
          type="button"
          className={styles.newButton}
          onClick={() => setModal({ custo: null, defaultPlantioId: plantioIdParam })}
        >
          <span aria-hidden="true">+</span> Adicionar Custo
        </button>
      </header>

      {/* Cartões de resumo por categoria (interativos: clicar filtra a listagem) */}
      <div className={styles.summary}>
        {categoriaTotals.map((item) => {
          const isActive =
            categoriaParam.toLowerCase().trim() === item.value.toLowerCase().trim();
          return (
            <div
              key={item.value}
              className={`${styles.summaryCard} ${styles.summaryCardInteractive} ${
                isActive ? styles.summaryCardActive : ''
              }`}
              onClick={() => handleCategoryClick(item.value)}
              role="button"
              tabIndex={0}
              onKeyDown={(e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                  e.preventDefault();
                  handleCategoryClick(item.value);
                }
              }}
              title={
                isActive
                  ? `Filtro ativo: ${item.label} (clique para remover)`
                  : `Filtrar tabela por ${item.label}`
              }
              aria-pressed={isActive}
            >
              <span className={`${styles.summaryLabel} ${CATEGORIA_CLASS[item.value]}`}>
                {item.label}
              </span>
              <strong className={styles.summaryValue}>{formatCurrency(item.total)}</strong>
            </div>
          );
        })}
      </div>

      <div className={styles.tableCard}>
        <div className={styles.tableCardHeader}>
          <h2 className={styles.tableTitle}>Detalhamento de Gastos</h2>

          {/* Barra de filtros ativos conectados com Dashboard / Plantios / Categorias */}
          {(categoriaParam || plantioIdParam) && (
            <div className={styles.activeFilters}>
              <span className={styles.activeFiltersLabel}>Filtros ativos:</span>
              {categoriaParam && (
                <span className={styles.filterChip}>
                  Categoria: <strong>{categoriaParam}</strong>
                  <button
                    type="button"
                    className={styles.filterChipRemove}
                    onClick={() => removeFilter('categoria')}
                    aria-label="Remover filtro de categoria"
                    title="Remover filtro de categoria"
                  >
                    ✕
                  </button>
                </span>
              )}
              {plantioIdParam && (
                <span className={styles.filterChip}>
                  Lote: <strong>{nomeLoteFiltro}</strong>
                  <button
                    type="button"
                    className={styles.filterChipRemove}
                    onClick={() => removeFilter('plantioId')}
                    aria-label="Remover filtro de lote"
                    title="Remover filtro de lote"
                  >
                    ✕
                  </button>
                </span>
              )}
              <button
                type="button"
                className={styles.clearAllBtn}
                onClick={clearAllFilters}
              >
                Limpar filtros
              </button>
            </div>
          )}
        </div>

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
                : categoriaParam || plantioIdParam || search
                ? `Nenhum custo encontrado para os filtros aplicados.`
                : `Nenhum custo encontrado.`}
            </p>
            {custos.length === 0 ? (
              <button
                type="button"
                className={styles.retry}
                onClick={() => setModal({ custo: null, defaultPlantioId: plantioIdParam })}
              >
                Adicionar custo
              </button>
            ) : (categoriaParam || plantioIdParam || search) ? (
              <button
                type="button"
                className={styles.retry}
                onClick={clearAllFilters}
              >
                Limpar filtros
              </button>
            ) : null}
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
            {(search || categoriaParam || plantioIdParam) && (
              <span className={styles.totalNote}> (filtrado)</span>
            )}
          </p>
        )}
      </div>

      {modal && (
        <CustoFormModal
          key={modal.custo?.id ?? 'novo'}
          custo={modal.custo}
          defaultPlantioId={modal.defaultPlantioId}
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
