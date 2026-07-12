import React from 'react';
import { Link } from 'react-router-dom';

const NotFoundPage = () => {
  return (
    <div style={{ textAlign: 'center', padding: '4rem 2rem' }}>
      <h1 style={{ fontSize: '4rem', color: 'var(--color-primary)' }}>404</h1>
      <p style={{ fontSize: '1.25rem', color: 'var(--color-text-muted)', marginBottom: '2rem' }}>The page you are looking for does not exist.</p>
      <Link to="/" className="btn btn-primary">Return to Dashboard</Link>
    </div>
  );
};

export default NotFoundPage;