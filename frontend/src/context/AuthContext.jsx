import { createContext, useContext, useState } from 'react';
import { authService } from '../services/authService.js';
import { storage } from '../utils/storage.js';

const AuthContext = createContext({});

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => storage.getUser());
  const [isAuthenticated, setIsAuthenticated] = useState(() => Boolean(storage.getToken()));

  // Autenticação real com o backend Spring Boot (/SIPA/login)
  async function login(email, senha) {
    const data = await authService.login(email, senha);

    const token = data.token;
    const usuario = data.usuario || { email };

    storage.setToken(token);
    storage.setUser(usuario);

    setUser(usuario);
    setIsAuthenticated(true);
    return usuario;
  }

  // Atualiza os dados do usuário logado na sessão e no localStorage em tempo real
  function updateUser(updatedFields) {
    setUser((prev) => {
      const nextUser = { ...(prev || {}), ...updatedFields };
      storage.setUser(nextUser);
      return nextUser;
    });
  }

  async function logout() {
    await authService.logout();
    storage.clear();
    setUser(null);
    setIsAuthenticated(false);
  }

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, login, logout, updateUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
