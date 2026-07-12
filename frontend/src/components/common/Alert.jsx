import React from 'react';
import { AlertCircle, CheckCircle } from 'lucide-react';

const Alert = ({ type = 'error', message }) => {
  if (!message) return null;
  return (
    <div className={`alert alert-${type}`}>
      {type === 'error' ? <AlertCircle size={18} /> : <CheckCircle size={18} />}
      <span>{message}</span>
    </div>
  );
};

export default Alert;