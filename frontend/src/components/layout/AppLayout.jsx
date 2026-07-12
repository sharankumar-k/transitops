import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';

const AppLayout = () => {
  return (
    <div className="app-layout">
      <Sidebar />
      <div className="main-content">
        <header className="topbar">
          <div style={{ fontWeight: 600 }}>Operations Workspace</div>
          <div className="badge badge-primary">Fleet Manager</div>
        </header>
        <main className="page-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default AppLayout;