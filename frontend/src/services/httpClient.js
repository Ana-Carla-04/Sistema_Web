import { storage } from '../utils/storage.js';

const BASE_URL = 'http://localhost:8080'; // Porta do seu Spring Boot

async function request(endpoint, options = {}) {
    const token = storage.getToken();

    const headers = {
        'Content-Type': 'application/json',
        ...(token && { Authorization: `Bearer ${token}` }),
        ...options.headers,
    };

    // Constrói a URL com parâmetros de busca (ex: ?nome=lote&status=PLANTADO)
    const url = new URL(`${BASE_URL}${endpoint}`);
    if (options.params) {
        Object.keys(options.params).forEach(key => {
            if (options.params[key] !== undefined && options.params[key] !== null) {
                url.searchParams.append(key, options.params[key]);
            }
        });
    }

    const response = await fetch(url.toString(), {
        ...options,
        headers,
    });

    if (!response.ok) {
        if (response.status === 401) {
            storage.removeToken(); // Token expirado
            window.location.href = '/login'; // Redireciona pro login
        }
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || 'Erro na requisição');
    }

    // Se a resposta for 204 (No Content), não tenta ler o JSON
    if (response.status === 204) return null;

    return response.json();
}

// Métodos HTTP
const httpClient = {
    get: (url, options) => request(url, { ...options, method: 'GET' }),
    post: (url, body, options) => request(url, { ...options, method: 'POST', body: JSON.stringify(body) }),
    put: (url, body, options) => request(url, { ...options, method: 'PUT', body: JSON.stringify(body) }),
    delete: (url, options) => request(url, { ...options, method: 'DELETE' }),
};

export default httpClient;