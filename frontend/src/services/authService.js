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
      throw new Error(errorData.message || 'Credenciais inválidas. Verifique seu e-mail e senha.');
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
      throw new Error(errorData.message || 'Erro ao realizar cadastro.');
    }

    return response.json(); // UsuarioResponseDTO
  },
};
