import React from 'react';
import Modal from './Modal';

const ConfirmDialog = ({ isOpen, onClose, onConfirm, title, message, confirmText = 'Confirm', variant = 'danger', loading = false }) => {
  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title}>
      <p className="mb-6">{message}</p>
      <div className="flex-between">
        <button className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
        <button className={`btn btn-${variant}`} onClick={onConfirm} disabled={loading}>
          {loading ? 'Processing...' : confirmText}
        </button>
      </div>
    </Modal>
  );
};

export default ConfirmDialog;