import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

function DashboardPage() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    navigate('/login');
  };

  return (
    <div style={{ textAlign: 'center', marginTop: '50px' }}>
      <h2>Welcome to your Dashboard!</h2>
      <p>This is a placeholder for your application's main content after login.</p>
      <p>From here, you would access protected features.</p>
      {/* Exemplo de botão de logout. Você usaria Link ou useNavigate para redirecionar */}
      <button onClick={handleLogout} style={{padding: '10px 20px', marginTop: '20px', cursor: 'pointer'}}>
        Logout (Placeholder)
      </button>
      <p style={{marginTop: '30px'}}>
        <Link to="/login">Go back to Login</Link>
      </p>
    </div>
  );
}

export default DashboardPage;