import React, { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import Alert from '../components/common/Alert';

const LoginPage = () => {
  const { auth, login } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState('fleet@transitops.com');
  const [password, setPassword] = useState('Fleet@123');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  if (auth?.token) {
    return <Navigate to="/" replace />;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);
      setError(null);

      const response = await api.post('/auth/login', {
        email,
        password
      });

      login(response.data);
      navigate('/');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '1.5rem'
      }}
    >
      <div className="card" style={{ width: '100%', maxWidth: '420px' }}>
        <h1 className="page-title">TransitOps</h1>
        <p className="page-subtitle">
          Sign in to the transport operations workspace.
        </p>

        <Alert type="error" message={error} />

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email</label>
            <input
              type="email"
              className="form-control"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-control"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', justifyContent: 'center' }}
            disabled={loading}
          >
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;