import api from './axios';

export const dashboardApi = {
  getStats: (params) => api.get('/dashboard', { params }).then(res => res.data),
};