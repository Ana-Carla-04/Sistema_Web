import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';

import { useAuth } from '../../context/AuthContext.jsx';
import { authService } from '../../services/authService.js';
import { dashboardService } from '../../services/dashboardService.js';
import { formatSafra, getCurrentSafraStartYear } from '../../utils/plantio.js';
import { evaluatePasswordStrength } from '../../utils/validators.js';
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

function formatDateBR(isoDate) {
  if (!isoDate) return 'Sem prazo';
  const parts = String(isoDate).split('-');
  if (parts.length !== 3) return isoDate;
  return `${parts[2]}/${parts[1]}/${parts[0]}`;
}

export default function AppLayout() {
  const { user, logout, updateUser } = useAuth();
  const navigate = useNavigate();
  const [globalSearch, setGlobalSearch] = useState('');
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  // ---- Estado do Card de Notificações (Sininho) ----
  const [notifOpen, setNotifOpen] = useState(false);
  const [notifLoading, setNotifLoading] = useState(false);
  const [notifTarefas, setNotifTarefas] = useState([]);
  const [notifPlantios, setNotifPlantios] = useState([]);
  const [notifLidasIds, setNotifLidasIds] = useState([]);
  const notifWrapperRef = useRef(null);

  // ---- Estado do Card de Perfil e Edição com Senha (Usuário) ----
  const [profileOpen, setProfileOpen] = useState(false);
  const [isEditingProfile, setIsEditingProfile] = useState(false);
  const [profileLoading, setProfileLoading] = useState(false);
  const [profileSaving, setProfileSaving] = useState(false);
  const [profileError, setProfileError] = useState('');
  const [profileSuccess, setProfileSuccess] = useState('');
  const [profileSecurityAlert, setProfileSecurityAlert] = useState(null);
  const [profileForm, setProfileForm] = useState({
    nome: '',
    email: '',
    novaSenha: '',
    confirmarNovaSenha: '',
    senhaAtual: '',
  });

  // Dados do usuário autenticado no backend
  const displayName = user?.nome ?? user?.name ?? user?.email ?? 'Produtor';
  const displayRole = user?.email ?? user?.cargo ?? 'Produtor Rural';
  const userId = user?.id ?? null;

  // Carrega notificações reais do banco (tarefas pendentes + lotes)
  const carregarNotificacoes = useCallback(async () => {
    setNotifLoading(true);
    try {
      const [tarefasRes, plantiosRes] = await Promise.all([
        dashboardService.listarTarefas().catch(() => []),
        dashboardService.listarPlantios().catch(() => []),
      ]);
      setNotifTarefas(Array.isArray(tarefasRes) ? tarefasRes : []);
      setNotifPlantios(Array.isArray(plantiosRes) ? plantiosRes : []);
    } finally {
      setNotifLoading(false);
    }
  }, []);

  useEffect(() => {
    carregarNotificacoes();
  }, [carregarNotificacoes]);

  // Fecha o card de notificações ao clicar fora ou apertar ESC
  useEffect(() => {
    if (!notifOpen) return;

    function handleClickOutside(event) {
      if (notifWrapperRef.current && !notifWrapperRef.current.contains(event.target)) {
        setNotifOpen(false);
      }
    }

    function handleKeyDown(event) {
      if (event.key === 'Escape') {
        setNotifOpen(false);
      }
    }

    document.addEventListener('mousedown', handleClickOutside);
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [notifOpen]);

  // Mapa de id -> nome do lote
  const plantioMap = useMemo(
    () => new Map(notifPlantios.map((p) => [p.id, p.nome])),
    [notifPlantios],
  );

  // Tarefas pendentes ainda não concluídas e não marcadas como lidas na sessão
  const tarefasPendentesNotif = useMemo(() => {
    return notifTarefas
      .filter((t) => !t.concluida && !notifLidasIds.includes(t.id))
      .slice(0, 6);
  }, [notifTarefas, notifLidasIds]);

  const totalNotificacoes = tarefasPendentesNotif.length;

  function handleToggleNotif() {
    const proximo = !notifOpen;
    setNotifOpen(proximo);
    if (proximo) {
      carregarNotificacoes();
    }
  }

  async function handleConcluirTarefaNotif(tarefaId, event) {
    event.stopPropagation();
    try {
      await dashboardService.concluirTarefa(tarefaId);
      setNotifTarefas((prev) =>
        prev.map((t) => (t.id === tarefaId ? { ...t, concluida: true } : t)),
      );
    } catch (err) {
      console.error('Erro ao concluir tarefa pela notificação:', err);
    }
  }

  function handleMarcarTodasComoLidas() {
    setNotifLidasIds(notifTarefas.map((t) => t.id));
  }

  // ---- Funções do Card de Perfil do Usuário ----
  async function handleOpenProfile() {
    setProfileOpen(true);
    setIsEditingProfile(false);
    setProfileError('');
    setProfileSuccess('');
    setProfileSecurityAlert(null);
    setProfileForm({
      nome: user?.nome ?? '',
      email: user?.email ?? '',
      novaSenha: '',
      confirmarNovaSenha: '',
      senhaAtual: '',
    });

    // Sincroniza dados frescos do usuário no backend (/SIPA/cadastro/me)
    setProfileLoading(true);
    try {
      const dadosFrescos = await authService.obterMeuPerfil(user?.id);
      if (dadosFrescos) {
        updateUser(dadosFrescos);
        setProfileForm((prev) => ({
          ...prev,
          nome: dadosFrescos.nome ?? prev.nome,
          email: dadosFrescos.email ?? prev.email,
        }));
      }
    } catch {
      // Mantém os dados da sessão local caso falhe
    } finally {
      setProfileLoading(false);
    }
  }

  function handleCloseProfile() {
    if (profileSaving) return;
    setProfileOpen(false);
    setIsEditingProfile(false);
    setProfileError('');
    setProfileSuccess('');
  }

  function handleProfileInputChange(event) {
    const { name, value } = event.target;
    if (profileError) setProfileError('');
    if (profileSuccess) setProfileSuccess('');
    setProfileForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleProfileSubmit(event) {
    event.preventDefault();
    if (profileSaving) return;

    const nomeLimpo = profileForm.nome.trim();
    const emailLimpo = profileForm.email.trim();
    const senhaAtual = profileForm.senhaAtual;
    const novaSenha = profileForm.novaSenha;
    const confirmarNovaSenha = profileForm.confirmarNovaSenha;

    if (!nomeLimpo) {
      setProfileError('O nome não pode ficar em branco.');
      return;
    }
    if (!emailLimpo || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailLimpo)) {
      setProfileError('Informe um endereço de e-mail válido.');
      return;
    }
    if (!senhaAtual) {
      setProfileError('Por segurança, digite sua senha atual para confirmar as alterações.');
      return;
    }
    if (novaSenha) {
      const strength = evaluatePasswordStrength(novaSenha, emailLimpo);
      if (!strength.minLength) {
        setProfileError('A nova senha deve ter no mínimo 6 caracteres.');
        return;
      }
      if (!strength.onlyAlphanumeric) {
        setProfileError('A nova senha deve conter apenas letras e números.');
        return;
      }
      if (!strength.hasLetter || !strength.hasDigit) {
        setProfileError('A nova senha deve conter pelo menos uma letra e um número.');
        return;
      }
      if (novaSenha !== confirmarNovaSenha) {
        setProfileError('A confirmação da nova senha não coincide.');
        return;
      }
    }

    setProfileSaving(true);
    setProfileError('');
    setProfileSuccess('');
    setProfileSecurityAlert(null);

    try {
      const atualizado = await authService.atualizarMeuPerfil(user?.id, {
        nome: nomeLimpo,
        email: emailLimpo,
        senhaAtual,
        novaSenha: novaSenha || undefined,
        confirmarNovaSenha: novaSenha ? confirmarNovaSenha : undefined,
      });

      updateUser({
        id: atualizado.id ?? user?.id,
        nome: atualizado.nome ?? nomeLimpo,
        email: atualizado.email ?? emailLimpo,
      });
      setProfileForm((prev) => ({
        ...prev,
        nome: atualizado.nome ?? nomeLimpo,
        email: atualizado.email ?? emailLimpo,
        senhaAtual: '',
        novaSenha: '',
        confirmarNovaSenha: '',
      }));

      if (atualizado.senhaAlterada && atualizado.sessoesInvalidadas) {
        setProfileSecurityAlert(atualizado.alertaSeguranca || null);
        setProfileSuccess(
          'Senha alterada com sucesso! Por segurança, todas as sessões ativas foram encerradas. Redirecionando para novo login...',
        );
        setIsEditingProfile(false);
        setTimeout(() => {
          setProfileOpen(false);
          logout();
          navigate('/login', { replace: true });
        }, 3000);
      } else {
        setProfileSuccess('Seus dados foram verificados e atualizados com sucesso!');
        setIsEditingProfile(false);
      }
    } catch (err) {
      setProfileError(err.message || 'Não foi possível atualizar os dados. Verifique sua senha.');
    } finally {
      setProfileSaving(false);
    }
  }

  function handleLogout() {
    setMobileMenuOpen(false);
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
      {/* Backdrop escuro para menu mobile */}
      {mobileMenuOpen && (
        <div
          className={styles.mobileBackdrop}
          onClick={() => setMobileMenuOpen(false)}
          aria-hidden="true"
        />
      )}

      <aside className={`${styles.sidebar} ${mobileMenuOpen ? styles.sidebarOpen : ''}`}>
        <div className={styles.brandRow}>
          <div className={styles.brand}>
            <span className={styles.brandMark}>
              <Icon name="leaf" size={20} />
            </span>
            <span className={styles.brandName}>SIPA</span>
          </div>

          <button
            type="button"
            className={styles.closeDrawerBtn}
            onClick={() => setMobileMenuOpen(false)}
            aria-label="Fechar menu"
          >
            <Icon name="close" size={20} />
          </button>
        </div>

        <nav className={styles.nav} aria-label="Navegação principal">
          {NAV_ITEMS.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              onClick={() => setMobileMenuOpen(false)}
              className={({ isActive }) =>
                isActive ? `${styles.navItem} ${styles.navItemActive}` : styles.navItem
              }
            >
              <Icon name={item.icon} size={19} />
              <span>{item.label}</span>
            </NavLink>
          ))}
        </nav>

        <div className={styles.userSection}>
          {/* Botão interativo do Usuário — abre o Card de Dados e Edição com Senha */}
          <button
            type="button"
            className={styles.userTriggerBtn}
            onClick={handleOpenProfile}
            title="Clique para visualizar ou editar seus dados"
          >
            <span className={styles.avatar}>
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor" aria-hidden="true">
                <circle cx="12" cy="8" r="4" />
                <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
              </svg>
            </span>
            <div className={styles.userInfo}>
              <span className={styles.userName}>{displayName}</span>
              <span className={styles.userRole}>{displayRole}</span>
            </div>
            <span className={styles.userEditBadge} aria-hidden="true">
              <Icon name="edit" size={14} />
            </span>
          </button>

          <button
            type="button"
            className={styles.logoutButton}
            onClick={handleLogout}
            title="Encerrar sessão"
          >
            <Icon name="logout" size={18} />
            <span>Sair</span>
          </button>
        </div>
      </aside>

      <div className={styles.main}>
        <header className={styles.topbar}>
          <div className={styles.topbarLeft}>
            <button
              type="button"
              className={styles.menuButton}
              onClick={() => setMobileMenuOpen(true)}
              aria-label="Abrir menu de navegação"
            >
              <Icon name="menu" size={22} />
            </button>

            <form className={styles.globalSearch} onSubmit={handleSearchSubmit}>
              <Icon name="search" size={16} />
              <input
                type="search"
                value={globalSearch}
                onChange={(event) => setGlobalSearch(event.target.value)}
                placeholder="Pesquisar no sistema..."
              />
            </form>
          </div>

          <div className={styles.topbarRight}>
            {/* Sininho com Card de Notificações */}
            <div className={styles.notifWrapper} ref={notifWrapperRef}>
              <button
                type="button"
                className={`${styles.bell} ${notifOpen ? styles.bellActive : ''}`}
                aria-label="Notificações"
                aria-expanded={notifOpen}
                onClick={handleToggleNotif}
                title="Abrir central de notificações"
              >
                <Icon name="bell" size={18} />
                {totalNotificacoes > 0 && (
                  <span className={styles.bellBadge}>
                    {totalNotificacoes > 9 ? '9+' : totalNotificacoes}
                  </span>
                )}
              </button>

              {notifOpen && (
                <div className={styles.notifCard} role="dialog" aria-label="Notificações do sistema">
                  <header className={styles.notifHeader}>
                    <div className={styles.notifHeaderLeft}>
                      <h3 className={styles.notifTitle}>Notificações</h3>
                      <span className={styles.notifCountPill}>
                        {totalNotificacoes} {totalNotificacoes === 1 ? 'pendente' : 'pendentes'}
                      </span>
                    </div>

                    {totalNotificacoes > 0 && (
                      <button
                        type="button"
                        className={styles.notifClearBtn}
                        onClick={handleMarcarTodasComoLidas}
                      >
                        Marcar lidas
                      </button>
                    )}
                  </header>

                  <div className={styles.notifBody}>
                    {notifLoading ? (
                      <div className={styles.notifEmpty}>Carregando alertas...</div>
                    ) : totalNotificacoes === 0 ? (
                      <div className={styles.notifEmpty}>
                        <span className={styles.notifEmptyIcon}>
                          <Icon name="check" size={22} />
                        </span>
                        <strong>Tudo em dia!</strong>
                        <p>Você não possui tarefas ou alertas pendentes no momento.</p>
                      </div>
                    ) : (
                      <ul className={styles.notifList}>
                        {tarefasPendentesNotif.map((t) => {
                          const loteNome = t.plantioId
                            ? plantioMap.get(t.plantioId) || `Lote #${t.plantioId}`
                            : 'Geral';

                          return (
                            <li key={t.id} className={styles.notifItem}>
                              <div
                                className={styles.notifItemMain}
                                onClick={() => {
                                  setNotifOpen(false);
                                  navigate('/tarefas');
                                }}
                                role="button"
                                tabIndex={0}
                                onKeyDown={(e) => {
                                  if (e.key === 'Enter') {
                                    setNotifOpen(false);
                                    navigate('/tarefas');
                                  }
                                }}
                              >
                                <span className={styles.notifItemDot} />
                                <div className={styles.notifItemTexts}>
                                  <strong className={styles.notifItemTitle}>{t.titulo}</strong>
                                  <span className={styles.notifItemMeta}>
                                    {loteNome} • Prazo: {formatDateBR(t.dataLimite)}
                                  </span>
                                </div>
                              </div>

                              <button
                                type="button"
                                className={styles.notifDoneBtn}
                                onClick={(e) => handleConcluirTarefaNotif(t.id, e)}
                                title="Marcar tarefa como concluída"
                              >
                                <Icon name="check" size={14} strokeWidth={2.3} />
                              </button>
                            </li>
                          );
                        })}
                      </ul>
                    )}

                    {notifPlantios.length > 0 && (
                      <div className={styles.notifSummaryBar}>
                        <Icon name="sprout" size={15} />
                        <span>
                          <strong>{notifPlantios.length}</strong>{' '}
                          {notifPlantios.length === 1 ? 'lote monitorado' : 'lotes monitorados'} na
                          safra atual
                        </span>
                      </div>
                    )}
                  </div>

                  <footer className={styles.notifFooter}>
                    <button
                      type="button"
                      className={styles.notifViewAllBtn}
                      onClick={() => {
                        setNotifOpen(false);
                        navigate('/tarefas');
                      }}
                    >
                      <span>Ver todas as tarefas</span>
                      <Icon name="arrowRight" size={15} />
                    </button>
                  </footer>
                </div>
              )}
            </div>

            <span className={styles.safraLabel}>
              Safra {formatSafra(getCurrentSafraStartYear())}
            </span>
          </div>
        </header>

        <main className={styles.content}>
          <Outlet context={{ globalSearch }} />
        </main>
      </div>

      {/* Modal / Card de Perfil do Usuário com Verificação de Segurança por Senha */}
      {profileOpen && (
        <div
          className={styles.modalOverlay}
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) handleCloseProfile();
          }}
        >
          <div
            className={styles.profileCard}
            role="dialog"
            aria-modal="true"
            aria-labelledby="profile-card-title"
          >
            <header className={styles.profileHeader}>
              <div className={styles.profileHeaderTitleWrap}>
                <span className={styles.profileHeaderIcon}>
                  <Icon name="user" size={20} />
                </span>
                <div>
                  <h2 id="profile-card-title" className={styles.profileTitle}>
                    Meus Dados no Sistema
                  </h2>
                  <p className={styles.profileSubtitle}>
                    Visualize suas informações cadastrais ou edite com confirmação de segurança
                  </p>
                </div>
              </div>

              <button
                type="button"
                className={styles.profileCloseBtn}
                onClick={handleCloseProfile}
                aria-label="Fechar cartão de perfil"
              >
                <Icon name="close" size={18} />
              </button>
            </header>

            {profileSuccess && (
              <div className={styles.profileSuccessBanner} role="status">
                <Icon name="check" size={16} />
                <div>
                  <span>{profileSuccess}</span>
                  {profileSecurityAlert && (
                    <div style={{ fontSize: '0.75rem', marginTop: '4px', opacity: 0.9 }}>
                      Auditoria: {profileSecurityAlert.timestamp} • IP: {profileSecurityAlert.ipAddress}
                    </div>
                  )}
                </div>
              </div>
            )}

            {profileError && (
              <div className={styles.profileErrorBanner} role="alert">
                <span>{profileError}</span>
              </div>
            )}

            {!isEditingProfile ? (
              /* MODO VISUALIZAÇÃO DOS DADOS DO USUÁRIO */
              <div className={styles.profileViewBody}>
                <div className={styles.profileHero}>
                  <span className={styles.profileLargeAvatar}>
                    {String(displayName).charAt(0).toUpperCase()}
                  </span>
                  <div className={styles.profileHeroInfo}>
                    <strong className={styles.profileHeroName}>{displayName}</strong>
                    <span className={styles.profileHeroEmail}>{displayRole}</span>
                    <span className={styles.profileVerifiedBadge}>
                      <Icon name="shield" size={13} />
                      Conta Autenticada • Produtor Rural
                    </span>
                  </div>
                </div>

                <dl className={styles.profileDataGrid}>
                  <div className={styles.profileDataItem}>
                    <dt>Nome Completo</dt>
                    <dd>{profileLoading ? 'Carregando...' : displayName}</dd>
                  </div>
                  <div className={styles.profileDataItem}>
                    <dt>E-mail Cadastrado</dt>
                    <dd>{profileLoading ? 'Carregando...' : user?.email || '—'}</dd>
                  </div>
                  <div className={styles.profileDataItem}>
                    <dt>ID do Usuário no SIPA</dt>
                    <dd>{userId ? `#${userId}` : 'Autenticado'}</dd>
                  </div>
                  <div className={styles.profileDataItem}>
                    <dt>Perfil de Acesso</dt>
                    <dd>Produtor Rural (USER)</dd>
                  </div>
                </dl>

                <div className={styles.profileFooterActions}>
                  <button
                    type="button"
                    className={styles.profileSecondaryBtn}
                    onClick={handleCloseProfile}
                  >
                    Fechar
                  </button>
                  <button
                    type="button"
                    className={styles.profilePrimaryBtn}
                    onClick={() => {
                      setProfileError('');
                      setProfileSuccess('');
                      setIsEditingProfile(true);
                    }}
                  >
                    <Icon name="edit" size={16} />
                    <span>Editar Meus Dados</span>
                  </button>
                </div>
              </div>
            ) : (
              /* MODO EDIÇÃO COM VERIFICAÇÃO DE SEGURANÇA (SENHA ATUAL) */
              <form className={styles.profileForm} onSubmit={handleProfileSubmit} noValidate>
                <div className={styles.profileFieldGroup}>
                  <label htmlFor="profile-nome" className={styles.profileLabel}>
                    Nome de Usuário / Produtor
                  </label>
                  <input
                    id="profile-nome"
                    name="nome"
                    type="text"
                    className={styles.profileInput}
                    value={profileForm.nome}
                    onChange={handleProfileInputChange}
                    placeholder="Seu nome completo"
                    required
                  />
                </div>

                <div className={styles.profileFieldGroup}>
                  <label htmlFor="profile-email" className={styles.profileLabel}>
                    E-mail
                  </label>
                  <input
                    id="profile-email"
                    name="email"
                    type="email"
                    className={styles.profileInput}
                    value={profileForm.email}
                    onChange={handleProfileInputChange}
                    placeholder="seu@email.com"
                    required
                  />
                </div>

                <div className={styles.profileFieldGroup}>
                  <label htmlFor="profile-novaSenha" className={styles.profileLabel}>
                    Nova Senha <span className={styles.optionalTag}>(opcional • mín. 6 caracteres • só letras e números)</span>
                  </label>
                  <input
                    id="profile-novaSenha"
                    name="novaSenha"
                    type="password"
                    className={styles.profileInput}
                    value={profileForm.novaSenha}
                    onChange={handleProfileInputChange}
                    placeholder="Deixe em branco para manter a senha atual"
                    autoComplete="new-password"
                  />
                </div>

                {profileForm.novaSenha && (
                  <div className={styles.profileFieldGroup}>
                    <label htmlFor="profile-confirmarNovaSenha" className={styles.profileLabel}>
                      Confirmar Nova Senha
                    </label>
                    <input
                      id="profile-confirmarNovaSenha"
                      name="confirmarNovaSenha"
                      type="password"
                      className={styles.profileInput}
                      value={profileForm.confirmarNovaSenha}
                      onChange={handleProfileInputChange}
                      placeholder="Repita a nova senha para confirmar"
                      autoComplete="new-password"
                      required
                    />
                  </div>
                )}

                {/* Caixa destacada de Verificação de Segurança */}
                <div className={styles.securityBox}>
                  <div className={styles.securityBoxHeader}>
                    <Icon name="lock" size={16} />
                    <strong>Verificação de Segurança Obrigatória</strong>
                  </div>
                  <p className={styles.securityBoxHint}>
                    Para proteger sua conta, informe sua <strong>senha atual</strong> para confirmar
                    e salvar as alterações.
                  </p>
                  <input
                    id="profile-senhaAtual"
                    name="senhaAtual"
                    type="password"
                    className={styles.securityInput}
                    value={profileForm.senhaAtual}
                    onChange={handleProfileInputChange}
                    placeholder="Digite sua senha atual..."
                    autoComplete="current-password"
                    required
                  />
                </div>

                <div className={styles.profileFooterActions}>
                  <button
                    type="button"
                    className={styles.profileSecondaryBtn}
                    onClick={() => {
                      setProfileError('');
                      setIsEditingProfile(false);
                    }}
                    disabled={profileSaving}
                  >
                    Voltar
                  </button>
                  <button
                    type="submit"
                    className={styles.profilePrimaryBtn}
                    disabled={profileSaving}
                  >
                    <Icon name="shield" size={16} />
                    <span>{profileSaving ? 'Verificando e Salvando...' : 'Confirmar e Salvar'}</span>
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
