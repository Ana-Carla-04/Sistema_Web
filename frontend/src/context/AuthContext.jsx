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

  async function logout() {
    await authService.logout();
    storage.clear();
    setUser(null);
    setIsAuthenticated(false);
  }

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
