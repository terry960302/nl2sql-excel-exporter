import React, { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuthStore } from "@/stores/authStore";

interface AuthRedirectProps {
  children: React.ReactNode;
}

export const AuthRedirect: React.FC<AuthRedirectProps> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { isAuthenticated, isInitialized } = useAuthStore();

  useEffect(() => {
    if (!isInitialized) return;

    const isPublicPath = ["/login", "/signup"].includes(location.pathname);
    const isRootPath = location.pathname === "/";

    if (isAuthenticated) {
      if (isPublicPath || isRootPath) {
        navigate("/datasources", { replace: true });
      }
    } else {
      if (!isPublicPath) {
        navigate("/login", { replace: true });
      }
    }
  }, [isAuthenticated, isInitialized, location.pathname, navigate]);

  if (!isInitialized) {
    return null;
  }

  return <>{children}</>;
};
