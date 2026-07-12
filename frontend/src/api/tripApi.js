import api from './axios';

export const tripApi = {
  getAll: () => api.get('/trips').then(res => res.data),
  create: (data) => api.post('/trips', data).then(res => res.data),
  dispatch: (id) => api.post(`/trips/${id}/dispatch`).then(res => res.data),
  complete: (id, data) => api.post(`/trips/${id}/complete`, data).then(res => res.data),
  cancel: (id) => api.post(`/trips/${id}/cancel`).then(res => res.data),
};