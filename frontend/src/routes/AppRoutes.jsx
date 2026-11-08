import { Routes, Route, Navigate } from 'react-router-dom';
import { PrivateRoute } from './PrivateRoute';
import  AppLayout  from '../components/Layout/AppLayout';

// Páginas de Autenticação e Cadastro
//import { LoginPage } from '../pages/auth/LoginPage';
//import { CadastroPage } from '../pages/cadastro/CadastroPage';

// Módulo de Exemplo Completo (CRUD real integrado com /SIPA/plantios)
import  PlantiosPage  from '../pages/plantios/PlantiosPage';

// Scaffolds das demais features mapeadas no backend
//import { DashboardPage } from '../features/dashboard/pages/DashboardPage';
//import { CustosPage } from '../features/custos/pages/CustosPage';
//import { ColheitasPage } from '../features/colheitas/pages/ColheitasPage';
//import { TarefasPage } from '../features/tarefas/pages/TarefasPage';
//import { AnaliseFinanceiraPage } from '../features/analiseFinanceira/pages/AnaliseFinanceiraPage';
//import { HistoricoPage } from '../features/historico/pages/HistoricoPage';

export function AppRoutes() {
  return (
      <Routes>
      {/* Rotas Públicas *
      <Route path="/login" element={<LoginPage />} />
      <Route path="/cadastro" element={<CadastroPage />} />

      {/* Rotas Protegidas (exigem token JWT) *
      <Route element={<PrivateRoute />}>
        <Route element={<AppLayout />}>
          <Route index element={<Navigate to="/plantios" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/plantios" element={<PlantiosPage />} />
          <Route path="/custos" element={<CustosPage />} />
          <Route path="/colheita" element={<ColheitasPage />} />
          <Route path="/tarefas" element={<TarefasPage />} />
          <Route path="/analise-financeira" element={<AnaliseFinanceiraPage />} />
          <Route path="/historico" element={<HistoricoPage />} />
        </Route>
      </Route>

      {/* Fallback *
      <Route path="*" element={<Navigate to="/plantios" replace />} />
    </Routes>*/}



        {/* O Layout sem PrivateRoute */}
        <Route path="/" element={<AppLayout />}>
            <Route index element={<Navigate to="/plantios" replace />} />
            <Route path="/plantios" element={<PlantiosPage />} />
        </Route>

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/plantios" replace />} />
    </Routes>
  );
}
