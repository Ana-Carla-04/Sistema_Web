import { useCallback, useMemo, useState } from 'react';

// Hook de formulário feito à mão.
// `validate` deve ser uma função estável (declarada fora do componente)
// que recebe os valores e devolve um objeto { campo: 'mensagem' }.
export default function useForm({ initialValues, validate }) {
  const [values, setValues] = useState(initialValues);
  const [touched, setTouched] = useState({});

  const allErrors = useMemo(() => validate(values), [validate, values]);

  const handleChange = useCallback((event) => {
    const { name, value } = event.target;
    setValues((current) => ({ ...current, [name]: value }));
  }, []);

  const handleBlur = useCallback((event) => {
    const { name } = event.target;
    setTouched((current) => ({ ...current, [name]: true }));
  }, []);

  // Só mostra o erro de campos que o usuário já tocou (ou após tentar enviar).
  const errors = useMemo(
    () => Object.fromEntries(Object.entries(allErrors).filter(([field]) => touched[field])),
    [allErrors, touched],
  );

  const handleSubmit = useCallback(
    (onValid) => async (event) => {
      event.preventDefault();
      setTouched(Object.fromEntries(Object.keys(values).map((field) => [field, true])));
      if (Object.keys(allErrors).length > 0) return;
      await onValid(values);
    },
    [allErrors, values],
  );

  return { values, errors, handleChange, handleBlur, handleSubmit };
}
