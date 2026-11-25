import httpClient from './httpClient.js';

export const authService = {
  // Autentica o usuário na rota real do Spring Boot (/SIPA/login)
  login: async (email, senha) => {
    const response = await fetch('http://localhost:8080/SIPA/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, senha }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(
        errorData.detail || errorData.message || 'Credenciais inválidas. Verifique seu e-mail e senha.',
      );
    }

    return response.json(); // { token, tipo, usuario: { id, nome, email } }
  },

  // Logout no backend (descarte de token stateless)
  logout: async () => {
    try {
      await httpClient.delete('/SIPA/login');
    } catch {
      // Ignora erro no logout para não travar a saída do usuário
    }
  },

  // Cadastro de novo usuário (/SIPA/cadastro)
  cadastrar: async ({ nome, email, senha }) => {
    const response = await fetch('http://localhost:8080/SIPA/cadastro', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ nome, email, senha }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      const fieldErrors = errorData.erros ? Object.values(errorData.erros).join(' ') : '';
      throw new Error(fieldErrors || errorData.detail || errorData.message || 'Erro ao realizar cadastro.');
    }

    return response.json(); // UsuarioResponseDTO
  },

  // Busca os dados atuais do usuário autenticado no banco
  obterMeuPerfil: async (usuarioId) => {
    const endpoint = usuarioId ? `/SIPA/cadastro/${usuarioId}` : '/SIPA/cadastro/me';
    return httpClient.get(endpoint, { skipAuthRedirect: true });
  },

  // Atualiza os dados do usuário mediante confirmação de segurança (senhaAtual)
  atualizarMeuPerfil: async (
    usuarioId,
    { nome, email, senhaAtual, novaSenha, confirmarNovaSenha },
  ) => {
    const endpoint = usuarioId ? `/SIPA/cadastro/${usuarioId}` : '/SIPA/cadastro/me';
    return httpClient.put(
      endpoint,
      {
        nome,
        email,
        senhaAtual,
        ...(novaSenha ? { novaSenha, confirmarNovaSenha } : {}),
      },
      { skipAuthRedirect: true },
    );
  },

  // Etapa 1: Solicita link tokenizado de recuperação de senha (POST /SIPA/auth/forgot-password)
  solicitarRecuperacaoSenha: async (email) => {
    const response = await fetch('http://localhost:8080/SIPA/auth/forgot-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      const fieldErrors = errorData.erros ? Object.values(errorData.erros).join(' ') : '';
      throw new Error(
        fieldErrors ||
          errorData.detail ||
          errorData.message ||
          'Não foi possível processar a solicitação de recuperação.',
      );
    }

    return response.json();
  },

  // Etapa 2 (GET): Valida o token de recuperação em tempo constante e obtém o token CSRF
  validarTokenRecuperacao: async (token) => {
    const response = await fetch(
      `http://localhost:8080/SIPA/auth/reset-password/${encodeURIComponent(token)}`,
      {
        method: 'GET',
        headers: {
          Accept: 'application/json',
        },
      },
    );

    const data = await response.json().catch(() => ({}));
    if (!response.ok || !data.valid) {
      const error = new Error(data.message || data.detail || 'Token inválido ou expirado.');
      error.captchaRequired = Boolean(data.captchaRequired);
      throw error;
    }

    return data; // { valid, message, csrfToken, maskedEmail, expiresInSeconds, captchaRequired }
  },

  // Etapa 3 (POST): Redefine a senha com token de uso único, proteção CSRF e validação HIBP
  redefinirSenhaComToken: async ({ token, password, passwordConfirm, csrf, captchaToken }) => {
    const response = await fetch('http://localhost:8080/SIPA/auth/reset-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        token,
        password,
        passwordConfirm,
        csrf,
        ...(captchaToken ? { captchaToken } : {}),
      }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      const fieldErrors = errorData.erros ? Object.values(errorData.erros).join(' ') : '';
      throw new Error(
        fieldErrors || errorData.detail || errorData.message || 'Erro ao redefinir senha.',
      );
    }

    return response.json();
  },
};
