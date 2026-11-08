import { createContext, useContext, useState } from 'react';
import { storage } from '../utils/storage.js';

const AuthContext = createContext({});

export function AuthProvider({ children }) {
    //const [user, setUser] = useState(() => storage.getUser());
    //const [isAuthenticated, setIsAuthenticated] = useState(() => !!storage.getToken());

    // MUDANÇA 1: Se não tiver usuário salvo, usamos o João Silva na hora
    const [user, setUser] = useState(() => {
        return storage.getUser() || { nome: 'João Silva', email: 'joao@email.com' };
    });

    // MUDANÇA 2: Forçamos o isAuthenticated a começar como true
    const [isAuthenticated, setIsAuthenticated] = useState(true);

    // Simulação de login. Você precisará adaptar para chamar a rota /auth/login do seu Spring Boot.
    async function login(email, password) {
        // Exemplo de como seria a chamada real:
        // const response = await fetch('http://localhost:8080/SIPA/auth/login', { ... });
        // const { token, nome } = await response.json();

        // Vamos simular um login bem-sucedido para você ver a tela:
        const mockUser = { nome: 'João Silva', email: 'joao@email.com' };
        const mockToken = 'token-falso-para-testar';

        storage.setToken(mockToken);
        storage.setUser(mockUser);
        setUser(mockUser);
        setIsAuthenticated(true);
    }

    function logout() {
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