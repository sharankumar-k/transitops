import React, { useState, useEffect } from 'react';
import { tripApi } from '../api/tripApi';
import { vehicleApi } from '../api/vehicleApi';
import { driverApi } from '../api/driverApi';
import StatusBadge from '../components/common/StatusBadge';
import { formatNumber, formatDate } from '../utils/formatters';
import { Plus, Play, CheckCircle, XCircle } from 'lucide-react';
import Modal from '../components/common/Modal';
import ConfirmDialog from '../components/common/ConfirmDialog';
import Alert from '../components/common/Alert';
import { useAuth } from '../context/AuthContext';

const TripsPage = () => {
  const { auth } = useAuth();
  const canManageTrips = ['FLEET_MANAGER', 'DRIVER'].includes(auth?.role);
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Modals state
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [dispatchId, setDispatchId] = useState(null);
  const [cancelId, setCancelId] = useState(null);
  const [completeId, setCompleteId] = useState(null);

  // Form Resources
  const [availableVehicles, setAvailableVehicles] = useState([]);
  const [availableDrivers, setAvailableDrivers] = useState([]);
  const [createForm, setCreateForm] = useState({ source: '', destination: '', vehicleId: '', driverId: '', cargoWeight: '', plannedDistance: '', revenue: '' });
  const [completeForm, setCompleteForm] = useState({ finalOdometer: '', fuelLiters: '', fuelCost: '', revenue: '' });

  const loadTrips = async () => {
    try { setLoading(true); setTrips(await tripApi.getAll()); }
    catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  useEffect(() => { loadTrips(); }, []);

  const openCreateModal = async () => {
    setCreateForm({ source: '', destination: '', vehicleId: '', driverId: '', cargoWeight: '', plannedDistance: '', revenue: '' });
    setError(null);
    try {
      const [vRes, dRes] = await Promise.all([vehicleApi.getAvailable(), driverApi.getAvailable()]);
      setAvailableVehicles(vRes);
      setAvailableDrivers(dRes);
      setIsCreateOpen(true);
    } catch (err) { setError('Failed to load available resources.'); }
  };

  const selectedVehicle = availableVehicles.find(v => v.id.toString() === createForm.vehicleId);
  const cargoExceedsCapacity = selectedVehicle && Number(createForm.cargoWeight) > selectedVehicle.maximumLoadCapacity;

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      setLoading(true); setError(null);
      await tripApi.create(createForm);
      setSuccess('Draft trip created successfully.');
      setIsCreateOpen(false);
      loadTrips();
    } catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  const handleDispatch = async () => {
    try {
      setLoading(true); setError(null);
      await tripApi.dispatch(dispatchId);
      setSuccess('Trip dispatched successfully.');
      setDispatchId(null);
      loadTrips();
    } catch (err) { setError(err.message); setDispatchId(null); }
    finally { setLoading(false); }
  };

  const handleComplete = async (e) => {
    e.preventDefault();
    try {
      setLoading(true); setError(null);
      await tripApi.complete(completeId, completeForm);
      setSuccess('Trip completed successfully.');
      setCompleteId(null);
      loadTrips();
    } catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  const handleCancel = async () => {
    try {
      setLoading(true); setError(null);
      await tripApi.cancel(cancelId);
      setSuccess('Trip cancelled.');
      setCancelId(null);
      loadTrips();
    } catch (err) { setError(err.message); setCancelId(null); }
    finally { setLoading(false); }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Dispatch Control</h1>
          <p className="page-subtitle">Manage lifecycle of trips and routing.</p>
        </div>
        {canManageTrips && <button className="btn btn-primary" onClick={openCreateModal}><Plus size={16} /> Create Trip</button>}
      </div>

      <Alert type="error" message={error} />
      <Alert type="success" message={success} />

      <div className="card table-responsive" style={{ padding: 0 }}>
        <table className="table">
          <thead>
            <tr>
              <th>ID</th><th>Route</th><th>Vehicle</th><th>Driver</th><th>Cargo</th><th>Status</th><th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {trips.map(t => (
              <tr key={t.id}>
                <td style={{fontWeight: 600}}>#{t.id}</td>
                <td>{t.source} <br/><span className="text-muted">to</span> {t.destination}</td>
                <td>{t.vehicleRegistration}</td>
                <td>{t.driverName}</td>
                <td>{formatNumber(t.cargoWeight)} kg</td>
                <td><StatusBadge status={t.status} /></td>
                <td>
                  {canManageTrips && <div className="table-actions">
                    {t.status === 'DRAFT' && (
                      <button className="btn btn-sm btn-primary" onClick={() => setDispatchId(t.id)}><Play size={14}/> Dispatch</button>
                    )}
                    {t.status === 'DISPATCHED' && (
                      <button className="btn btn-sm btn-success" onClick={() => { setCompleteId(t.id); setCompleteForm({ finalOdometer: '', fuelLiters: '', fuelCost: '', revenue: t.revenue || '' }); }}><CheckCircle size={14}/> Complete</button>
                    )}
                    {(t.status === 'DRAFT' || t.status === 'DISPATCHED') && (
                      <button className="btn btn-sm btn-danger" onClick={() => setCancelId(t.id)}><XCircle size={14}/></button>
                    )}
                  </div>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Create Modal */}
      <Modal isOpen={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Draft New Trip">
        <form onSubmit={handleCreate}>
          <div className="form-row">
            <div className="form-group"><label className="form-label">Source</label><input type="text" className="form-control" required value={createForm.source} onChange={e => setCreateForm({...createForm, source: e.target.value})} /></div>
            <div className="form-group"><label className="form-label">Destination</label><input type="text" className="form-control" required value={createForm.destination} onChange={e => setCreateForm({...createForm, destination: e.target.value})} /></div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Assign Vehicle</label>
              <select className="form-control" required value={createForm.vehicleId} onChange={e => setCreateForm({...createForm, vehicleId: e.target.value})}>
                <option value="">Select Available Vehicle...</option>
                {availableVehicles.map(v => <option key={v.id} value={v.id}>{v.registrationNumber} ({v.type}, max {v.maximumLoadCapacity}kg)</option>)}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Assign Driver</label>
              <select className="form-control" required value={createForm.driverId} onChange={e => setCreateForm({...createForm, driverId: e.target.value})}>
                <option value="">Select Available Driver...</option>
                {availableDrivers.map(d => <option key={d.id} value={d.id}>{d.name} (Exp: {formatDate(d.licenseExpiryDate)})</option>)}
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Cargo Weight (kg)</label>
              <input type="number" min="0.1" step="0.1" className="form-control" required value={createForm.cargoWeight} onChange={e => setCreateForm({...createForm, cargoWeight: e.target.value})} />
              {cargoExceedsCapacity && <small className="text-danger">Warning: Cargo exceeds vehicle capacity!</small>}
            </div>
            <div className="form-group">
              <label className="form-label">Planned Distance (km)</label>
              <input type="number" min="0.1" step="0.1" className="form-control" required value={createForm.plannedDistance} onChange={e => setCreateForm({...createForm, plannedDistance: e.target.value})} />
            </div>
          </div>
          <div className="form-group">
            <label className="form-label">Expected Revenue (Optional)</label>
            <input type="number" min="0" step="0.01" className="form-control" value={createForm.revenue} onChange={e => setCreateForm({...createForm, revenue: e.target.value})} />
          </div>
          <div className="modal-footer" style={{margin: '-1.5rem', marginTop: '1.5rem'}}>
            <button type="button" className="btn btn-secondary" onClick={() => setIsCreateOpen(false)}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading || cargoExceedsCapacity}>{loading ? 'Creating...' : 'Create Draft'}</button>
          </div>
        </form>
      </Modal>

      {/* Complete Modal */}
      <Modal isOpen={!!completeId} onClose={() => setCompleteId(null)} title="Complete Trip & Log Fuel">
        <form onSubmit={handleComplete}>
          <div className="form-group">
            <label className="form-label">Final Odometer</label>
            <input type="number" min="0" step="0.1" className="form-control" required value={completeForm.finalOdometer} onChange={e => setCompleteForm({...completeForm, finalOdometer: e.target.value})} />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Fuel Added (Liters)</label>
              <input type="number" min="0" step="0.1" className="form-control" value={completeForm.fuelLiters} onChange={e => setCompleteForm({...completeForm, fuelLiters: e.target.value})} />
            </div>
            <div className="form-group">
              <label className="form-label">Fuel Cost</label>
              <input type="number" min="0" step="0.01" className="form-control" value={completeForm.fuelCost} onChange={e => setCompleteForm({...completeForm, fuelCost: e.target.value})} />
            </div>
          </div>
          <div className="form-group">
            <label className="form-label">Actual Revenue</label>
            <input type="number" min="0" step="0.01" className="form-control" value={completeForm.revenue} onChange={e => setCompleteForm({...completeForm, revenue: e.target.value})} />
          </div>
          <div className="modal-footer" style={{margin: '-1.5rem', marginTop: '1.5rem'}}>
            <button type="button" className="btn btn-secondary" onClick={() => setCompleteId(null)}>Cancel</button>
            <button type="submit" className="btn btn-success" disabled={loading}>{loading ? 'Processing...' : 'Complete Trip'}</button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog isOpen={!!dispatchId} title="Dispatch Trip" message="This will assign the vehicle and driver to ON_TRIP. They will be unavailable for other dispatches." onConfirm={handleDispatch} onClose={() => setDispatchId(null)} confirmText="Dispatch Now" variant="primary" loading={loading} />
      <ConfirmDialog isOpen={!!cancelId} title="Cancel Trip" message="Are you sure? Any dispatched resources will be restored to AVAILABLE." onConfirm={handleCancel} onClose={() => setCancelId(null)} loading={loading} />
    </div>
  );
};

export default TripsPage;
