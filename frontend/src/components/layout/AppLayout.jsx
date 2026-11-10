import { useState } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';

import { useAuth } from '../../context/AuthContext.jsx';
import { formatSafra, getCurrentSafraStartYear } from '../../utils/plantio.js';
import Icon from '../icons/Icon.jsx';
import styles from './AppLayout.module.css';

const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard', icon: 'dashboard' },
  { to: '/plantios', label: 'Plantios', icon: 'sprout' },
  { to: '/custos', label: 'Custos', icon: 'cost' },
  { to: '/colheita', label: 'Colheita', icon: 'harvest' },
  { to: '/tarefas', label: 'Tarefas', icon: 'tasks' },
  { to: '/analise-financeira', label: 'Análise Financeira', icon: 'chart' },
  { to: '/historico', label: 'Histórico', icon: 'history' },
];

export default function AppLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [globalSearch, setGlobalSearch] = useState('');

  // Dados do usuário autenticado no backend
  const displayName = user?.nome ?? user?.name ?? user?.email ?? 'Produtor';
  const displayRole = user?.email ?? user?.cargo ?? 'Produtor Rural';

  function handleLogout() {
    logout();
    navigate('/login', { replace: true });
  }

  function handleSearchSubmit(event) {
    event.preventDefault();
    const term = globalSearch.trim();
    if (!term) return;
    navigate(`/plantios?busca=${encodeURIComponent(term)}`);
  }

  return (
    <div className={styles.shell}>
      <aside className={styles.sidebar}>
        <div className={styles.brand}>
          <span className={styles.brandMark}>
            <Icon name="leaf" size={18} />
          </span>
          <span className={styles.brandName}>SIPA</span>
        </div>

        <nav className={styles.nav} aria-label="Navegação principal">
          {NAV_ITEMS.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                isActive ? `${styles.navItem} ${styles.navItemActive}` : styles.navItem
              }
            >
              <Icon name={item.icon} size={16} />
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className={styles.userSection}>
          <div className={styles.user}>
            <span className={styles.avatar}>
              <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor" aria-hidden="true">
                <circle cx="12" cy="8" r="4" />
                <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
              </svg>
            </span>
            <div className={styles.userInfo}>
              <span className={styles.userName}>{displayName}</span>
              <span className={styles.userRole}>{displayRole}</span>
            </div>
          </div>
          <button
            type="button"
            className={styles.logoutButton}
            onClick={handleLogout}
            title="Encerrar sessão"
          >
            <Icon name="logout" size={16} />
            <span>Sair</span>
          </button>
        </div>
      </aside>

      <div className={styles.main}>
        <header className={styles.topbar}>
          <form className={styles.globalSearch} onSubmit={handleSearchSubmit}>
            <Icon name="search" size={14} />
            <input
              type="search"
              value={globalSearch}
              onChange={(event) => setGlobalSearch(event.target.value)}
              placeholder="Pesquisar no sistema..."
            />
          </form>

          <div className={styles.topbarRight}>
            <button
              type="button"
              className={styles.bell}
              aria-label="Notificações"
              onClick={() => navigate('/tarefas')}
              title="Ver tarefas e alertas"
            >
              <Icon name="bell" size={16} />
            </button>
            <span className={styles.safraLabel}>
              Safra {formatSafra(getCurrentSafraStartYear())}
            </span>
          </div>
        </header>

        <main className={styles.content}>
          <Outlet context={{ globalSearch }} />
        </main>
      </div>
    </div>
  );
}
