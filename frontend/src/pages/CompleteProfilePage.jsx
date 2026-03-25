import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { saveAddress, saveAccount, saveCustomer, saveGallerist, getUserDetails, removeToken } from '../services/api';

export default function CompleteProfilePage() {
  const [role, setRole] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  // Form states
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [tckn, setTckn] = useState('');
  const [birthOfDate, setBirthOfDate] = useState(''); // dd/MM/yyyy expected by backend
  const [city, setCity] = useState('');
  const [district, setDistrict] = useState('');
  const [neighborhood, setNeighborhood] = useState('');
  const [street, setStreet] = useState('');

  // Account states
  const [accountNo, setAccountNo] = useState('');
  const [iban, setIban] = useState('');
  const [amount, setAmount] = useState('');
  const [currencyType, setCurrencyType] = useState('TL');

  useEffect(() => {
    const user = getUserDetails();
    if (!user || !user.roles) {
      navigate('/login');
      return;
    }
    if (user.roles.includes('CUSTOMER')) {
      setRole('CUSTOMER');
    } else if (user.roles.includes('GALLERIST')) {
      setRole('GALLERIST');
    } else {
      // ADMIN or basic USER
      navigate('/');
    }
  }, [navigate]);

  const handleDateChange = (e) => {
    const val = e.target.value;
    if(val) {
      const parts = val.split('-');
      if(parts.length === 3) {
        setBirthOfDate(`${parts[2]}/${parts[1]}/${parts[0]}`);
        return;
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // 1. Save Address First
      const addressReq = { city, district, neighborhood, street };
      const addressRes = await saveAddress(addressReq);
      
      if (role === 'CUSTOMER') {
        // 2. Save Mock Account for Customer
        const accountReq = { 
          accountNo, 
          iban, 
          amount: Number(amount), 
          currencyType 
        };
        const accountRes = await saveAccount(accountReq);

        // 3. Save Customer
        const customerReq = {
          firstName,
          lastName,
          tckn,
          birthOfDate,
          addressId: addressRes.id,
          accountId: accountRes.id
        };
        await saveCustomer(customerReq);

      } else if (role === 'GALLERIST') {
        // 3. Save Gallerist
        const galleristReq = {
          firstName,
          lastName,
          addressId: addressRes.id
        };
        await saveGallerist(galleristReq);
      }

      alert("Profiliniz başarıyla tamamlandı. Yeniden giriş yapmanıza gerek yok, devam edebilirsiniz.");
      navigate('/');

    } catch (err) {
      setError(err.message || 'Bir hata oluştu.');
    } finally {
      setLoading(false);
    }
  };

  if (!role) return null;

  return (
    <div className="page-header">
      <h1 className="page-title">{role === 'CUSTOMER' ? 'Müşteri Profilini Tamamla' : 'Galerici Profilini Tamamla'}</h1>
      <p className="page-subtitle">Sistemi kullanmaya başlamadan önce lütfen profil bilgilerinizi eksiksiz doldurun.</p>

      {error && <div className="error-msg" style={{ maxWidth: '500px', margin: '1rem 0' }}>{error}</div>}

      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem', maxWidth: '500px', marginTop: '2rem' }}>
        <input className="form-input" placeholder="Ad" value={firstName} onChange={e => setFirstName(e.target.value)} required />
        <input className="form-input" placeholder="Soyad" value={lastName} onChange={e => setLastName(e.target.value)} required />
        
        {role === 'CUSTOMER' && (
          <>
            <input className="form-input" placeholder="TC Kimlik No" value={tckn} onChange={e => setTckn(e.target.value)} required />
            <div>
              <label className="form-label">Doğum Tarihi:</label>
              <input className="form-input" type="date" onChange={handleDateChange} required />
            </div>

            <div style={{ marginTop: '1rem', marginBottom: '0.5rem', fontWeight: 'bold', color: '#64ffda' }}>Hesap Bilgileri</div>
            <input className="form-input" placeholder="Hesap No (Örn: 12345678)" value={accountNo} onChange={e => setAccountNo(e.target.value)} required />
            <input className="form-input" placeholder="IBAN (Örn: TR12...)" value={iban} onChange={e => setIban(e.target.value)} required />
            <div style={{ display: 'flex', gap: '1rem' }}>
              <input className="form-input" placeholder="Bakiye (Örn: 500000)" type="number" step="0.01" value={amount} onChange={e => setAmount(e.target.value)} required />
              <select className="form-input" value={currencyType} onChange={e => setCurrencyType(e.target.value)} required>
                <option value="TL">TL</option>
                <option value="USD">USD</option>
                <option value="EUR">EUR</option>
              </select>
            </div>
          </>
        )}

        <div style={{ display: 'flex', gap: '1rem' }}>
          <input className="form-input" placeholder="İl (Örn: İstanbul)" value={city} onChange={e => setCity(e.target.value)} required />
          <input className="form-input" placeholder="İlçe (Örn: Kadıköy)" value={district} onChange={e => setDistrict(e.target.value)} required />
        </div>

        <div style={{ display: 'flex', gap: '1rem' }}>
          <input className="form-input" placeholder="Mahalle" value={neighborhood} onChange={e => setNeighborhood(e.target.value)} required />
          <input className="form-input" placeholder="Sokak/Cadde" value={street} onChange={e => setStreet(e.target.value)} required />
        </div>

        <div style={{ display: 'flex', gap: '1rem' }}>
          <button className="login-btn" type="submit" disabled={loading}>{loading ? 'Kaydediliyor...' : 'Tamamla'}</button>
        </div>
      </form>
    </div>
  );
}
