import React from 'react';

const statusMap = {
  AVAILABLE: 'badge-success',
  ON_TRIP: 'badge-primary',
  IN_SHOP: 'badge-warning',
  RETIRED: 'badge-neutral',
  OFF_DUTY: 'badge-neutral',
  SUSPENDED: 'badge-danger',
  DRAFT: 'badge-warning',
  DISPATCHED: 'badge-primary',
  COMPLETED: 'badge-success',
  CANCELLED: 'badge-danger',
  ACTIVE: 'badge-primary',
  CLOSED: 'badge-neutral'
};

const StatusBadge = ({ status }) => {
  if (!status) return null;
  const badgeClass = statusMap[status] || 'badge-neutral';
  return <span className={`badge ${badgeClass}`}>{status.replace('_', ' ')}</span>;
};

export default StatusBadge;