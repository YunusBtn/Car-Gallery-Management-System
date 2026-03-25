import { useState, useEffect } from 'react';
import { getCars, getCustomers, getSoldCars, getGallerists, getUserDetails } from '../services/api';

export default function DashboardPage() {
  const [stats, setStats] = useState({ cars: 0, customers: 0, sales: 0, gallerists: 0 });
  const [recentSales, setRecentSales] = useState([]);
  const [loading, setLoading] = useState(true);
  const user = getUserDetails();
  const isAdmin = user?.roles?.includes('ADMIN') || user?.roles?.includes('MANAGER');

  useEffect(() => {
    if (!isAdmin) {
      setLoading(false);
      return;
    }
    const fetchData = async () => {
      try {
        const [cars, customers, sales, gallerists] = await Promise.all([
          getCars(),
          getCustomers(),
          getSoldCars(),
          getGallerists(),
        ]);
        setStats({
          cars: cars.length,
          customers: customers.length,
          sales: sales.length,
          gallerists: gallerists.length,
        });
        setRecentSales(sales.slice(-5).reverse());
      } catch (err) {
        console.error('Dashboard verisi yüklenemedi:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [isAdmin]);

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
      </div>
    );
  }

  if (!isAdmin) {
    let roleText = 'Kullanıcı';
    if (user?.roles?.includes('CUSTOMER')) roleText = 'Müşteri';
    if (user?.roles?.includes('GALLERIST')) roleText = 'Galerici';

    return (
      <div>
        <div className="page-header">
          <h1 className="page-title">Hoş Geldiniz, {user?.username}</h1>
          <p className="page-subtitle">Sisteme {roleText} olarak giriş yaptınız. Sol menüden işlemlerinize devam edebilirsiniz.</p>
        </div>
        <div className="stats-grid" style={{ marginTop: '2rem' }}>
          <div className="stat-card" style={{ border: '1px solid #64ffda' }}>
            <div className="stat-icon">👋</div>
            <div className="stat-value">İyi Günler!</div>
            <div className="stat-label">İşlemleriniz için menüyü kullanın</div>
          </div>
        </div>
      </div>
    );
  }

  const statCards = [
    { icon: '🚗', value: stats.cars, label: 'Toplam Araç' },
    { icon: '👥', value: stats.customers, label: 'Toplam Müşteri' },
    { icon: '💰', value: stats.sales, label: 'Toplam Satış' },
    { icon: '🏢', value: stats.gallerists, label: 'Galerici' },
  ];

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Admin Dashboard</h1>
        <p className="page-subtitle">Galeri yönetim sisteminizin genel görünümü</p>
      </div>

      <div className="stats-grid">
        {statCards.map((s, i) => (
          <div className="stat-card" key={i}>
            <div className="stat-icon">{s.icon}</div>
            <div className="stat-value">{s.value}</div>
            <div className="stat-label">{s.label}</div>
          </div>
        ))}
      </div>

      <div className="table-container">
        <div className="table-header">
          <h3 className="table-title">Son Satışlar</h3>
        </div>
        {recentSales.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">📋</div>
            <p className="empty-text">Henüz satış kaydı yok</p>
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Müşteri</th>
                <th>Araç</th>
                <th>Galerici</th>
                <th>Tarih</th>
              </tr>
            </thead>
            <tbody>
              {recentSales.map((sale) => (
                <tr key={sale.id}>
                  <td>#{sale.id}</td>
                  <td>{sale.customer?.firstName} {sale.customer?.lastName}</td>
                  <td>{sale.car?.brand} {sale.car?.model}</td>
                  <td>{sale.gallerist?.firstName} {sale.gallerist?.lastName}</td>
                  <td>{sale.createTime ? new Date(sale.createTime).toLocaleDateString('tr-TR') : '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
