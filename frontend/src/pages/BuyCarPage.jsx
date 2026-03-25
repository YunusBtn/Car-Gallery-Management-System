import { useState, useEffect } from 'react';
import { getCars, buyCar } from '../services/api';

export default function BuyCarPage() {
  const [cars, setCars] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [buyingId, setBuyingId] = useState(null);

  const fetchCars = async () => {
    try {
      const data = await getCars();
      // Yalnızca satılık olanları göster
      const salableCars = data.filter(car => car.carStatusType === 'SALABLE');
      setCars(salableCars);
    } catch (err) {
      console.error(err);
      setError('Araçlar yüklenemedi.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCars();
  }, []);

  const handleBuy = async (carId) => {
    if (!window.confirm('Bu aracı satın almak istediğinize emin misiniz?')) return;
    
    setBuyingId(carId);
    try {
      // Backend'deki DtoSoldCarIU yapısına uygun mock veri gönderiyoruz
      // Gerçek bir senaryoda customerId giriş yapmış kullanıcıdan alınır.
      await buyCar({
        galleristId: 1, // Mock
        carId: carId,
        customerId: 1 // Mock
      });
      setSuccess('Tebrikler! Aracı başarıyla satın aldınız.');
      fetchCars(); // Listeyi yenile
    } catch (err) {
      // Eger backend hatasi varsa gosteriyoruz
      setError(err.message || 'Satın alma işlemi başarısız oldu.');
    } finally {
      setBuyingId(null);
      setTimeout(() => { setSuccess(''); setError(''); }, 4000);
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Araç Galerisi</h1>
        <p className="page-subtitle">Hayalinizdeki aracı hemen satın alın</p>
      </div>

      {success && <div className="success-msg">{success}</div>}
      {error && <div className="error-msg">{error}</div>}

      <div className="stats-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))' }}>
        {cars.length === 0 ? (
          <div className="empty-state" style={{ gridColumn: '1 / -1' }}>
            <div className="empty-icon">🚗</div>
            <p className="empty-text">Şu an satılık araç bulunmuyor</p>
          </div>
        ) : (
          cars.map((car) => (
            <div key={car.id} className="stat-card" style={{ display: 'flex', flexDirection: 'column', gap: '1rem', alignItems: 'flex-start' }}>
              <div style={{ width: '100%', height: '180px', background: '#2a2a2a', borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '3rem' }}>
                🚘
              </div>
              <div>
                <h3 style={{ margin: 0, color: '#e0e0e0', fontSize: '1.25rem' }}>{car.brand} {car.model}</h3>
                <p style={{ margin: '0.25rem 0 0 0', color: '#888', fontSize: '0.9rem' }}>Plaka: {car.plaka} • {car.productionYear}</p>
              </div>
              <div style={{ marginTop: 'auto', width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#64ffda' }}>
                  {Number(car.price).toLocaleString('tr-TR')} {car.currencyType}
                </span>
                <button 
                  className="btn btn-primary" 
                  onClick={() => handleBuy(car.id)}
                  disabled={buyingId === car.id}
                >
                  {buyingId === car.id ? 'İşleniyor...' : 'Satın Al'}
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
