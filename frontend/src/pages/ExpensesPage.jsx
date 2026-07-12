import React, { useState, useEffect } from 'react';
import { expenseApi } from '../api/expenseApi';
import { vehicleApi } from '../api/vehicleApi';
import { formatDate, formatCurrency } from '../utils/formatters';
import { Plus } from 'lucide-react';
import Modal from '../components/common/Modal';
import Alert from '../components/common/Alert';

const ExpensesPage = () => {
  const [expenses, setExpenses] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [form, setForm] = useState({ vehicleId: '', tripId: '', expenseType: 'TOLL', description: '', amount: '', expenseDate: new Date().toISOString().split('T')[0] });

  const loadData = async () => {
    try { setLoading(true); setExpenses(await expenseApi.getAll()); }
    catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  useEffect(() => { loadData(); vehicleApi.getAll().then(setVehicles).catch(console.error); }, []);

  const handleAdd = async (e) => {
    e.preventDefault();
    try {
      setLoading(true); setError(null);
      await expenseApi.create({ ...form, tripId: form.tripId || null });
      setIsAddOpen(false);
      loadData();
    } catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Operational Expenses</h1>
          <p className="page-subtitle">Track generic tolls, parking, and other fleet costs.</p>
        </div>
        <button className="btn btn-primary" onClick={() => { setForm({ vehicleId: '', tripId: '', expenseType: 'TOLL', description: '', amount: '', expenseDate: new Date().toISOString().split('T')[0] }); setIsAddOpen(true); }}><Plus size={16} /> Log Expense</button>
      </div>

      <Alert type="error" message={error} />

      <div className="card table-responsive" style={{ padding: 0 }}>
        <table className="table">
          <thead>
            <tr><th>Date</th><th>Type</th><th>Vehicle</th><th>Trip ID</th><th>Description</th><th>Amount</th></tr>
          </thead>
          <tbody>
            {expenses.map(e => (
              <tr key={e.id}>
                <td>{formatDate(e.expenseDate)}</td>
                <td><span className="badge badge-neutral">{e.expenseType}</span></td>
                <td style={{fontWeight: 600}}>{e.vehicleRegistration}</td>
                <td>{e.tripId ? `#${e.tripId}` : '-'}</td>
                <td>{e.description}</td>
                <td style={{color: 'var(--color-danger)', fontWeight: 600}}>{formatCurrency(e.amount)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal isOpen={isAddOpen} onClose={() => setIsAddOpen(false)} title="Log Expense">
        <form onSubmit={handleAdd}>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Vehicle</label>
              <select className="form-control" required value={form.vehicleId} onChange={e => setForm({...form, vehicleId: e.target.value})}>
                <option value="">Select Vehicle...</option>
                {vehicles.map(v => <option key={v.id} value={v.id}>{v.registrationNumber}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Trip ID (Optional)</label>
              <input type="number" className="form-control" value={form.tripId} onChange={e => setForm({...form, tripId: e.target.value})} />
            </div>
          </div>
          <div className="form-group">
            <label className="form-label">Expense Type</label>
            <select className="form-control" required value={form.expenseType} onChange={e => setForm({...form, expenseType: e.target.value})}>
              <option value="TOLL">Toll</option><option value="PARKING">Parking</option><option value="REPAIR">Repair</option><option value="INSURANCE">Insurance</option><option value="OTHER">Other</option>
            </select>
          </div>
          <div className="form-group">
            <label className="form-label">Description</label>
            <input type="text" className="form-control" required value={form.description} onChange={e => setForm({...form, description: e.target.value})} />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Amount</label>
              <input type="number" min="0" step="0.01" className="form-control" required value={form.amount} onChange={e => setForm({...form, amount: e.target.value})} />
            </div>
            <div className="form-group">
              <label className="form-label">Date</label>
              <input type="date" className="form-control" required value={form.expenseDate} onChange={e => setForm({...form, expenseDate: e.target.value})} />
            </div>
          </div>
          <div className="modal-footer" style={{margin: '-1.5rem', marginTop: '1.5rem'}}>
            <button type="button" className="btn btn-secondary" onClick={() => setIsAddOpen(false)}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>Save</button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default ExpensesPage;