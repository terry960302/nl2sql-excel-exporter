import { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { authClient, SignupRequest, LoginRequest } from "@/lib/api/authClient";
import { useAuthStore } from "@/stores/authStore";

export const useAuth = () => {
  const navigate = useNavigate();
  const {
    isAuthenticated,
    accessToken,
    refreshToken,
    userId,
    userInfo,
    setTokens,
    setUserInfo,
    clearAuth,
  } = useAuthStore();

  const signup = useCallback(async (data: SignupRequest) => {
    try {
      await authClient.signup(data);
      return true;
    } catch (error) {
      console.error("Signup failed:", error);
      return false;
    }
  }, []);

  const login = useCallback(
    async (data: LoginRequest) => {
      try {
        const { accessToken, refreshToken } = await authClient.login(data);
        setTokens(accessToken, refreshToken);

        const userInfo = await authClient.getMyInfo(accessToken);
        setUserInfo(userInfo);

        navigate("/dashboard");
        return true;
      } catch (error) {
        console.error("Login failed:", error);
        return false;
      }
    },
    [navigate, setTokens, setUserInfo, userId]
  );

  const logout = useCallback(async () => {
    try {
      console.log("Logout started");
      if (accessToken && refreshToken) {
        console.log("Calling logout API");
        await authClient.logout(accessToken, refreshToken);
      }
      console.log("Clearing auth state");
      clearAuth();
      console.log("Logout completed");
    } catch (error) {
      console.error("Logout failed:", error);
      clearAuth();
    }
  }, [accessToken, refreshToken, clearAuth]);

  const refreshAccessToken = useCallback(async () => {
    try {
      if (!refreshToken) return false;

      const { accessToken: newAccessToken, refreshToken: newRefreshToken } =
        await authClient.refreshToken(refreshToken);

      setTokens(newAccessToken, newRefreshToken);
      return true;
    } catch (error) {
      console.error("Token refresh failed:", error);
      clearAuth();
      navigate("/login");
      return false;
    }
  }, [refreshToken, setTokens, clearAuth, navigate]);

  return {
    isAuthenticated,
    accessToken,
    refreshToken,
    userId,
    userInfo,
    setTokens,
    setUserInfo,
    clearAuth,
    signup,
    login,
    logout,
    refreshAccessToken,
  };
};
