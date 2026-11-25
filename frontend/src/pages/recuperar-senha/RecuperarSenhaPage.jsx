import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';

import Icon from '../../components/icons/Icon.jsx';
import { useAuth } from '../../context/AuthContext.jsx';
import useForm from '../../hooks/useForm.js';
import { authService } from '../../services/authService.js';
import {
  evaluatePasswordStrength,
  validateForgotPassword,
  validateRecuperarSenha,
} from '../../utils/validators.js';
import TextField from '../login/TextField.jsx';
import styles from './RecuperarSenhaPage.module.css';

const STEP1_INITIAL = { email: '' };
const STEP2_INITIAL = { novaSenha: '', confirmarSenha: '' };

export default function RecuperarSenhaPage() {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  // Controla as 2 etapas do fluxo seguro: 'request' (solicitação por e-mail) | 'reset' (token validado) | 'completed'
  const [step, setStep] = useState('request');
  const [activeToken, setActiveToken] = useState('');
  const [csrfToken, setCsrfToken] = useState('');
  const [maskedEmail, setMaskedEmail] = useState('');
  const [expiresInSeconds, setExpiresInSeconds] = useState(0);
  const [validatingToken, setValidatingToken] = useState(false);

  // Estado anti-bot (Captcha exigido após 3 falhas)
  const [captchaRequired, setCaptchaRequired] = useState(false);
  const [captchaChecked, setCaptchaChecked] = useState(false);
  const [failedAttempts, setFailedAttempts] = useState(0);

  // Feedbacks e preview de e-mail para ambiente local de desenvolvimento
  const [submitError, setSubmitError] = useState('');
  const [submitSuccess, setSubmitSuccess] = useState('');
  const [sandboxEmail, setSandboxEmail] = useState(null);
  const [securityAlert, setSecurityAlert] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const requestForm = useForm({
    initialValues: STEP1_INITIAL,
    validate: validateForgotPassword,
  });

  const resetForm = useForm({
    initialValues: STEP2_INITIAL,
    validate: validateRecuperarSenha,
  });

  const passwordStrength = useMemo(
    () => evaluatePasswordStrength(resetForm.values.novaSenha),
    [resetForm.values.novaSenha],
  );

  // Valida um token extraído do fragmento da URL (#token=...) e limpa a URL imediatamente
  const consumeAndValidateToken = useCallback(async (rawToken) => {
    if (!rawToken) return;
    setValidatingToken(true);
    setSubmitError('');
    setSubmitSuccess('');

    try {
      const validation = await authService.validarTokenRecuperacao(rawToken);
      setActiveToken(rawToken);
      setCsrfToken(validation.csrfToken || '');
      setMaskedEmail(validation.maskedEmail || '');
      setExpiresInSeconds(validation.expiresInSeconds || 1200);
      setCaptchaRequired(Boolean(validation.captchaRequired));
      setStep('reset');
    } catch (err) {
      setStep('request');
      setActiveToken('');
      setCsrfToken('');
      if (err.captchaRequired) setCaptchaRequired(true);
      setSubmitError(err.message || 'O link de recuperação é inválido ou expirou.');
    } finally {
      setValidatingToken(false);
    }
  }, []);

  // Lê o token do URL Fragment (#token=...) e limpa da barra de endereços (OWASP ASVS: token nunca persistido na URL)
  useEffect(() => {
    function checkHashToken() {
      const hash = window.location.hash || '';
      if (hash.startsWith('#token=')) {
        const extractedToken = decodeURIComponent(hash.slice('#token='.length).trim());
        // Remove imediatamente o fragmento da URL para não ficar no histórico do navegador
        window.history.replaceState(null, '', window.location.pathname);
        if (extractedToken) {
          consumeAndValidateToken(extractedToken);
        }
      }
    }

    checkHashToken();
    window.addEventListener('hashchange', checkHashToken);
    return () => window.removeEventListener('hashchange', checkHashToken);
  }, [consumeAndValidateToken]);

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  function handleStep1Change(event) {
    if (submitError) setSubmitError('');
    if (submitSuccess) setSubmitSuccess('');
    requestForm.handleChange(event);
  }

  function handleStep2Change(event) {
    if (submitError) setSubmitError('');
    resetForm.handleChange(event);
  }

  // Etapa 1: Solicitar link tokenizado por e-mail
  const onRequestSubmit = requestForm.handleSubmit(async ({ email }) => {
    if (isSubmitting) return;

    setSubmitError('');
    setSubmitSuccess('');
    setSandboxEmail(null);
    setIsSubmitting(true);

    try {
      const response = await authService.solicitarRecuperacaoSenha(email.trim().toLowerCase());
      setSubmitSuccess(
        response.message ||
          'Se o endereço estiver cadastrado, enviamos um link seguro de recuperação (válido por 20 minutos).',
      );
      if (response.sandboxEmail) {
        setSandboxEmail(response.sandboxEmail);
      }
    } catch (error) {
      setSubmitError(
        error instanceof TypeError
          ? 'Não foi possível conectar ao servidor. Tente novamente.'
          : error.message || 'Erro ao solicitar recuperação de senha.',
      );
    } finally {
      setIsSubmitting(false);
    }
  });

  // Abre o link tokenizado a partir da caixa de entrada simulada (ambiente local)
  function handleOpenSandboxLink(resetLink) {
    if (!resetLink) return;
    const hashIndex = resetLink.indexOf('#token=');
    if (hashIndex !== -1) {
      const rawToken = resetLink.slice(hashIndex + '#token='.length).trim();
      setSandboxEmail(null);
      consumeAndValidateToken(rawToken);
    }

  }

  // Etapa 2: Redefinir senha com token de uso único + CSRF + validação HIBP
  const onResetSubmit = resetForm.handleSubmit(async ({ novaSenha, confirmarSenha }) => {
    if (isSubmitting) return;

    if (captchaRequired && !captchaChecked) {
      setSubmitError('Confirme a verificação de segurança anti-bot (Captcha) antes de prosseguir.');
      return;
    }

    setSubmitError('');
    setIsSubmitting(true);

    try {
      const result = await authService.redefinirSenhaComToken({
        token: activeToken,
        password: novaSenha,
        passwordConfirm: confirmarSenha,
        csrf: csrfToken,
        captchaToken: captchaRequired && captchaChecked ? 'HUMAN_VERIFIED' : undefined,
      });

      setSubmitSuccess(
        result.message ||
          'Senha redefinida com sucesso! Todas as sessões ativas foram encerradas por segurança.',
      );
      setSecurityAlert(result.securityAlert || null);
      setStep('completed');

      setTimeout(() => {
        navigate('/login', { replace: true });
      }, 3200);
    } catch (error) {
      const nextFailures = failedAttempts + 1;
      setFailedAttempts(nextFailures);
      if (nextFailures >= 3 || String(error.message).toLowerCase().includes('captcha')) {
        setCaptchaRequired(true);
      }
      setSubmitError(
        error instanceof TypeError
          ? 'Não foi possível conectar ao servidor. Tente novamente.'
          : error.message || 'Erro ao redefinir senha.',
      );
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

        <h1 className={styles.title}>Recuperar senha</h1>

        {/* Indicador de etapas do protocolo de segurança */}
        <div className={styles.stepIndicator} aria-label="Etapas de recuperação">
          <span className={`${styles.stepDot} ${step === 'request' ? styles.stepDotActive : styles.stepDotDone}`}>
            1. Solicitação
          </span>
          <span className={styles.stepDivider}>→</span>
          <span className={`${styles.stepDot} ${step === 'reset' || step === 'completed' ? styles.stepDotActive : ''}`}>
            2. Redefinição Tokenizada
          </span>
        </div>

        {validatingToken ? (
          <div className={styles.validatingBox} role="status">
            Validando token criptográfico em tempo constante...
          </div>
        ) : step === 'request' ? (
          <>
            <p className={styles.subtitle}>
              Informe seu e-mail cadastrado para receber um link tokenizado de uso único (expira em 20 minutos).
            </p>

            <form className={styles.form} onSubmit={onRequestSubmit} noValidate>
              <TextField
                label="E-mail da conta"
                name="email"
                type="email"
                inputMode="email"
                autoComplete="email"
                value={requestForm.values.email}
                error={requestForm.errors.email}
                onChange={handleStep1Change}
                onBlur={requestForm.handleBlur}
              />

              {submitSuccess && (
                <div className={styles.successMessage} role="status">
                  {submitSuccess}
                </div>
              )}

              {sandboxEmail && (
                <div className={styles.sandboxBox} role="region" aria-label="E-mail de recuperação gerado">
                  <div className={styles.sandboxHeader}>
                    <Icon name="shield" size={15} />
                    <strong>E-mail de Recuperação Gerado (Sandbox Local)</strong>
                  </div>
                  <p className={styles.sandboxText}>
                    Token CSPRNG (256 bits / SHA-256) emitido às {sandboxEmail.timestamp}.
                  </p>
                  <button
                    type="button"
                    className={styles.sandboxLinkBtn}
                    onClick={() => handleOpenSandboxLink(sandboxEmail.resetLink)}
                  >
                    Abrir Link Tokenizado de Redefinição
                  </button>
                </div>
              )}

              <p className={styles.feedback} role="alert">
                {submitError}
              </p>

              <button
                type="submit"
                className={styles.submit}
                disabled={isSubmitting}
              >
                {isSubmitting ? 'Enviando link...' : 'Enviar link de recuperação'}
              </button>
            </form>
          </>
        ) : step === 'reset' ? (
          <>
            <p className={styles.subtitle}>
              Token verificado para <strong>{maskedEmail || 'sua conta'}</strong>. Defina sua nova senha segura.
            </p>

            <div className={styles.tokenBadge}>
              <Icon name="shield" size={14} />
              <span>Token SHA-256 Válido • Uso Único • Expira em {Math.ceil(expiresInSeconds / 60)} min</span>
            </div>

            <form className={styles.form} onSubmit={onResetSubmit} noValidate>
              <TextField
                label="Nova senha forte"
                name="novaSenha"
                type="password"
                autoComplete="new-password"
                value={resetForm.values.novaSenha}
                error={resetForm.errors.novaSenha}
                onChange={handleStep2Change}
                onBlur={resetForm.handleBlur}
              />

              {/* Checklist de requisitos de senha simplificado */}
              <ul className={styles.strengthChecklist} aria-label="Requisitos de força da senha">
                <li className={passwordStrength.minLength ? styles.ruleMet : styles.rulePending}>
                  {passwordStrength.minLength ? '✓' : '○'} Mínimo de 6 caracteres
                </li>
                <li className={passwordStrength.onlyAlphanumeric ? styles.ruleMet : styles.rulePending}>
                  {passwordStrength.onlyAlphanumeric ? '✓' : '○'} Apenas números e letras
                </li>
                <li className={passwordStrength.hasLetter && passwordStrength.hasDigit ? styles.ruleMet : styles.rulePending}>
                  {passwordStrength.hasLetter && passwordStrength.hasDigit ? '✓' : '○'} Conter ao menos uma letra e um número
                </li>
              </ul>

              <TextField
                label="Confirmar nova senha"
                name="confirmarSenha"
                type="password"
                autoComplete="new-password"
                value={resetForm.values.confirmarSenha}
                error={resetForm.errors.confirmarSenha}
                onChange={handleStep2Change}
                onBlur={resetForm.handleBlur}
              />

              {captchaRequired && (
                <label className={styles.captchaBox}>
                  <input
                    type="checkbox"
                    checked={captchaChecked}
                    onChange={(e) => setCaptchaChecked(e.target.checked)}
                  />
                  <span>Sou humano (Verificação Anti-Bot hCaptcha/Challenge)</span>
                </label>
              )}

              <p className={styles.feedback} role="alert">
                {submitError}
              </p>

              <button
                type="submit"
                className={styles.submit}
                disabled={isSubmitting}
              >
                {isSubmitting ? 'Validando e Redefinindo...' : 'Redefinir senha'}
              </button>

              <button
                type="button"
                className={styles.secondaryAction}
                onClick={() => {
                  setStep('request');
                  setActiveToken('');
                  setCsrfToken('');
                  setSubmitError('');
                }}
              >
                Solicitar outro link
              </button>
            </form>
          </>
        ) : (
          <div className={styles.completedWrap}>
            <div className={styles.successMessage} role="status">
              {submitSuccess}
            </div>

            {securityAlert && (
              <div className={styles.alertSummaryBox}>
                <strong>Confirmação de Segurança Registrada:</strong>
                <span>Data/Hora: {securityAlert.timestamp}</span>
                <span>IP: {securityAlert.ipAddress}</span>
                <span>Sessões anteriores: Todas revogadas</span>
              </div>
            )}

            <button
              type="button"
              className={styles.submit}
              onClick={() => navigate('/login', { replace: true })}
            >
              Ir para o Login
            </button>
          </div>
        )}

        <div className={styles.navLinks}>
          <p className={styles.linkText}>
            Lembrou sua senha?{' '}
            <Link to="/login" className={styles.actionLink}>
              Fazer login
            </Link>
          </p>
          <p className={styles.linkText}>
            Ainda não tem conta?{' '}
            <Link to="/cadastro" className={styles.actionLink}>
              Cadastrar
            </Link>
          </p>
        </div>
      </main>

      <footer className={styles.copyright}>
        © {new Date().getFullYear()} SIPA - Sistema Inteligente de Produção Agrícola
      </footer>
    </div>
  );
}
