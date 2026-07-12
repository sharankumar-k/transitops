import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import { useAuth } from '../../context/AuthContext';

const AppLayout = () => {
  const { auth, logout } = useAuth();

  const roleLabel = auth?.role
    ?.split('_')
    .map(word => word.charAt(0) + word.slice(1).toLowerCase())
    .join(' ');

  return (
    <div className="app-layout">
      <Sidebar />

      <div className="main-content">
        <header className="topbar">
          <div style={{ fontWeight: 600 }}>Operations Workspace</div>

          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.75rem'
            }}
          >
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontSize: '0.8rem' }}>{auth?.email}</div>
              <div className="badge badge-primary">{roleLabel}</div>
            </div>

            <button
              type="button"
              className="btn btn-sm btn-secondary"
              onClick={logout}
            >
              Logout
            </button>
          </div>
        </header>

        <main className="page-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default AppLayout;