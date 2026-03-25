import { useState, useEffect } from 'react';
import { getSoldCars, buyCar, getCars, getCustomers, getGallerists } from '../services/api';
import Modal from '../components/Modal';

export default function SalesPage() {
  const [sales, setSales] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [cars, setCars] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [gallerists, setGallerists] = useState([]);
  const [form, setForm] = useState({ customerId: '', galleristId: '', carId: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const fetchSales = async () => {
    try {
      const data = await getSoldCars();
      setSales(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSales();
  }, []);

  const openModal = async () => {
    try {
      const [c, cu, g] = await Promise.all([getCars(), getCustomers(), getGallerists()]);
      setCars(c.filter((car) => car.carStatusType !== 'SOLD'));
      setCustomers(cu);
      setGallerists(g);
      setShowModal(true);
    } catch (err) {
      setError('Veriler yüklenemedi: ' + err.message);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await buyCar({
        customerId: Number(form.customerId),
        galleristId: Number(form.galleristId),
        carId: Number(form.carId),
      });
      setShowModal(false);
      setForm({ customerId: '', galleristId: '', carId: '' });
      setSuccess('Satış başarıyla tamamlandı!');
      setTimeout(() => setSuccess(''), 3000);
      fetchSales();
    } catch (err) {
      setError(err.message);
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Satışlar</h1>
        <p className="page-subtitle">Araç satış geçmişini görüntüleyin ve yeni satış yapın</p>
      </div>

      {success && <div className="success-msg">{success}</div>}
      {error && !showModal && <div className="error-msg">{error}</div>}

      <div className="table-container">
        <div className="table-header">
          <h3 className="table-title">Satış Geçmişi ({sales.length})</h3>
          <button className="btn btn-primary" onClick={openModal}>
            + Yeni Satış
          </button>
        </div>

        {sales.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">💰</div>
            <p className="empty-text">Henüz satış kaydı yok</p>
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Müşteri</th>
                <th>Araç</th>
                <th>Fiyat</th>
                <th>Galerici</th>
                <th>Tarih</th>
              </tr>
            </thead>
            <tbody>
              {sales.map((sale) => (
                <tr key={sale.id}>
                  <td>#{sale.id}</td>
                  <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                    {sale.customer?.firstName} {sale.customer?.lastName}
                  </td>
                  <td>{sale.car?.brand} {sale.car?.model}</td>
                  <td style={{ fontWeight: 600 }}>
                    {sale.car ? `${Number(sale.car.price).toLocaleString('tr-TR')} ${sale.car.currencyType}` : '-'}
                  </td>
                  <td>{sale.gallerist?.firstName} {sale.gallerist?.lastName}</td>
                  <td>{sale.createTime ? new Date(sale.createTime).toLocaleDateString('tr-TR') : '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {showModal && (
        <Modal
          title="Yeni Satış Yap"
          onClose={() => { setShowModal(false); setError(''); }}
          footer={
            <>
              <button className="btn btn-ghost" onClick={() => setShowModal(false)}>İptal</button>
              <button className="btn btn-primary" onClick={handleSubmit}>Satışı Onayla</button>
            </>
          }
        >
          {error && <div className="error-msg">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Müşteri</label>
              <select className="form-select" value={form.customerId} onChange={(e) => setForm({ ...form, customerId: e.target.value })} required>
                <option value="">Müşteri Seçiniz</option>
                {customers.map((c) => (
                  <option key={c.id} value={c.id}>{c.firstName} {c.lastName} ({c.tckn})</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Araç</label>
              <select className="form-select" value={form.carId} onChange={(e) => setForm({ ...form, carId: e.target.value })} required>
                <option value="">Araç Seçiniz</option>
                {cars.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.brand} {c.model} — {c.plaka} ({Number(c.price).toLocaleString('tr-TR')} {c.currencyType})
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Galerici</label>
              <select className="form-select" value={form.galleristId} onChange={(e) => setForm({ ...form, galleristId: e.target.value })} required>
                <option value="">Galerici Seçiniz</option>
                {gallerists.map((g) => (
                  <option key={g.id} value={g.id}>{g.firstName} {g.lastName}</option>
                ))}
              </select>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
