import React, { useState } from 'react';
import { authService } from '../../services/authService';
import './LoginPage.css';
import { useNavigate } from 'react-router-dom';
import { IoWalletOutline } from "react-icons/io5";

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');

    if (!email || !password) {
      setError('Please fill in all fields.');
      return;
    }

    setLoading(true);

    try {
      const responseData = await authService.login(email, password);
      setLoading(false);

      if (responseData.token) {
        localStorage.setItem('authToken', responseData.token);
        navigate('/dashboard');
      }

    } catch (err) {
      setLoading(false);
      setError(err.message || 'An unexpected error occurred.');
      console.error('Login attempt failed:', err);
    }
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit} className="login-form">
        
        <div className='login-logo'>
          <IoWalletOutline size={85} color='#1e3a8a'/>
          <h2 className="logo-text">InvestmentsWallet</h2>
        </div>

        {error && <p className="error-message">{error}</p>}
        <div className="form-group">
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email"
            disabled={loading}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your password"
            disabled={loading}
            required
          />
        </div>
        <button type="submit" className="login-button" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
        <div className="register-link">
          <p>Don't have an account? <a href="/register">Register here</a></p>
        </div>
      </form>
    </div>
  );
}

export default LoginPage;