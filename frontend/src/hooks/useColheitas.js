import { useCallback, useEffect, useState } from 'react';

import { colheitaService } from '../services/colheitaService.js';

// Lançamentos de colheita do usuário logado.
export default function useColheitas() {
    const [colheitas, setColheitas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [reloadKey, setReloadKey] = useState(0);

    useEffect(() => {
        const controller = new AbortController();
        setLoading(true);
        setError('');

        colheitaService
            .listar(controller.signal)
            .then(setColheitas)
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

    return { colheitas, loading, error, reload };
}