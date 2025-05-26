import React, { useState } from 'react';
import { authService } from '../services/authService';
import './RegisterPage.css'; 
import { useNavigate } from 'react-router-dom';

function RegisterPage() {
  const [name, setName] = useState('');
  const [lastname, setLastname] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSuccessMessage('');

    if (!name || !lastname || !email || !password) {
      setError('Please fill in all fields.');
      return;
    }

    // Validação de senha (exemplo simples)
    if (password.length < 6) {
      setError('Password must be at least 6 characters long.');
      return;
    }

    setLoading(true);

    const userData = {
      name,
      lastname,
      email,
      password,
    };

    try {
      const responseData = await authService.register(userData);
      setLoading(false);
        setSuccessMessage(responseData.message || 'Registration successful! Redirecting to login...');
        setName('');
        setLastname('');
        setEmail('');
        setPassword('');
        
        navigate('/login');
    } catch (err) {
      setLoading(false);
      setError(err.message || 'An unexpected error occurred during registration.');
      console.error('Registration attempt failed:', err);
    }
  };

  return (
    <div className="register-container"> {/* Pode usar 'login-container' se compartilhar CSS */}
      <form onSubmit={handleSubmit} className="register-form"> {/* Pode usar 'login-form' */}
        <h2>Register</h2>
        {error && <p className="error-message">{error}</p>}
        {successMessage && <p className="success-message">{successMessage}</p>}

        <div className="form-group">
          <label htmlFor="name">Name:</label>
          <input
            type="text"
            id="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Enter your first name"
            disabled={loading}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="lastname">Last Name:</label>
          <input
            type="text"
            id="lastname"
            value={lastname}
            onChange={(e) => setLastname(e.target.value)}
            placeholder="Enter your last name"
            disabled={loading}
            required
          />
        </div>

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
            placeholder="Enter your password (min. 6 characters)"
            disabled={loading}
            required
          />
        </div>

        <button type="submit" className="register-button" disabled={loading}>
          {loading ? 'Registering...' : 'Register'}
        </button>
      </form>
    </div>
  );
}

export default RegisterPage;