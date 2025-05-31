import React from "react";
import { Link, Outlet } from "react-router-dom";
import "./Layout.css";
import { IoWalletOutline } from "react-icons/io5";
import { SlGraph, SlWallet } from "react-icons/sl";
import { MdAttachMoney } from "react-icons/md";
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
            <Link to="/dashboard" className="nav-logo">
              InvestmentsWallet
            </Link>
          </div>
          <div className="nav-links-wrapper">
            <ul className="nav-links">
              <li>
                <Link to="/dashboard" className={isActive("dashboard")}>
                  <SlGraph className="icon" />
                  Dashboard
                </Link>
              </li>
              <li>
                <Link to="/assets" className={isActive("assets")}>
                  <MdAttachMoney className="icon" size={24} />
                  Ativos
                </Link>
              </li>
              <li>
                <Link to="/wallet" className={isActive("wallet")}>
                  <SlWallet className="icon" />
                  Carteira
                </Link>
              </li>
            </ul>
          </div>
          <div className="menu-composition-container">
            <MenuListComposition />
          </div>
        </div>
      </nav>
      <main className="content-area">
        <Outlet />
      </main>
      <footer className="footer">
        <b>InvestmentsWallet</b>
        <p>
          &copy; {new Date().getFullYear()} InvestmentsWallet. All rights
          reserved.
        </p>
      </footer>
    </>
  );
}

export default Layout;
