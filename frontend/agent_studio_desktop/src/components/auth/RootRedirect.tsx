import { useAuthStore } from "@/stores/authStore";
import { Navigate } from "react-router-dom";

export const RootRedirect = () => {
  const { isAuthenticated, isInitialized } = useAuthStore();

  console.log("isAuthenticated", isAuthenticated);
  if (!isInitialized) return null;
  return <Navigate to={isAuthenticated ? "/datasources" : "/login"} replace />;
};
