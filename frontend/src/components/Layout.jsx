import React from 'react';
import { Link, Outlet } from 'react-router-dom';
import './Layout.css';

function Layout() {
  return (
    <>
      <nav className="navbar">
        <div className="nav-container">
          <Link to="/" className="nav-logo">InvestmentsWallet</Link>
          <ul className="nav-links">
            <li><Link to="/login">Login</Link></li>
            <li><Link to="/register">Register</Link></li>
          </ul>
        </div>
      </nav>
      <main className="content-area">
        <Outlet />
      </main>
      <footer className="footer">
        <p>&copy; {new Date().getFullYear()} MyApp. All rights reserved.</p>
      </footer>
    </>
  );
}

export default Layout;