import api from './axios';

export const vehicleApi = {
  getAll: () => api.get('/vehicles').then(res => res.data),
  getAvailable: () => api.get('/vehicles/available').then(res => res.data),
  search: (params) => api.get('/vehicles/search', { params }).then(res => res.data),
  create: (data) => api.post('/vehicles', data).then(res => res.data),
  update: (id, data) => api.put(`/vehicles/${id}`, data).then(res => res.data),
  delete: (id) => api.delete(`/vehicles/${id}`).then(res => res.data),
};