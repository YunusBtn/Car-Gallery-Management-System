import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register as registerApi, setToken, setUserDetails } from '../services/api';

export default function RegisterPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('CUSTOMER');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const data = await registerApi(username, password, role);
      setToken(data.token);
      setUserDetails({ username: data.username, roles: data.roles || [], id: data.id });
      navigate('/complete-profile');
    } catch (err) {
      setError(err.message || 'Kayıt başarısız');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <h1 className="login-title">AutoGaleri</h1>
        <p className="login-subtitle">Yeni bir hesap oluşturun</p>

        {error && <div className="error-msg">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Kullanıcı Adı</label>
            <input
              className="form-input"
              type="text"
              placeholder="Bir kullanıcı adı belirleyin"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              autoFocus
            />
          </div>

          <div className="form-group">
            <label className="form-label">Şifre</label>
            <input
              className="form-input"
              type="password"
              placeholder="Bir şifre belirleyin"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Kayıt Türü</label>
            <select
              className="form-input"
              value={role}
              onChange={(e) => setRole(e.target.value)}
            >
              <option value="CUSTOMER">Müşteri</option>
              <option value="GALLERIST">Galerici</option>
            </select>
          </div>

          <button className="login-btn" type="submit" disabled={loading}>
            {loading ? 'Kayıt olunuyor...' : 'Kayıt Ol'}
          </button>
        </form>

        <div style={{ marginTop: '1rem', textAlign: 'center', color: '#999' }}>
          Zaten hesabınız var mı? <Link to="/login" style={{ color: '#64ffda', textDecoration: 'none' }}>Giriş Yap</Link>
        </div>
      </div>
    </div>
  );
}
