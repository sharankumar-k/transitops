import React, { useState, useEffect } from 'react';
import { vehicleApi } from '../api/vehicleApi';
import StatusBadge from '../components/common/StatusBadge';
import { formatNumber, formatCurrency } from '../utils/formatters';
import { Search, Plus, Trash2, Edit } from 'lucide-react';
import Modal from '../components/common/Modal';
import ConfirmDialog from '../components/common/ConfirmDialog';
import Alert from '../components/common/Alert';

const VehiclesPage = () => {
    const [vehicles, setVehicles] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    const [filters, setFilters] = useState({ registrationNumber: '', type: '', status: '', region: '' });

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [formData, setFormData] = useState({ id: null, registrationNumber: '', name: '', model: '', type: 'VAN', maximumLoadCapacity: '', odometer: '', acquisitionCost: '', region: '' });

    const [deleteId, setDeleteId] = useState(null);

    const loadVehicles = async () => {
        try {
            setLoading(true);
            const cleanFilters = Object.fromEntries(Object.entries(filters).filter(([_, v]) => v !== ''));
            const data = await vehicleApi.search(cleanFilters);
            setVehicles(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { loadVehicles(); }, []);

    const handleFilterChange = (e) => setFilters({ ...filters, [e.target.name]: e.target.value });
    const handleSearch = (e) => { e.preventDefault(); loadVehicles(); };

    const handleSave = async (e) => {
        e.preventDefault();
        try {
            setLoading(true);
            setError(null);
            if (formData.id) await vehicleApi.update(formData.id, formData);
            else await vehicleApi.create(formData);
            setSuccess('Vehicle saved successfully');
            setIsModalOpen(false);
            loadVehicles();
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async () => {
        try {
            setLoading(true);
            await vehicleApi.delete(deleteId);
            setSuccess('Vehicle removed or retired successfully');
            setDeleteId(null);
            loadVehicles();
        } catch (err) {
            setError(err.message);
            setDeleteId(null);
        } finally {
            setLoading(false);
        }
    };

    const openEdit = (vehicle) => {
        setFormData(vehicle);
        setIsModalOpen(true);
    };

    return (
        <div>
            <div className="page-header">
                <div>
                    <h1 className="page-title">Fleet Registry</h1>
                    <p className="page-subtitle">Manage vehicle records and capacity.</p>
                </div>
                <button className="btn btn-primary" onClick={() => { setFormData({ id: null, registrationNumber: '', name: '', model: '', type: 'VAN', maximumLoadCapacity: '', odometer: '', acquisitionCost: '', region: '' }); setIsModalOpen(true); }}>
                    <Plus size={16} /> Add Vehicle
                </button>
            </div>

            <Alert type="error" message={error} />
            <Alert type="success" message={success} />

            <form className="filters-bar" onSubmit={handleSearch}>
                <div className="form-group" style={{ marginBottom: 0 }}>
                    <label className="form-label">Registration</label>
                    <input type="text" className="form-control" name="registrationNumber" value={filters.registrationNumber} onChange={handleFilterChange} placeholder="Search Reg..." />
                </div>
                <div className="form-group" style={{ marginBottom: 0 }}>
                    <label className="form-label">Type</label>
                    <select className="form-control" name="type" value={filters.type} onChange={handleFilterChange}>
                        <option value="">All</option><option value="VAN">Van</option><option value="TRUCK">Truck</option><option value="BUS">Bus</option>
                    </select>
                </div>
                <div className="form-group" style={{ marginBottom: 0 }}>
                    <label className="form-label">Status</label>
                    <select className="form-control" name="status" value={filters.status} onChange={handleFilterChange}>
                        <option value="">All</option><option value="AVAILABLE">Available</option><option value="ON_TRIP">On Trip</option><option value="IN_SHOP">In Shop</option><option value="RETIRED">Retired</option>
                    </select>
                </div>
                <button type="submit" className="btn btn-secondary"><Search size={16} /> Filter</button>
            </form>

            <div className="card table-responsive" style={{ padding: 0 }}>
                <table className="table">
                    <thead>
                        <tr>
                            <th>Registration</th><th>Name</th><th>Type</th><th>Capacity (kg)</th><th>Odometer</th><th>Status</th><th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {vehicles.map(v => (
                            <tr key={v.id}>
                                <td style={{ fontWeight: 600 }}>{v.registrationNumber}</td>
                                <td>{v.name}</td>
                                <td>{v.type}</td>
                                <td>{formatNumber(v.maximumLoadCapacity)}</td>
                                <td>{formatNumber(v.odometer)} km</td>
                                <td><StatusBadge status={v.status} /></td>
                                <td>
                                    <div className="table-actions">
                                        <button className="btn btn-sm btn-secondary" onClick={() => openEdit(v)}><Edit size={14} /></button>
                                        <button className="btn btn-sm btn-danger" onClick={() => setDeleteId(v.id)}><Trash2 size={14} /></button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        {vehicles.length === 0 && !loading && <tr><td colSpan="7" style={{ textAlign: 'center', padding: '2rem' }}>No vehicles found.</td></tr>}
                    </tbody>
                </table>
            </div>

            <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={formData.id ? 'Edit Vehicle' : 'Add Vehicle'}>
                <form onSubmit={handleSave}>
                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">Registration Number</label>
                            <input type="text" className="form-control" required value={formData.registrationNumber} onChange={e => setFormData({ ...formData, registrationNumber: e.target.value })} />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Name</label>
                            <input type="text" className="form-control" required value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} />
                        </div>
                    </div>
                    <div className="form-group">
                        <label className="form-label">Model</label>
                        <input
                            type="text"
                            className="form-control"
                            required
                            value={formData.model}
                            onChange={e => setFormData({ ...formData, model: e.target.value })} />
                    </div>
                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">Type</label>
                            <select className="form-control" value={formData.type} onChange={e => setFormData({ ...formData, type: e.target.value })}>
                                <option value="VAN">Van</option><option value="TRUCK">Truck</option><option value="BUS">Bus</option><option value="CAR">Car</option><option value="BIKE">Bike</option>
                            </select>
                        </div>
                        <div className="form-group">
                            <label className="form-label">Region</label>
                            <input type="text" className="form-control" value={formData.region} onChange={e => setFormData({ ...formData, region: e.target.value })} />
                        </div>
                    </div>
                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">Max Load Capacity (kg)</label>
                            <input type="number" min="1" step="0.01" className="form-control" required value={formData.maximumLoadCapacity} onChange={e => setFormData({ ...formData, maximumLoadCapacity: e.target.value })} />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Odometer</label>
                            <input type="number" min="0" step="0.01" className="form-control" required value={formData.odometer} onChange={e => setFormData({ ...formData, odometer: e.target.value })} />
                        </div>
                    </div>
                    <div className="form-group">
                        <label className="form-label">Acquisition Cost</label>
                        <input type="number" min="0" step="0.01" className="form-control" required value={formData.acquisitionCost} onChange={e => setFormData({ ...formData, acquisitionCost: e.target.value })} />
                    </div>
                    <div className="modal-footer" style={{ margin: '-1.5rem', marginTop: '1.5rem' }}>
                        <button type="button" className="btn btn-secondary" onClick={() => setIsModalOpen(false)}>Cancel</button>
                        <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'Saving...' : 'Save Vehicle'}</button>
                    </div>
                </form>
            </Modal>

            <ConfirmDialog
                isOpen={!!deleteId}
                title="Remove Vehicle"
                message="Are you sure? If the vehicle has operational history, it will be logically retired to preserve analytics."
                onClose={() => setDeleteId(null)}
                onConfirm={handleDelete}
                loading={loading}
            />
        </div>
    );
};

export default VehiclesPage;