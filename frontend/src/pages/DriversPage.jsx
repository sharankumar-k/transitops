import React, { useState, useEffect } from 'react';
import { driverApi } from '../api/driverApi';
import StatusBadge from '../components/common/StatusBadge';
import { formatDate } from '../utils/formatters';
import { Search, Plus, Trash2, Edit, AlertTriangle } from 'lucide-react';
import Modal from '../components/common/Modal';
import ConfirmDialog from '../components/common/ConfirmDialog';
import Alert from '../components/common/Alert';

const DriversPage = () => {
    const [drivers, setDrivers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    const [filters, setFilters] = useState({ name: '', licenseNumber: '', status: '' });

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [formData, setFormData] = useState({ id: null, name: '', licenseNumber: '', licenseCategory: '', licenseExpiryDate: '', contactNumber: '', safetyScore: 100, region: '' });

    const [deleteId, setDeleteId] = useState(null);

    const loadDrivers = async () => {
        try {
            setLoading(true);
            const cleanFilters = Object.fromEntries(Object.entries(filters).filter(([_, v]) => v !== ''));
            const data = await driverApi.search(cleanFilters);
            setDrivers(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { loadDrivers(); }, []);

    const handleSearch = (e) => { e.preventDefault(); loadDrivers(); };

    const handleSave = async (e) => {
        e.preventDefault();
        try {
            setLoading(true); setError(null);
            if (formData.id) await driverApi.update(formData.id, formData);
            else await driverApi.create(formData);
            setSuccess('Driver saved successfully');
            setIsModalOpen(false);
            loadDrivers();
        } catch (err) { setError(err.message); }
        finally { setLoading(false); }
    };

    const handleDelete = async () => {
        try {
            setLoading(true);
            await driverApi.delete(deleteId);
            setSuccess('Driver removed or suspended');
            setDeleteId(null);
            loadDrivers();
        } catch (err) { setError(err.message); setDeleteId(null); }
        finally { setLoading(false); }
    };

    const isExpired = (dateString) => new Date(dateString) < new Date();

    return (
        <div>
            <div className="page-header">
                <div>
                    <h1 className="page-title">Driver Roster</h1>
                    <p className="page-subtitle">Manage driver details, compliance, and safety.</p>
                </div>
                <button className="btn btn-primary" onClick={() => { setFormData({ id: null, name: '', licenseNumber: '', licenseCategory: '', licenseExpiryDate: '', contactNumber: '', safetyScore: 100, region: '' }); setIsModalOpen(true); }}>
                    <Plus size={16} /> Add Driver
                </button>
            </div>

            <Alert type="error" message={error} />
            <Alert type="success" message={success} />

            <form className="filters-bar" onSubmit={handleSearch}>
                <div className="form-group" style={{ marginBottom: 0 }}>
                    <input type="text" className="form-control" placeholder="Search Name..." value={filters.name} onChange={e => setFilters({ ...filters, name: e.target.value })} />
                </div>
                <div className="form-group" style={{ marginBottom: 0 }}>
                    <input type="text" className="form-control" placeholder="License No..." value={filters.licenseNumber} onChange={e => setFilters({ ...filters, licenseNumber: e.target.value })} />
                </div>
                <div className="form-group" style={{ marginBottom: 0 }}>
                    <select className="form-control" value={filters.status} onChange={e => setFilters({ ...filters, status: e.target.value })}>
                        <option value="">All Statuses</option><option value="AVAILABLE">Available</option><option value="ON_TRIP">On Trip</option><option value="SUSPENDED">Suspended</option>
                    </select>
                </div>
                <button type="submit" className="btn btn-secondary"><Search size={16} /> Filter</button>
            </form>

            <div className="card table-responsive" style={{ padding: 0 }}>
                <table className="table">
                    <thead>
                        <tr>
                            <th>Name</th><th>License</th><th>Expiry</th><th>Safety Score</th><th>Status</th><th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {drivers.map(d => (
                            <tr key={d.id}>
                                <td style={{ fontWeight: 600 }}>{d.name}</td>
                                <td>{d.licenseNumber} <span className="text-muted">({d.licenseCategory})</span></td>
                                <td>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: isExpired(d.licenseExpiryDate) ? 'var(--color-danger)' : 'inherit' }}>
                                        {isExpired(d.licenseExpiryDate) && <AlertTriangle size={14} />}
                                        {formatDate(d.licenseExpiryDate)}
                                    </div>
                                </td>
                                <td>{d.safetyScore}/100</td>
                                <td><StatusBadge status={d.status} /></td>
                                <td>
                                    <div className="table-actions">
                                        <button className="btn btn-sm btn-secondary" onClick={() => { setFormData(d); setIsModalOpen(true); }}><Edit size={14} /></button>
                                        <button className="btn btn-sm btn-danger" onClick={() => setDeleteId(d.id)}><Trash2 size={14} /></button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={formData.id ? 'Edit Driver' : 'Add Driver'}>
                <form onSubmit={handleSave}>
                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">Name</label>
                            <input type="text" className="form-control" required value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Contact</label>
                            <input type="text" className="form-control" value={formData.contactNumber} onChange={e => setFormData({ ...formData, contactNumber: e.target.value })} />
                        </div>
                    </div>
                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">License Number</label>
                            <input type="text" className="form-control" required value={formData.licenseNumber} onChange={e => setFormData({ ...formData, licenseNumber: e.target.value })} />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Category</label>
                            <input type="text" className="form-control" required value={formData.licenseCategory} onChange={e => setFormData({ ...formData, licenseCategory: e.target.value })} />
                        </div>
                    </div>
                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">License Expiry</label>
                            <input type="date" className="form-control" required value={formData.licenseExpiryDate} onChange={e => setFormData({ ...formData, licenseExpiryDate: e.target.value })} />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Safety Score (0-100)</label>
                            <input type="number" min="0" max="100" className="form-control" required value={formData.safetyScore} onChange={e => setFormData({ ...formData, safetyScore: e.target.value })} />
                        </div>
                    </div>
                    <div className="form-group">
                        <label className="form-label">Region</label>
                        <input
                            type="text"
                            className="form-control"
                            required
                            value={formData.region}
                            onChange={e =>
                                setFormData({ ...formData, region: e.target.value })
                            }
                        />
                    </div>
                    <div className="modal-footer" style={{ margin: '-1.5rem', marginTop: '1.5rem' }}>
                        <button type="button" className="btn btn-secondary" onClick={() => setIsModalOpen(false)}>Cancel</button>
                        <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'Saving...' : 'Save Driver'}</button>
                    </div>
                </form>
            </Modal>

            <ConfirmDialog isOpen={!!deleteId} title="Remove Driver" message="Are you sure? If they have history, they will be suspended." onClose={() => setDeleteId(null)} onConfirm={handleDelete} loading={loading} />
        </div>
    );
};

export default DriversPage;