export const formatCurrency = (amount) => {
  if (amount == null) return '-';
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(amount);
};

export const formatNumber = (num) => {
  if (num == null) return '-';
  return new Intl.NumberFormat('en-IN').format(num);
};

export const formatDate = (dateString) => {
  if (!dateString) return '-';
  return new Date(dateString).toLocaleDateString('en-IN', {
    year: 'numeric', month: 'short', day: 'numeric'
  });
};

export const formatDateTime = (dateString) => {
  if (!dateString) return '-';
  return new Date(dateString).toLocaleString('en-IN', {
    year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
  });
};