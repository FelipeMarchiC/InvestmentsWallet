import React from "react";
import { Link, Outlet } from "react-router-dom";
import "./Layout.css";
import "../Variables.css";
import { IoWalletOutline } from "react-icons/io5";
import { SlGraph, SlWallet } from "react-icons/sl";
import MenuListComposition from "./MenuListComposition";

import { useLocation } from "react-router-dom";

function Layout() {
  const location = useLocation();
  const path = location.pathname.split("/")[1];

  const isActive = (route) => (path === route ? "active" : "");

  return (
    <>
      <nav className="navbar">
        <div className="nav-container">
          <div id="left-side">
            <IoWalletOutline size={30} />
            <Link to="/" className="nav-logo">
              InvestmentsWallet
            </Link>
          </div>
          <div>
            <ul className="nav-links">
              <li>
                <Link to="/dashboard" className={isActive("dashboard")}>
                  <SlGraph />
                  Dashboard
                </Link>
              </li>
              <li>
                <Link to="/wallet" className={isActive("wallet")}>
                  <SlWallet />
                  Wallet
                </Link>
              </li>
              <li>
                <Link to="/assets" className={isActive("assets")}>
                  Assets
                </Link>
              </li>
              <li>
                <Link to="/login" className={isActive("login")}>
                  Login
                </Link>
              </li>
              <li>
                <Link to="/register" className={isActive("register")}>
                  Register
                </Link>
              </li>
            </ul>
          </div>
          <div>
            <MenuListComposition />
          </div>
        </div>
      </nav>
      <main className="content-area">
        <Outlet />
      </main>
      <footer className="footer">
        <p>
          &copy; {new Date().getFullYear()} InvestmentsWallet. All rights
          reserved.
        </p>
      </footer>
    </>
  );
}

export default Layout;
