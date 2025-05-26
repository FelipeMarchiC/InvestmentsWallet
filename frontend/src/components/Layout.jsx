import React from 'react';
import { Link, Outlet } from 'react-router-dom';
import './Layout.css';
import './Variables.css'
import { IoWalletOutline } from "react-icons/io5";

function Layout() {
  return (
    <>
      <nav className="navbar">
        <div className="nav-container">
        <div id="left-side">
          <IoWalletOutline size={30}/>
          <Link to="/" className="nav-logo">InvestmentsWallet</Link>
        </div>
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
        <p>&copy; {new Date().getFullYear()} InvestmentsWallet. All rights reserved.</p>
      </footer>
    </>
  );
}

export default Layout;