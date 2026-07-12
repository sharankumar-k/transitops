import api from './axios';

export const driverApi = {
  getAll: () => api.get('/drivers').then(res => res.data),
  getAvailable: () => api.get('/drivers/available').then(res => res.data),
  search: (params) => api.get('/drivers/search', { params }).then(res => res.data),
  create: (data) => api.post('/drivers', data).then(res => res.data),
  update: (id, data) => api.put(`/drivers/${id}`, data).then(res => res.data),
  delete: (id) => api.delete(`/drivers/${id}`).then(res => res.data),
};