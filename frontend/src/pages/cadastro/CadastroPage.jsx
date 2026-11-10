import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import Icon from '../../components/icons/Icon.jsx';
import { useAuth } from '../../context/AuthContext.jsx';
import useForm from '../../hooks/useForm.js';
import { authService } from '../../services/authService.js';
import { validateCadastro } from '../../utils/validators.js';
import styles from '../login/LoginPage.module.css';
import TextField from '../login/TextField.jsx';

const INITIAL_VALUES = { nome: '', email: '', senha: '' };

export default function CadastroPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [submitError, setSubmitError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { values, errors, handleChange, handleBlur, handleSubmit } = useForm({
    initialValues: INITIAL_VALUES,
    validate: validateCadastro,
  });

  function handleFieldChange(event) {
    if (submitError) setSubmitError('');
    handleChange(event);
  }

  const onSubmit = handleSubmit(async ({ nome, email, senha }) => {
    if (isSubmitting) return;

    setSubmitError('');
    setIsSubmitting(true);
    try {
      await authService.cadastrar({ nome, email, senha });
      // Realiza o login automaticamente após cadastrar
      await login(email, senha);
      navigate('/dashboard', { replace: true });
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

        <h1 className={styles.title}>Criar conta</h1>
        <p className={styles.subtitle}>Preencha seus dados para acessar o SIPA.</p>

        <form className={styles.form} onSubmit={onSubmit} noValidate>
          <TextField
            label="Nome completo"
            name="nome"
            type="text"
            autoComplete="name"
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
            label="Senha (mínimo 6 caracteres)"
            name="senha"
            type="password"
            autoComplete="new-password"
            value={values.senha}
            error={errors.senha}
            onChange={handleFieldChange}
            onBlur={handleBlur}
          />

          <p className={styles.feedback} role="alert">
            {submitError}
          </p>

          <button type="submit" className={styles.submit} disabled={isSubmitting}>
            {isSubmitting ? 'Cadastrando...' : 'Cadastrar'}
          </button>
        </form>

        <p className={styles.signup}>
          Já possui uma conta?{' '}
          <Link to="/login" className={styles.signupLink}>
            Fazer login
          </Link>
        </p>
      </main>
    </div>
  );
}
