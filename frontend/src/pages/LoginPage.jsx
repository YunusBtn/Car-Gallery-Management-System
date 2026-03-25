import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login, setToken, setUserDetails } from '../services/api';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const data = await login(username, password);
      setToken(data.token);
      setUserDetails({ username: data.username, roles: data.roles || [], id: data.id });
      navigate('/');
    } catch (err) {
      setError(err.message || 'Giriş başarısız');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <h1 className="login-title">AutoGaleri</h1>
        <p className="login-subtitle">Yönetim paneline giriş yapın</p>

        {error && <div className="error-msg">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Kullanıcı Adı</label>
            <input
              className="form-input"
              type="text"
              placeholder="Kullanıcı adınızı girin"
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
              placeholder="Şifrenizi girin"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button className="login-btn" type="submit" disabled={loading}>
            {loading ? 'Giriş yapılıyor...' : 'Giriş Yap'}
          </button>
        </form>
        
        <div style={{ marginTop: '1rem', textAlign: 'center', color: '#999' }}>
          Hesabınız yok mu? <Link to="/register" style={{ color: '#64ffda', textDecoration: 'none' }}>Kayıt Ol</Link>
        </div>
      </div>
    </div>
  );
}
