import { useCallback, useEffect, useState } from 'react';

import { tarefaService } from '../services/tarefaService.js';

// Busca as tarefas do usuário logado. Busca e filtros são feitos na própria tela.
export default function useTarefas() {
  const [tarefas, setTarefas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    setError('');

    tarefaService
      .listar(controller.signal)
      .then((data) => setTarefas(data ?? []))
      .catch((err) => {
        if (err.name === 'AbortError') return;
        setError(err.message);
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false);
      });

    return () => controller.abort();
  }, [reloadKey]);

  const reload = useCallback(() => setReloadKey((key) => key + 1), []);

  return { tarefas, loading, error, reload };
}