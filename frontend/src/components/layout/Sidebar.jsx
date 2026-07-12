import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Truck, Users, Map, Wrench, Receipt, BarChart2 } from 'lucide-react';

const Sidebar = () => {
  const menu = [
    { name: 'Dashboard', path: '/', icon: <LayoutDashboard size={20} /> },
    { name: 'Vehicles', path: '/vehicles', icon: <Truck size={20} /> },
    { name: 'Drivers', path: '/drivers', icon: <Users size={20} /> },
    { name: 'Trips', path: '/trips', icon: <Map size={20} /> },
    { name: 'Maintenance', path: '/maintenance', icon: <Wrench size={20} /> },
    { name: 'Expenses', path: '/expenses', icon: <Receipt size={20} /> },
    { name: 'Reports', path: '/reports', icon: <BarChart2 size={20} /> },
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <Map size={24} color="var(--color-primary)" />
        TransitOps
      </div>
      <nav className="sidebar-nav">
        {menu.map((item) => (
          <NavLink 
            key={item.path} 
            to={item.path} 
            className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
          >
            {item.icon}
            <span>{item.name}</span>
          </NavLink>
        ))}
      </nav>
    </aside>
  );
};

export default Sidebar;