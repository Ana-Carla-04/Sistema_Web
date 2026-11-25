import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// O proxy evita problemas de CORS em desenvolvimento:
// o front chama /SIPA/... e o Vite encaminha para o Spring.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/SIPA': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
