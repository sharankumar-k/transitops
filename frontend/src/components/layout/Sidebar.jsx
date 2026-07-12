import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Truck,
  Users,
  Map,
  Wrench,
  Receipt,
  BarChart2
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

const Sidebar = () => {
  const { auth } = useAuth();
  const role = auth?.role;

  const menu = [
    {
      name: 'Dashboard',
      path: '/',
      icon: <LayoutDashboard size={20} />,
      roles: ['FLEET_MANAGER', 'DRIVER', 'SAFETY_OFFICER', 'FINANCIAL_ANALYST']
    },
    {
      name: 'Vehicles',
      path: '/vehicles',
      icon: <Truck size={20} />,
      roles: ['FLEET_MANAGER', 'DRIVER', 'SAFETY_OFFICER', 'FINANCIAL_ANALYST']
    },
    {
      name: 'Drivers',
      path: '/drivers',
      icon: <Users size={20} />,
      roles: ['FLEET_MANAGER', 'DRIVER', 'SAFETY_OFFICER']
    },
    {
      name: 'Trips',
      path: '/trips',
      icon: <Map size={20} />,
      roles: ['FLEET_MANAGER', 'DRIVER', 'FINANCIAL_ANALYST']
    },
    {
      name: 'Maintenance',
      path: '/maintenance',
      icon: <Wrench size={20} />,
      roles: ['FLEET_MANAGER', 'FINANCIAL_ANALYST']
    },
    {
      name: 'Expenses',
      path: '/expenses',
      icon: <Receipt size={20} />,
      roles: ['FLEET_MANAGER', 'FINANCIAL_ANALYST']
    },
    {
      name: 'Reports',
      path: '/reports',
      icon: <BarChart2 size={20} />,
      roles: ['FLEET_MANAGER', 'FINANCIAL_ANALYST']
    }
  ];

  const visibleMenu = menu.filter(item => item.roles.includes(role));

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <Map size={24} color="var(--color-primary)" />
        TransitOps
      </div>

      <nav className="sidebar-nav">
        {visibleMenu.map(item => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `nav-item ${isActive ? 'active' : ''}`
            }
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