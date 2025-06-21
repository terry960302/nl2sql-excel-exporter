import { InternalAxiosRequestConfig } from "axios";
import { useAuthStore } from "@/stores/authStore";
import { HEADERS } from "@/lib/api/constants/headers";

export const contextInterceptor = (config: InternalAxiosRequestConfig) => {
  const { userId, userInfo } = useAuthStore.getState();
  const orgId = userInfo?.organization.id;
  const agentId = import.meta.env.VITE_AGENT_ID;

  if (userId) {
    config.headers.set(HEADERS.USER_ID, userId);
  }
  if (orgId) {
    config.headers.set(HEADERS.ORG_ID, orgId);
  }
  if (agentId) {
    config.headers.set(HEADERS.AGENT_ID, agentId as string);
  }

  return config;
};
