import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import { fileURLToPath } from 'node:url';

// O .env fica na raiz do monorepo (compartilhado com o docker-compose)
const envDir = fileURLToPath(new URL('..', import.meta.url));

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, envDir, '');
  // Alvo do proxy em desenvolvimento (ex.: http://localhost:8080)
  const proxyTarget = env.VITE_DEV_PROXY_TARGET || 'http://localhost:8080';

  return {
    envDir,
    plugins: [react()],
    server: {
      host: true,
      port: 5173,
      proxy: {
        // Redireciona todas as chamadas /SIPA/* para o backend Spring Boot (evita CORS em dev)
        '/SIPA': {
          target: proxyTarget,
          changeOrigin: true,
          secure: false,
        },
      },
    },
  };
});
