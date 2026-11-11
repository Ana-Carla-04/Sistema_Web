import { useId } from 'react';

import styles from './TextField.module.css';

// Campo de texto controlado. O rótulo fica só para leitores de tela (o visual usa
// o placeholder) e o espaço da mensagem de erro é sempre reservado, para o layout
// não "pular" quando o erro aparece.
export default function TextField({
  label,
  name,
  type = 'text',
  value,
  error,
  onChange,
  onBlur,
  ...rest
}) {
  const id = useId();
  const errorId = `${id}-error`;
  const inputClassName = error ? `${styles.input} ${styles.inputInvalid}` : styles.input;

  return (
    <div className={styles.field}>
      <label htmlFor={id} className={styles.label}>
        {label}
      </label>
      <input
        id={id}
        className={inputClassName}
        name={name}
        type={type}
        placeholder={label}
        value={value}
        onChange={onChange}
        onBlur={onBlur}
        aria-invalid={error ? 'true' : undefined}
        aria-describedby={error ? errorId : undefined}
        {...rest}
      />
      <p id={errorId} className={styles.error}>
        {error}
      </p>
    </div>
  );
}
