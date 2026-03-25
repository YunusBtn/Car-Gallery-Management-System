import { useState, useEffect } from 'react';
import { getCustomers, saveCustomer } from '../services/api';
import Modal from '../components/Modal';

const EMPTY_CUSTOMER = {
  firstName: '',
  lastName: '',
  tckn: '',
  birthOfDate: '',
  addressId: '',
  accountId: '',
};

export default function CustomersPage() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState(EMPTY_CUSTOMER);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const fetchCustomers = async () => {
    try {
      const data = await getCustomers();
      setCustomers(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await saveCustomer({
        ...form,
        addressId: Number(form.addressId),
        accountId: Number(form.accountId),
      });
      setShowModal(false);
      setForm(EMPTY_CUSTOMER);
      setSuccess('Müşteri başarıyla eklendi!');
      setTimeout(() => setSuccess(''), 3000);
      fetchCustomers();
    } catch (err) {
      setError(err.message);
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Müşteriler</h1>
        <p className="page-subtitle">Kayıtlı müşterilerinizi yönetin</p>
      </div>

      {success && <div className="success-msg">{success}</div>}
      {error && <div className="error-msg">{error}</div>}

      <div className="table-container">
        <div className="table-header">
          <h3 className="table-title">Müşteri Listesi ({customers.length})</h3>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>
            + Yeni Müşteri
          </button>
        </div>

        {customers.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">👥</div>
            <p className="empty-text">Henüz müşteri kaydı yok</p>
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Ad</th>
                <th>Soyad</th>
                <th>TCKN</th>
                <th>Doğum Tarihi</th>
                <th>Bakiye</th>
                <th>Şehir</th>
              </tr>
            </thead>
            <tbody>
              {customers.map((c) => (
                <tr key={c.id}>
                  <td>#{c.id}</td>
                  <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{c.firstName}</td>
                  <td>{c.lastName}</td>
                  <td>{c.tckn}</td>
                  <td>{c.birthOfDate || '-'}</td>
                  <td style={{ fontWeight: 600 }}>
                    {c.account ? `${Number(c.account.amount).toLocaleString('tr-TR')} ${c.account.currencyType}` : '-'}
                  </td>
                  <td>{c.address?.city || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {showModal && (
        <Modal
          title="Yeni Müşteri Ekle"
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
                <label className="form-label">Ad</label>
                <input className="form-input" name="firstName" value={form.firstName} onChange={handleChange} placeholder="Ahmet" required />
              </div>
              <div className="form-group">
                <label className="form-label">Soyad</label>
                <input className="form-input" name="lastName" value={form.lastName} onChange={handleChange} placeholder="Yılmaz" required />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">TCKN</label>
                <input className="form-input" name="tckn" value={form.tckn} onChange={handleChange} placeholder="12345678901" required />
              </div>
              <div className="form-group">
                <label className="form-label">Doğum Tarihi</label>
                <input className="form-input" name="birthOfDate" value={form.birthOfDate} onChange={handleChange} placeholder="01/01/1990" required />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Adres ID</label>
                <input className="form-input" name="addressId" type="number" value={form.addressId} onChange={handleChange} placeholder="1" required />
              </div>
              <div className="form-group">
                <label className="form-label">Hesap ID</label>
                <input className="form-input" name="accountId" type="number" value={form.accountId} onChange={handleChange} placeholder="1" required />
              </div>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
