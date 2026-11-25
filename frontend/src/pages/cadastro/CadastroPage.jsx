import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';

import Icon from '../../components/icons/Icon.jsx';
import { useAuth } from '../../context/AuthContext.jsx';
import useForm from '../../hooks/useForm.js';
import { authService } from '../../services/authService.js';
import { validateCadastro } from '../../utils/validators.js';
import TextField from '../login/TextField.jsx';
import styles from './CadastroPage.module.css';

const INITIAL_VALUES = { nome: '', email: '', senha: '' };

export default function CadastroPage() {
  const { isAuthenticated, login } = useAuth();
  const navigate = useNavigate();
  const [submitError, setSubmitError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { values, errors, handleChange, handleBlur, handleSubmit } = useForm({
    initialValues: INITIAL_VALUES,
    validate: validateCadastro,
  });

  // Quem já está autenticado (ou acabou de se cadastrar) não deve ver o formulário.
  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  function handleFieldChange(event) {
    if (submitError) setSubmitError('');
    handleChange(event);
  }

  const onSubmit = handleSubmit(async ({ nome, email, senha }) => {
    if (isSubmitting) return;

    setSubmitError('');
    setIsSubmitting(true);

    try {
      await authService.cadastrar({ nome: nome.trim(), email: email.trim(), senha });
    } catch (error) {
      setSubmitError(
        error instanceof TypeError
          ? 'Não foi possível conectar ao servidor. Tente novamente.'
          : error.message
      );
      setIsSubmitting(false);
      return;
    }

    // Cadastro feito: entra automaticamente. Se o login falhar, segue para a tela de login.
    try {
      await login(email.trim(), senha);
    } catch {
      navigate('/login', { replace: true });
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

        <h1 className={styles.title}>Cadastro</h1>
        <p className={styles.subtitle}>Cadastre suas informações para começar.</p>

        <form className={styles.form} onSubmit={onSubmit} noValidate>
          <TextField
            label="Nome de usuário"
            name="nome"
            type="text"
            autoComplete="username"
            value={values.nome}
            error={errors.nome}
            onChange={handleFieldChange}
            onBlur={handleBlur}
          />
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
            autoComplete="new-password"
            value={values.senha}
            error={errors.senha}b
            onChange={handleFieldChange}
            onBlur={handleBlur}
          />

          <p className={styles.feedback} role="alert">
            {submitError}
          </p>

          <button type="submit" className={styles.submit} disabled={isSubmitting}>
            {isSubmitting ? 'Cadastrando...' : 'Cadastrar-se'}
          </button>
        </form>

        <p className={styles.signin}>
          Já tem uma conta?{' '}
          <Link to="/login" className={styles.signinLink}>
            Entrar
          </Link>
        </p>
      </main>

      <p className={styles.copyright}>
        Copyright © {new Date().getFullYear()}, SIGA, Todos os direitos reservados.
      </p>
    </div>
  );
}