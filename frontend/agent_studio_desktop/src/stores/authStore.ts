import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";
import { UserInfo } from "@/types/auth";

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  userId: string | null;
  userInfo: UserInfo | null;
  isAuthenticated: boolean;
  isInitialized: boolean;
  setTokens: (accessToken: string, refreshToken: string) => void;
  setUserInfo: (userInfo: UserInfo) => void;
  clearAuth: () => void;
  setInitialized: (value: boolean) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      userId: null,
      userInfo: null,
      isAuthenticated: false,
      isInitialized: false,

      setTokens: (accessToken: string, refreshToken: string) => {
        set({
          accessToken,
          refreshToken,
          isAuthenticated: true,
        });
      },

      setUserInfo: (userInfo: UserInfo) => {
        set({
          userInfo,
          userId: userInfo.userId,
        });
      },

      clearAuth: () => {
        set({
          accessToken: null,
          refreshToken: null,
          userId: null,
          userInfo: null,
          isAuthenticated: false,
        });
      },

      setInitialized: (value: boolean) => {
        set({ isInitialized: value });
      },
    }),
    {
      name: "auth-storage",
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        userId: state.userId,
        userInfo: state.userInfo,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);
