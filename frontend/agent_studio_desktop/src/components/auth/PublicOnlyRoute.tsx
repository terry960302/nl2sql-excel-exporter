import React from "react";
import { Navigate } from "react-router-dom";
import { useAuthStore } from "@/stores/authStore";

interface PublicOnlyRouteProps {
  children: React.ReactNode;
}

export const PublicOnlyRoute: React.FC<PublicOnlyRouteProps> = ({
  children,
}) => {
  const { isAuthenticated, isInitialized } = useAuthStore();

  if (!isInitialized) return null;
  if (isAuthenticated) return <Navigate to="/datasources" replace />;
  return <>{children}</>;
};
