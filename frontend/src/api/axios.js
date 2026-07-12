import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('email');
      localStorage.removeItem('role');

      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }

    let errorMessage = 'An unexpected error occurred.';

    if (error.response && error.response.data) {
      errorMessage =
        error.response.data.message ||
        error.response.data.error ||
        errorMessage;
    } else if (error.message) {
      errorMessage = error.message;
    }

    return Promise.reject(new Error(errorMessage));
  }
);

export default api;