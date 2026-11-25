# SIPA — Front-end (React + Vite)

Estrutura base (scaffold) do front-end do **Sistema Integrado de Produção Agrícola (SIPA)**, construída em **React + JavaScript puro**, **CSS puro** e `fetch` nativo, conectada aos contratos reais da API Spring Boot (`/SIPA`).

## 1. Como rodar em desenvolvimento

Na raiz do repositório, suba o banco e o backend (ou rode via Maven), e em seguida inicie o front-end:

```bash
# na raiz: cp .env.example .env  (o Vite l� o .env da raiz)
cd frontend
npm install
npm run dev
```

- O front-end abre em `http://localhost:5173`.
- O proxy do Vite (`vite.config.js`) redireciona automaticamente qualquer chamada `/SIPA/*` para `http://localhost:8080`, evitando erros de CORS em desenvolvimento.

## 2. Como rodar via Docker Compose

Na raiz do repositório (`Sistema_Web/`):

```bash
docker compose up --build -d
```

- **API (Spring Boot)**: `http://localhost:8080`
- **Front-end (Nginx + React)**: `http://localhost:3000` (o Nginx faz proxy reverso de `/SIPA/` para o container `app:8080`).
- Existe **um �nico** `Dockerfile` na raiz, com os est�gios `backend` e `frontend`; o `docker-compose.yml` escolhe cada um via `target`. Tamb�m `.env`, `.env.example`, `.gitignore` e `.dockerignore` ficam s� na raiz.

## 3. Estrutura de Pastas

```text
frontend/
├── public/
├── src/
│   ├── components/          # Componentes visuais genéricos e reutilizáveis
│   │   ├── Button/
│   │   ├── Card/
│   │   ├── Input/
│   │   ├── Layout/          # AppLayout (Sidebar + Header + Outlet)
│   │   ├── Loading/
│   │   ├── Modal/
│   │   └── Table/
│   ├── context/             # Contextos globais (AuthContext)
│   ├── features/            # Módulos de negócio isolados por domínio
│   │   ├── auth/            # Login (/SIPA/login)
│   │   ├── cadastro/        # Cadastro e perfil de usuário (/SIPA/cadastro)
│   │   ├── plantios/        # Módulo de exemplo COMPLETO (pages, components, hooks, services)
│   │   ├── custos/          # Service mapeado + página scaffold (/SIPA/custos)
│   │   ├── colheitas/       # Service mapeado + página scaffold (/SIPA/colheita)
│   │   ├── tarefas/         # Service mapeado + página scaffold (/SIPA/tarefas)
│   │   ├── dashboard/       # Service mapeado + página scaffold (/SIPA/dashboard)
│   │   ├── analiseFinanceira/ # Service mapeado + página scaffold (/SIPA/analise-financeira)
│   │   └── historico/       # Service mapeado + página scaffold (/SIPA/historico)
│   ├── routes/              # AppRoutes e PrivateRoute
│   ├── services/            # Cliente HTTP centralizado (httpClient.js)
│   ├── styles/              # variables.css, reset.css e global.css
│   ├── utils/               # storage, formatters (data/moeda) e hook useForm
│   ├── App.jsx
│   └── main.jsx
├── .env.example
├── Dockerfile
├── nginx.conf
├── eslint.config.js
├── .prettierrc
├── package.json
└── vite.config.js
```

## 4. Convenções do Projeto

- **Nomenclatura**:
  - Componentes e Páginas React: `PascalCase.jsx` + `PascalCase.css` (ex.: `PlantiosPage.jsx`, `PlantiosPage.css`).
  - Hooks customizados: `camelCase.js` com prefixo `use` (ex.: `usePlantios.js`, `useForm.js`).
  - Services e utilitários: `camelCase.js` (ex.: `plantiosService.js`, `httpClient.js`).
- **Onde colocar cada coisa**:
  - Se um componente, hook ou service é usado **apenas por uma tela/módulo**, coloque dentro de `src/features/<modulo>/{pages,components,hooks,services}`.
  - Se um componente é **genérico e compartilhado** entre vários módulos (ex.: `Button`, `Modal`, `Table`), coloque em `src/components/`.
  - **Nunca** chame `fetch` diretamente nos componentes: use sempre os services de cada feature, que por sua vez chamam o `httpClient`.
- **Scripts úteis**:
  - `npm run dev`: servidor de desenvolvimento
  - `npm run build`: build de produção
  - `npm run lint`: validação com ESLint
  - `npm run format`: formatação com Prettier
