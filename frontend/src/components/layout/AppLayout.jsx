import { NavLink, Outlet, useNavigate } from 'react-router-dom';

import { useAuth } from '../../context/AuthContext.jsx';
import { formatSafra, getCurrentSafraStartYear } from '../../utils/plantio.js';
import Icon from '../Icons/Icon.jsx';
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

function getInitial(name) {
  return name ? name.trim().charAt(0).toUpperCase() : '?';
}

export default function AppLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  // Ajuste os campos conforme o que o seu login devolve para o usuário.
  const displayName = user?.nome ?? user?.name ?? user?.email ?? 'Usuário';

  function handleLogout() {
    logout();
    navigate('/login', { replace: true });
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
          <button type="button" className={styles.navItem} onClick={handleLogout}>
            <Icon name="logout" size={16} />
            Sair
          </button>
        </nav>

        <div className={styles.user}>
          <span className={styles.avatar}>{getInitial(displayName)}</span>
          <span className={styles.userName}>{displayName}</span>
        </div>
      </aside>

      <div className={styles.main}>
        <header className={styles.topbar}>
          <label className={styles.globalSearch}>
            <Icon name="search" size={14} />
            <input type="search" placeholder="Pesquisar no sistema..." />
          </label>

          <div className={styles.topbarRight}>
            <button type="button" className={styles.bell} aria-label="Notificações">
              <Icon name="bell" size={16} />
            </button>
            <span className={styles.safraLabel}>
              Safra {formatSafra(getCurrentSafraStartYear())}
            </span>
          </div>
        </header>

        <main className={styles.content}>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
