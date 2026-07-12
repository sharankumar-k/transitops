import React, { useState, useEffect } from 'react';
import { reportApi } from '../api/reportApi';
import { vehicleApi } from '../api/vehicleApi';
import { formatCurrency, formatNumber } from '../utils/formatters';
import { Download, TrendingUp } from 'lucide-react';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts';
import Alert from '../components/common/Alert';

const ReportsPage = () => {
  const [vehicles, setVehicles] = useState([]);
  const [selectedVehicle, setSelectedVehicle] = useState('');
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => { vehicleApi.getAll().then(setVehicles).catch(console.error); }, []);

  useEffect(() => {
    if (!selectedVehicle) { setAnalytics(null); return; }
    const loadReport = async () => {
      try { setLoading(true); setError(null); setAnalytics(await reportApi.getVehicleAnalytics(selectedVehicle)); }
      catch (err) { setError(err.message); }
      finally { setLoading(false); }
    };
    loadReport();
  }, [selectedVehicle]);

  const handleDownload = async () => {
    try { await reportApi.downloadCsv(); }
    catch (err) { setError('Failed to download CSV'); }
  };

  const costData = analytics ? [
    { name: 'Fuel', value: analytics.totalFuelCost, color: '#3b82f6' },
    { name: 'Maintenance', value: analytics.totalMaintenanceCost, color: '#f59e0b' },
    { name: 'Other Expenses', value: analytics.totalOtherExpenses, color: '#ef4444' }
  ].filter(d => d.value > 0) : [];

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Fleet Analytics & ROI</h1>
          <p className="page-subtitle">Track operational costs and profitability.</p>
        </div>
        <button className="btn btn-primary" onClick={handleDownload}><Download size={16} /> Export Fleet CSV</button>
      </div>

      <div className="card mb-6">
        <label className="form-label">Analyze Specific Vehicle</label>
        <select className="form-control" value={selectedVehicle} onChange={e => setSelectedVehicle(e.target.value)} style={{ maxWidth: '400px' }}>
          <option value="">Select a vehicle...</option>
          {vehicles.map(v => <option key={v.id} value={v.id}>{v.registrationNumber} - {v.name}</option>)}
        </select>
      </div>

      <Alert type="error" message={error} />
      {loading && <div>Loading analytics...</div>}

      {analytics && (
        <>
          <div className="grid-cols-4">
            <div className="stat-card">
              <div className="stat-content"><h3>Revenue</h3><p className="text-success" style={{color: 'var(--color-success)'}}>{formatCurrency(analytics.totalRevenue)}</p></div>
            </div>
            <div className="stat-card">
              <div className="stat-content"><h3>Total Ops Cost</h3><p className="text-danger" style={{color: 'var(--color-danger)'}}>{formatCurrency(analytics.totalOperationalCost)}</p></div>
            </div>
            <div className="stat-card">
              <div className="stat-content"><h3>Vehicle ROI</h3><p>{analytics.vehicleROI != null ? `${analytics.vehicleROI}%` : 'N/A'}</p></div>
            </div>
            <div className="stat-card">
              <div className="stat-content"><h3>Fuel Efficiency</h3><p>{formatNumber(analytics.fuelEfficiency)} km/L</p></div>
            </div>
          </div>

          <div className="grid-cols-2">
            <div className="card" style={{ height: '350px' }}>
              <h3 className="mb-4 text-muted">Cost Breakdown</h3>
              {costData.length > 0 ? (
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie data={costData} cx="50%" cy="50%" innerRadius={60} outerRadius={100} paddingAngle={5} dataKey="value" label>
                      {costData.map((entry, index) => <Cell key={`cell-${index}`} fill={entry.color} />)}
                    </Pie>
                    <Tooltip formatter={(val) => formatCurrency(val)} />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <div style={{display: 'flex', height: '100%', alignItems: 'center', justifyContent: 'center', color: 'var(--color-text-muted)'}}>No cost data available</div>
              )}
            </div>
            <div className="card">
              <h3 className="mb-4 text-muted">Performance Summary</h3>
              <ul style={{ listStyle: 'none', lineHeight: '2.5' }}>
                <li className="flex-between"><span>Completed Distance:</span> <strong>{formatNumber(analytics.totalCompletedDistance)} km</strong></li>
                <li className="flex-between"><span>Total Fuel Consumed:</span> <strong>{formatNumber(analytics.totalFuelLiters)} L</strong></li>
                <li className="flex-between"><span>Fuel Cost:</span> <strong>{formatCurrency(analytics.totalFuelCost)}</strong></li>
                <li className="flex-between"><span>Maintenance Cost:</span> <strong>{formatCurrency(analytics.totalMaintenanceCost)}</strong></li>
                <li className="flex-between"><span>Other Tracked Expenses:</span> <strong>{formatCurrency(analytics.totalOtherExpenses)}</strong></li>
              </ul>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default ReportsPage;