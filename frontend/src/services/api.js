import axios from 'axios';
import { toast } from 'sonner';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
});

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

// Request Interceptor
api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => Promise.reject(error));

// Response Interceptor
api.interceptors.response.use(
  (response) => {
    // If it's a 204 or empty, just return null
    if (response.status === 204 || response.headers['content-length'] === '0') {
      return null;
    }
    return response.data;
  },
  (error) => {
    let errorMsg = 'Bir hata oluştu';
    if (error.response?.status === 401) {
      removeToken();
      window.location.href = '/login';
      errorMsg = 'Oturum süresi doldu, lütfen tekrar giriş yapın.';
      toast.error(errorMsg);
    } else {
      errorMsg = error.response?.data?.message || error.message || `HTTP ${error.response?.status || 'Bilinmeyen Hata'}`;
      toast.error(errorMsg);
    }
    return Promise.reject(new Error(errorMsg));
  }
);

// Auth
export const login = (username, password) => api.post('/auth/login', { username, password });
export const register = (username, password, role) => api.post('/auth/register', { username, password, role });

// Cars
export const getCars = () => api.get('/car/list');
export const getCarById = (id) => api.get(`/car/${id}`);
export const saveCar = (car) => api.post('/car/save', car);
export const deleteCar = (id) => api.delete(`/car/${id}`);

// Customers
export const getCustomers = () => api.get('/customer/list');
export const getCustomerById = (id) => api.get(`/customer/${id}`);
export const getMyCustomerProfile = () => api.get('/customer/my-profile');
export const saveCustomer = (customer) => api.post('/customer/save', customer);

// Gallerists
export const getGallerists = () => api.get('/gallerist/list');
export const getGalleristById = (id) => api.get(`/gallerist/${id}`);
export const getMyGalleristProfile = () => api.get('/gallerist/my-profile');
export const saveGallerist = (gallerist) => api.post('/gallerist/save', gallerist);

// Gallerist Cars
export const getGalleristCars = () => api.get('/gallerist-car/list');
export const saveGalleristCar = (gc) => api.post('/gallerist-car/save', gc);

// Sold Cars
export const getSoldCars = () => api.get('/sold-car/list');
export const buyCar = (data) => api.post('/sold-car/buy', data);

// Accounts
export const getAccountById = (id) => api.get(`/account/${id}`);
export const saveAccount = (account) => api.post('/account/save', account);

// Addresses
export const getAddressById = (id) => api.get(`/address/${id}`);
export const saveAddress = (address) => api.post('/address/save', address);
