import { useCallback, useEffect, useState } from 'react';

import { custoService } from '../services/custoService.js';

// Busca os custos do usuário logado. Busca e totais são feitos na própria tela.
export default function useCustos() {
  const [custos, setCustos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    setError('');

    custoService
      .listar(controller.signal)
      .then((data) => setCustos(data ?? []))
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

  return { custos, loading, error, reload };
}
