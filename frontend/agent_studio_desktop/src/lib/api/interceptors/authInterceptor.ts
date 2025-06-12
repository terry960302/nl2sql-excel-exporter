import { InternalAxiosRequestConfig } from "axios";
import { useAuthStore } from "@/stores/authStore";
import { HEADERS, AUTH_HEADER_PREFIX } from "@/lib/api/constants/headers";

export const authInterceptor = (config: InternalAxiosRequestConfig) => {
  const store = useAuthStore;
  const accessToken = store.getState().accessToken;

  if (accessToken) {
    config.headers.set(
      HEADERS.AUTHORIZATION,
      `${AUTH_HEADER_PREFIX} ${accessToken}`
    );
  }

  return config;
};
