import { NavLink, useNavigate } from 'react-router-dom';
import { removeToken, getUserDetails } from '../services/api';

export default function Sidebar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    removeToken();
    navigate('/login');
  };

  const user = getUserDetails();
  const roles = user?.roles || [];
  
  const isAdmin = roles.includes('ADMIN') || roles.includes('MANAGER');
  const isCustomer = roles.includes('CUSTOMER');
  const isGallerist = roles.includes('GALLERIST');
  const isOnlyUser = !isAdmin && !isCustomer && !isGallerist;

  let links = [{ to: '/', icon: '📊', label: 'Dashboard' }];

  if (isAdmin) {
    links.push({ to: '/cars', icon: '🚗', label: 'Araçlar' });
    links.push({ to: '/customers', icon: '👥', label: 'Müşteriler' });
    links.push({ to: '/sales', icon: '💰', label: 'Satışlar' });
  }

  if (isCustomer) {
    links.push({ to: '/buy-car', icon: '🛒', label: 'Araç Satın Al' });
  }

  if (isGallerist) {
    links.push({ to: '/my-cars', icon: '🚘', label: 'İlanlarım' });
  }

  if (isOnlyUser) {
    links.push({ to: '/complete-profile', icon: '⭐', label: 'Profili Tamamla' });
  }

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <div className="sidebar-logo">AutoGaleri</div>
        <div className="sidebar-subtitle">Yönetim Paneli</div>
      </div>

      <nav className="sidebar-nav">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            end={link.to === '/'}
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
          >
            <span className="nav-icon">{link.icon}</span>
            {link.label}
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <button className="logout-btn" onClick={handleLogout}>
          <span className="nav-icon">🚪</span>
          Çıkış Yap
        </button>
      </div>
    </aside>
  );
}
