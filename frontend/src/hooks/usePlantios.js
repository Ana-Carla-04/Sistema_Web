import { useCallback, useEffect, useState } from 'react';

import { plantioService } from '../services/plantioService.js';

// Busca os plantios do usuário logado. `nome` e `status` são filtrados no back-end.
export default function usePlantios({ nome, status }) {
  const [plantios, setPlantios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    setError('');

    plantioService
      .listar({ nome: nome || undefined, status: status || undefined }, controller.signal)
      .then((data) => setPlantios(data ?? []))
      .catch((err) => {
        if (err.name === 'AbortError') return;
        setError(err.message);
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false);
      });

    return () => controller.abort();
  }, [nome, status, reloadKey]);

  const reload = useCallback(() => setReloadKey((key) => key + 1), []);

  return { plantios, loading, error, reload };
}
