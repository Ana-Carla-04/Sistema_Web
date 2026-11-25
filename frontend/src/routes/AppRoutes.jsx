import { Routes, Route, Navigate } from 'react-router-dom';
import AppLayout from '../components/layout/AppLayout.jsx';
import AnaliseFinanceiraPage from '../pages/analise-financeira/AnaliseFinanceiraPage.jsx';
import CadastroPage from '../pages/cadastro/CadastroPage.jsx';
import CustosPage from '../pages/custos/CustosPage.jsx';
import DashboardPage from '../pages/dashboard/DashboardPage.jsx';
import HistoricoPage from '../pages/historico/HistoricoPage.jsx';
import LoginPage from '../pages/login/LoginPage.jsx';
import RecuperarSenhaPage from '../pages/recuperar-senha/RecuperarSenhaPage.jsx';
import PlantiosPage from '../pages/plantios/PlantiosPage.jsx';
import TarefasPage from '../pages/tarefas/TarefasPage.jsx';
import { PrivateRoute } from './PrivateRoute.jsx';
import ColheitaPage from '../pages/Colheita/ColheitaPage.jsx';

export function AppRoutes() {
  return (
    <Routes>
      {/* Rotas Públicas */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/cadastro" element={<CadastroPage />} />
      <Route path="/recuperar-senha" element={<RecuperarSenhaPage />} />

      {/* Rotas Protegidas (exigem autenticação do usuário) */}
      <Route element={<PrivateRoute />}>
        <Route path="/" element={<AppLayout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/plantios" element={<PlantiosPage />} />
          <Route path="/custos" element={<CustosPage />} />
          <Route path="/colheita" element={<ColheitaPage />} />

          <Route path="/tarefas" element={<TarefasPage />} />
          <Route path="/analise-financeira" element={<AnaliseFinanceiraPage />} />
          <Route path="/historico" element={<HistoricoPage />} />
        </Route>
      </Route>

      {/* Fallback padrão */}
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}