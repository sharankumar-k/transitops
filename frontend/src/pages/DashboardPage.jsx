import React, { useState, useEffect } from 'react';
import { dashboardApi } from '../api/dashboardApi';
import { Truck, Map, Wrench, Users, Activity, Play } from 'lucide-react';
import { Link } from 'react-router-dom';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import Alert from '../components/common/Alert';

const DashboardPage = () => {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [filters, setFilters] = useState({
        vehicleType: '',
        vehicleStatus: '',
        region: ''
    });

    const loadStats = async () => {
        try {
            setLoading(true);
            setError(null);
            const cleanFilters = Object.fromEntries(Object.entries(filters).filter(([_, v]) => v !== ''));
            const data = await dashboardApi.getStats(cleanFilters);
            setStats(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadStats();
    }, [filters]);

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prev => ({ ...prev, [name]: value }));
    };

    if (loading && !stats) return <div>Loading dashboard...</div>;
    if (error && !stats) return <Alert type="error" message={error} />;

    const pieData = stats ? [
        { name: 'Available', value: stats.availableVehicles, color: '#10b981' },
        { name: 'In Maintenance', value: stats.vehiclesInMaintenance, color: '#f59e0b' },
        { name: 'On Trip', value: stats.activeVehicles - stats.availableVehicles - stats.vehiclesInMaintenance, color: '#4f46e5' }
    ].filter(d => d.value > 0) : [];

    const barData = stats ? [
        { name: 'Active Trips', count: stats.activeTrips },
        { name: 'Pending Trips', count: stats.pendingTrips },
        { name: 'Drivers On Duty', count: stats.driversOnDuty }
    ] : [];

    return (
        <div>
            <div className="page-header">
                <div>
                    <h1 className="page-title">Operations Dashboard</h1>
                    <p className="page-subtitle">Real-time overview of fleet operations.</p>
                </div>
            </div>

            <div className="filters-bar">
                <div className="form-group" style={{ marginBottom: 0, minWidth: '150px' }}>
                    <label className="form-label">Vehicle Type</label>
                    <select className="form-control" name="vehicleType" value={filters.vehicleType} onChange={handleFilterChange}>
                        <option value="">All Types</option>
                        <option value="VAN">Van</option>
                        <option value="TRUCK">Truck</option>
                        <option value="BUS">Bus</option>
                    </select>
                </div>
                <div className="form-group" style={{ marginBottom: 0, minWidth: '150px' }}>
                    <label className="form-label">Vehicle Status</label>
                    <select
                        className="form-control"
                        name="vehicleStatus"
                        value={filters.vehicleStatus}
                        onChange={handleFilterChange}
                    >
                        <option value="">All Statuses</option>
                        <option value="AVAILABLE">Available</option>
                        <option value="ON_TRIP">On Trip</option>
                        <option value="IN_SHOP">In Shop</option>
                        <option value="RETIRED">Retired</option>
                    </select>
                </div>
                <div className="form-group" style={{ marginBottom: 0, minWidth: '150px' }}>
                    <label className="form-label">Region</label>
                    <input type="text" className="form-control" name="region" placeholder="Filter region..." value={filters.region} onChange={handleFilterChange} />
                </div>
                <div className="form-group" style={{ marginBottom: 0 }}>
                    <button className="btn btn-primary" onClick={loadStats}>Refresh</button>
                </div>
            </div>

            {error && <Alert type="error" message={error} />}

            {stats && (
                <>
                    <div className="grid-cols-4">
                        <div className="stat-card">
                            <div className="stat-icon"><Truck size={24} /></div>
                            <div className="stat-content"><h3>Active Vehicles</h3><p>{stats.activeVehicles}</p></div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon" style={{ color: '#10b981', background: '#d1fae5' }}><Truck size={24} /></div>
                            <div className="stat-content"><h3>Available Vehicles</h3><p>{stats.availableVehicles}</p></div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon" style={{ color: '#f59e0b', background: '#fef3c7' }}><Wrench size={24} /></div>
                            <div className="stat-content"><h3>In Maintenance</h3><p>{stats.vehiclesInMaintenance}</p></div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon" style={{ color: '#3b82f6', background: '#dbeafe' }}><Activity size={24} /></div>
                            <div className="stat-content"><h3>Fleet Utilization</h3><p>{stats.fleetUtilizationPercentage}%</p></div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon" style={{ color: '#4f46e5', background: '#e0e7ff' }}><Map size={24} /></div>
                            <div className="stat-content"><h3>Active Trips</h3><p>{stats.activeTrips}</p></div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon"><Users size={24} /></div>
                            <div className="stat-content"><h3>Drivers On Duty</h3><p>{stats.driversOnDuty}</p></div>
                        </div>
                    </div>

                    <div className="grid-cols-2">
                        <div className="card" style={{ height: '350px' }}>
                            <h3 className="mb-4 text-muted">Fleet Status Distribution</h3>
                            <ResponsiveContainer width="100%" height="100%">
                                <PieChart>
                                    <Pie data={pieData} cx="50%" cy="50%" innerRadius={60} outerRadius={100} paddingAngle={5} dataKey="value" label>
                                        {pieData.map((entry, index) => <Cell key={`cell-${index}`} fill={entry.color} />)}
                                    </Pie>
                                    <Tooltip />
                                </PieChart>
                            </ResponsiveContainer>
                        </div>
                        <div className="card" style={{ height: '350px' }}>
                            <h3 className="mb-4 text-muted">Active Operations</h3>
                            <ResponsiveContainer width="100%" height="100%">
                                <BarChart data={barData}>
                                    <XAxis dataKey="name" />
                                    <YAxis allowDecimals={false} />
                                    <Tooltip />
                                    <Bar dataKey="count" fill="var(--color-primary)" radius={[4, 4, 0, 0]} />
                                </BarChart>
                            </ResponsiveContainer>
                        </div>
                    </div>

                    <div className="card">
                        <h3 className="mb-4">Live Operations Quick Actions</h3>
                        <div className="flex-between" style={{ justifyContent: 'flex-start', gap: '1rem' }}>
                            <Link to="/trips" className="btn btn-primary"><Play size={16} /> Dispatch Trip</Link>
                            <Link to="/maintenance" className="btn btn-secondary"><Wrench size={16} /> Schedule Maintenance</Link>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
};

export default DashboardPage;