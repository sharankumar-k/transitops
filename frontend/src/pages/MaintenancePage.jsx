import React, { useState, useEffect } from 'react';
import { maintenanceApi } from '../api/maintenanceApi';
import { vehicleApi } from '../api/vehicleApi';
import StatusBadge from '../components/common/StatusBadge';
import { formatDate, formatCurrency } from '../utils/formatters';
import { Plus, CheckSquare } from 'lucide-react';
import Modal from '../components/common/Modal';
import Alert from '../components/common/Alert';
import { useAuth } from '../context/AuthContext';

const MaintenancePage = () => {
  const { auth } = useAuth();
  const canManageMaintenance = auth?.role === 'FLEET_MANAGER';
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [availableVehicles, setAvailableVehicles] = useState([]);
  const [addForm, setAddForm] = useState({ vehicleId: '', description: '', startDate: new Date().toISOString().split('T')[0] });
  
  const [closeId, setCloseId] = useState(null);
  const [closeForm, setCloseForm] = useState({ endDate: new Date().toISOString().split('T')[0], maintenanceCost: '' });

  const loadData = async () => {
    try { setLoading(true); setRecords(await maintenanceApi.getAll()); }
    catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  useEffect(() => { loadData(); }, []);

  const openAdd = async () => {
    try {
      const v = await vehicleApi.getAvailable();
      setAvailableVehicles(v);
      setAddForm({ vehicleId: '', description: '', startDate: new Date().toISOString().split('T')[0] });
      setIsAddOpen(true);
    } catch (err) { setError('Failed to load vehicles'); }
  };

  const handleAdd = async (e) => {
    e.preventDefault();
    try {
      setLoading(true); setError(null);
      await maintenanceApi.create(addForm);
      setIsAddOpen(false);
      loadData();
    } catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  const handleClose = async (e) => {
    e.preventDefault();
    try {
      setLoading(true); setError(null);
      await maintenanceApi.close(closeId, closeForm);
      setCloseId(null);
      loadData();
    } catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Maintenance Yard</h1>
          <p className="page-subtitle">Manage vehicle repairs and IN_SHOP states.</p>
        </div>
        {canManageMaintenance && <button className="btn btn-primary" onClick={openAdd}><Plus size={16} /> Start Maintenance</button>}
      </div>

      <Alert type="error" message={error} />

      <div className="card table-responsive" style={{ padding: 0 }}>
        <table className="table">
          <thead>
            <tr><th>Vehicle</th><th>Description</th><th>Start Date</th><th>End Date</th><th>Cost</th><th>Status</th><th>Action</th></tr>
          </thead>
          <tbody>
            {records.map(r => (
              <tr key={r.id}>
                <td style={{fontWeight: 600}}>{r.vehicleRegistration}</td>
                <td>{r.description}</td>
                <td>{formatDate(r.startDate)}</td>
                <td>{r.endDate ? formatDate(r.endDate) : '-'}</td>
                <td>{formatCurrency(r.maintenanceCost)}</td>
                <td><StatusBadge status={r.status} /></td>
                <td>
                  {canManageMaintenance && r.status === 'ACTIVE' && (
                    <button className="btn btn-sm btn-secondary" onClick={() => {setCloseId(r.id); setCloseForm({...closeForm, maintenanceCost: ''})}}><CheckSquare size={14}/> Close</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal isOpen={isAddOpen} onClose={() => setIsAddOpen(false)} title="Send to Maintenance">
        <Alert type="warning" message="Starting maintenance moves the vehicle to IN_SHOP and removes it from dispatch availability." />
        <form onSubmit={handleAdd}>
          <div className="form-group">
            <label className="form-label">Vehicle</label>
            <select className="form-control" required value={addForm.vehicleId} onChange={e => setAddForm({...addForm, vehicleId: e.target.value})}>
              <option value="">Select Vehicle...</option>
              {availableVehicles.map(v => <option key={v.id} value={v.id}>{v.registrationNumber}</option>)}
            </select>
          </div>
          <div className="form-group">
            <label className="form-label">Description</label>
            <textarea className="form-control" required value={addForm.description} onChange={e => setAddForm({...addForm, description: e.target.value})} rows="3"></textarea>
          </div>
          <div className="form-group">
            <label className="form-label">Start Date</label>
            <input type="date" className="form-control" required value={addForm.startDate} onChange={e => setAddForm({...addForm, startDate: e.target.value})} />
          </div>
          <div className="modal-footer" style={{margin: '-1.5rem', marginTop: '1.5rem'}}>
            <button type="button" className="btn btn-secondary" onClick={() => setIsAddOpen(false)}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>Start Maintenance</button>
          </div>
        </form>
      </Modal>

      <Modal isOpen={!!closeId} onClose={() => setCloseId(null)} title="Close Maintenance Work">
        <form onSubmit={handleClose}>
          <div className="form-group">
            <label className="form-label">End Date</label>
            <input type="date" className="form-control" required value={closeForm.endDate} onChange={e => setCloseForm({...closeForm, endDate: e.target.value})} />
          </div>
          <div className="form-group">
            <label className="form-label">Total Maintenance Cost</label>
            <input type="number" min="0" step="0.01" className="form-control" required value={closeForm.maintenanceCost} onChange={e => setCloseForm({...closeForm, maintenanceCost: e.target.value})} />
          </div>
          <div className="modal-footer" style={{margin: '-1.5rem', marginTop: '1.5rem'}}>
            <button type="button" className="btn btn-secondary" onClick={() => setCloseId(null)}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>Complete</button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default MaintenancePage;
