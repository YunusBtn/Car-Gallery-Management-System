import { useState, useEffect } from 'react';
import { getCars, saveCar, deleteCar } from '../services/api';
import Modal from '../components/Modal';

const EMPTY_CAR = {
  plaka: '',
  brand: '',
  model: '',
  productionYear: 2024,
  price: '',
  currencyType: 'USD',
  damagePrice: '0',
  carStatusType: 'SALABLE',
};

export default function CarsPage() {
  const [cars, setCars] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState(EMPTY_CAR);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const fetchCars = async () => {
    try {
      const data = await getCars();
      setCars(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCars();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await saveCar({
        ...form,
        productionYear: Number(form.productionYear),
        price: Number(form.price),
        damagePrice: Number(form.damagePrice),
      });
      setShowModal(false);
      setForm(EMPTY_CAR);
      setSuccess('Araç başarıyla eklendi!');
      setTimeout(() => setSuccess(''), 3000);
      fetchCars();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Bu aracı silmek istediğinize emin misiniz?')) return;
    try {
      await deleteCar(id);
      setSuccess('Araç silindi!');
      setTimeout(() => setSuccess(''), 3000);
      fetchCars();
    } catch (err) {
      setError(err.message);
      setTimeout(() => setError(''), 3000);
    }
  };

  const statusBadge = (status) => {
    switch (status) {
      case 'SALABLE': return <span className="badge badge-success">Satılık</span>;
      case 'SOLD': return <span className="badge badge-danger">Satıldı</span>;
      case 'DAMAGED': return <span className="badge badge-warning">Hasarlı</span>;
      default: return <span className="badge">{status}</span>;
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Araçlar</h1>
        <p className="page-subtitle">Galerinizdeki tüm araçları yönetin</p>
      </div>

      {success && <div className="success-msg">{success}</div>}
      {error && <div className="error-msg">{error}</div>}

      <div className="table-container">
        <div className="table-header">
          <h3 className="table-title">Araç Listesi ({cars.length})</h3>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>
            + Yeni Araç
          </button>
        </div>

        {cars.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">🚗</div>
            <p className="empty-text">Henüz araç eklenmemiş</p>
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Plaka</th>
                <th>Marka</th>
                <th>Model</th>
                <th>Yıl</th>
                <th>Fiyat</th>
                <th>Durum</th>
                <th>İşlem</th>
              </tr>
            </thead>
            <tbody>
              {cars.map((car) => (
                <tr key={car.id}>
                  <td>#{car.id}</td>
                  <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{car.plaka}</td>
                  <td>{car.brand}</td>
                  <td>{car.model}</td>
                  <td>{car.productionYear}</td>
                  <td style={{ fontWeight: 600 }}>
                    {Number(car.price).toLocaleString('tr-TR')} {car.currencyType}
                  </td>
                  <td>{statusBadge(car.carStatusType)}</td>
                  <td>
                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDelete(car.id)}
                    >
                      Sil
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {showModal && (
        <Modal
          title="Yeni Araç Ekle"
          onClose={() => { setShowModal(false); setError(''); }}
          footer={
            <>
              <button className="btn btn-ghost" onClick={() => setShowModal(false)}>İptal</button>
              <button className="btn btn-primary" onClick={handleSubmit}>Kaydet</button>
            </>
          }
        >
          {error && <div className="error-msg">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Plaka</label>
                <input className="form-input" name="plaka" value={form.plaka} onChange={handleChange} placeholder="34 ABC 123" required />
              </div>
              <div className="form-group">
                <label className="form-label">Marka</label>
                <input className="form-input" name="brand" value={form.brand} onChange={handleChange} placeholder="BMW" required />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Model</label>
                <input className="form-input" name="model" value={form.model} onChange={handleChange} placeholder="320i" required />
              </div>
              <div className="form-group">
                <label className="form-label">Üretim Yılı</label>
                <input className="form-input" name="productionYear" type="number" value={form.productionYear} onChange={handleChange} required />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Fiyat</label>
                <input className="form-input" name="price" type="number" step="0.01" value={form.price} onChange={handleChange} placeholder="50000" required />
              </div>
              <div className="form-group">
                <label className="form-label">Para Birimi</label>
                <select className="form-select" name="currencyType" value={form.currencyType} onChange={handleChange}>
                  <option value="USD">USD</option>
                  <option value="TL">TL</option>
                  <option value="EUR">EUR</option>
                </select>
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Hasar Bedeli</label>
                <input className="form-input" name="damagePrice" type="number" step="0.01" value={form.damagePrice} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label className="form-label">Durum</label>
                <select className="form-select" name="carStatusType" value={form.carStatusType} onChange={handleChange}>
                  <option value="SALABLE">Satılık</option>
                  <option value="DAMAGED">Hasarlı</option>
                </select>
              </div>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
