const API_BASE = 'http://localhost:8080/api';

// Token yönetimi
export const getToken = () => localStorage.getItem('token');
export const setToken = (token) => localStorage.setItem('token', token);
export const removeToken = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
};
export const setUserDetails = (user) => localStorage.setItem('user', JSON.stringify(user));
export const getUserDetails = () => {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
};
export const isLoggedIn = () => !!getToken();

// Ortak fetch helper
const request = async (endpoint, options = {}) => {
  const token = getToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token && { Authorization: `Bearer ${token}` }),
    ...options.headers,
  };

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    removeToken();
    window.location.href = '/login';
    throw new Error('Oturum süresi doldu');
  }

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Bir hata oluştu' }));
    throw new Error(error.message || `HTTP ${response.status}`);
  }

  if (response.status === 204 || response.headers.get('content-length') === '0') {
    return null;
  }

  return response.json();
};

// Auth
export const login = (username, password) =>
  request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });

export const register = (username, password, role) =>
  request('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username, password, role }),
  });

// Cars
export const getCars = () => request('/car/list');
export const getCarById = (id) => request(`/car/${id}`);
export const saveCar = (car) =>
  request('/car/save', { method: 'POST', body: JSON.stringify(car) });
export const deleteCar = (id) =>
  request(`/car/${id}`, { method: 'DELETE' });

// Customers
export const getCustomers = () => request('/customer/list');
export const getCustomerById = (id) => request(`/customer/${id}`);
export const getMyCustomerProfile = () => request('/customer/my-profile');
export const saveCustomer = (customer) =>
  request('/customer/save', { method: 'POST', body: JSON.stringify(customer) });

// Gallerists
export const getGallerists = () => request('/gallerist/list');
export const getGalleristById = (id) => request(`/gallerist/${id}`);
export const getMyGalleristProfile = () => request('/gallerist/my-profile');
export const saveGallerist = (gallerist) =>
  request('/gallerist/save', { method: 'POST', body: JSON.stringify(gallerist) });

// Gallerist Cars
export const getGalleristCars = () => request('/gallerist-car/list');
export const saveGalleristCar = (gc) =>
  request('/gallerist-car/save', { method: 'POST', body: JSON.stringify(gc) });

// Sold Cars
export const getSoldCars = () => request('/sold-car/list');
export const buyCar = (data) =>
  request('/sold-car/buy', { method: 'POST', body: JSON.stringify(data) });

// Accounts
export const getAccountById = (id) => request(`/account/${id}`);
export const saveAccount = (account) =>
  request('/account/save', { method: 'POST', body: JSON.stringify(account) });

// Addresses
export const getAddressById = (id) => request(`/address/${id}`);
export const saveAddress = (address) =>
  request('/address/save', { method: 'POST', body: JSON.stringify(address) });
