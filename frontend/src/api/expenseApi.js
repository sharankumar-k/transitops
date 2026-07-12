import api from './axios';

export const expenseApi = {
  getAll: () => api.get('/expenses').then(res => res.data),
  create: (data) => api.post('/expenses', data).then(res => res.data),
};