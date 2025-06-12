// useAuthInit.ts
import { useEffect } from "react";
import { useAuthStore } from "@/stores/authStore";

export const useAuthInit = () => {
  const { isInitialized, accessToken, isAuthenticated, setInitialized } =
    useAuthStore();

  useEffect(() => {
    if (!isInitialized) {
      setInitialized(true);

      if (accessToken && !isAuthenticated) {
        useAuthStore.setState({ isAuthenticated: true });
      }
    }
  }, [isInitialized, accessToken, isAuthenticated, setInitialized]);
};
