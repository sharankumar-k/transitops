import api from './axios';

export const maintenanceApi = {
  getAll: () => api.get('/maintenance').then(res => res.data),
  create: (data) => api.post('/maintenance', data).then(res => res.data),
  close: (id, data) => api.post(`/maintenance/${id}/close`, data).then(res => res.data),
};