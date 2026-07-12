import api from './axios';

export const reportApi = {
  getVehicleAnalytics: (id) => api.get(`/reports/vehicle/${id}`).then(res => res.data),
  downloadCsv: async () => {
    const response = await api.get('/reports/vehicles/csv', { responseType: 'blob' });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'vehicle-analytics.csv');
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  }
};