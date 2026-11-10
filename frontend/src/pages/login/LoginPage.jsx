import { useState } from 'react';
import { Link, Navigate, useLocation } from 'react-router-dom';

import Icon from '../../components/icons/Icon.jsx';
import { useAuth } from '../../context/AuthContext.jsx';
import useForm from '../../hooks/useForm.js';
import { validateLogin } from '../../utils/validators.js';
import styles from './LoginPage.module.css';
import TextField from './TextField.jsx';

const INITIAL_VALUES = { email: '', senha: '' };

// Depois de entrar, o usuário vai para o Dashboard, a não ser que tenha sido barrado
// por uma rota privada: nesse caso volta para a tela que tentava acessar.
const DEFAULT_REDIRECT = '/dashboard';

export default function LoginPage() {
  const { isAuthenticated, login } = useAuth();
  const location = useLocation();
  const [submitError, setSubmitError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { values, errors, handleChange, handleBlur, handleSubmit } = useForm({
    initialValues: INITIAL_VALUES,
    validate: validateLogin,
  });

  // Quem já está autenticado (ou acabou de entrar) não deve ver o formulário.
  if (isAuthenticated) {
    const redirectTo = location.state?.from?.pathname ?? DEFAULT_REDIRECT;
    return <Navigate to={redirectTo} replace />;
  }

  function handleFieldChange(event) {
    if (submitError) setSubmitError('');
    handleChange(event);
  }

  const onSubmit = handleSubmit(async ({ email, senha }) => {
    if (isSubmitting) return;

    setSubmitError('');
    setIsSubmitting(true);
    try {
      await login(email, senha);
    } catch (error) {
      setSubmitError(error.message);
    } finally {
      setIsSubmitting(false);
    }
  });

  return (
    <div className={styles.page}>
      <main className={styles.panel}>
        <div className={styles.logo}>
          <Icon name="leaf" strokeWidth={2.8} className={styles.logoIcon} />
        </div>

        <h1 className={styles.title}>Fazer login</h1>
        <p className={styles.subtitle}>Insira seu e-mail e senha para entrar.</p>

        <form className={styles.form} onSubmit={onSubmit} noValidate>
          <TextField
            label="E-mail"
            name="email"
            type="email"
            inputMode="email"
            autoComplete="email"
            value={values.email}
            error={errors.email}
            onChange={handleFieldChange}
            onBlur={handleBlur}
          />
          <TextField
            label="Senha"
            name="senha"
            type="password"
            autoComplete="current-password"
            value={values.senha}
            error={errors.senha}
            onChange={handleFieldChange}
            onBlur={handleBlur}
          />

          <p className={styles.feedback} role="alert">
            {submitError}
          </p>

          <button type="submit" className={styles.submit} disabled={isSubmitting}>
            {isSubmitting ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        <p className={styles.signup}>
          Ainda não tem uma conta?{' '}
          <Link to="/cadastro" className={styles.signupLink}>
            Cadastrar
          </Link>
        </p>
      </main>

      <p className={styles.copyright}>
        Copyright © {new Date().getFullYear()}, SIGA, Todos os direitos reservados.
      </p>
    </div>
  );
}
