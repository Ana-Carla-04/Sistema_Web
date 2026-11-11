import { Routes, Route, Navigate } from 'react-router-dom';
import AppLayout from '../components/layout/AppLayout.jsx';
import CadastroPage from '../pages/cadastro/CadastroPage.jsx';
import DashboardPage from '../pages/dashboard/DashboardPage.jsx';
import LoginPage from '../pages/login/LoginPage.jsx';
import PlantiosPage from '../pages/plantios/PlantiosPage.jsx';
import FuturePagePlaceholder from '../pages/shared/FuturePagePlaceholder.jsx';
import { PrivateRoute } from './PrivateRoute.jsx';

export function AppRoutes() {
  return (
    <Routes>
      {/* Rotas Públicas */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/cadastro" element={<CadastroPage />} />

      {/* Rotas Protegidas (exigem autenticação do usuário) */}
      <Route element={<PrivateRoute />}>
        <Route path="/" element={<AppLayout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/plantios" element={<PlantiosPage />} />
          <Route
            path="/custos"
            element={
              <FuturePagePlaceholder
                title="Custos da Produção"
                subtitle="Detalhamento de despesas por categoria e lote"
                endpoint="GET /SIPA/custos"
              />
            }
          />
          <Route
            path="/colheita"
            element={
              <FuturePagePlaceholder
                title="Colheita e Receita"
                subtitle="Simulador de rendimento por alqueire (115kg) e lançamentos"
                endpoint="POST /SIPA/colheita/simulacoes-colheita"
              />
            }
          />
          <Route
            path="/tarefas"
            element={
              <FuturePagePlaceholder
                title="Cronograma de Tarefas"
                subtitle="Atividades prioritárias, pendentes, agendadas e concluídas"
                endpoint="GET /SIPA/tarefas"
              />
            }
          />
          <Route
            path="/analise-financeira"
            element={
              <FuturePagePlaceholder
                title="Análise Financeira"
                subtitle="Balanço consolidado de custos, receitas e lucro líquido"
                endpoint="GET /SIPA/analise-financeira"
              />
            }
          />
          <Route
            path="/historico"
            element={
              <FuturePagePlaceholder
                title="Histórico de Produção"
                subtitle="Comparativo de safras consolidadas ano a ano"
                endpoint="GET /SIPA/historico"
              />
            }
          />
        </Route>
      </Route>

      {/* Fallback padrão */}
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
