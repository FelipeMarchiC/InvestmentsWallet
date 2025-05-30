import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { getItemWithExpiry } from "../utils/storageWithExpiry";

const isAuthenticated = () => {
  const token = getItemWithExpiry("authToken");
  return token !== null && token !== undefined && token !== "";
};

function ProtectedRoute({ children }) {
  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  return children ? children : <Outlet />;
}

export default ProtectedRoute;
