import apiClient from "@/lib/api/client";
import { authInterceptor } from "@/lib/api/interceptors/authInterceptor";
import { contextInterceptor } from "@/lib/api/interceptors/contextInterceptor";
import { AxiosInstance } from "axios";

// 인증 인터셉터 설정
setupInterceptors(apiClient);

export { apiClient };
export * from "@/lib/api/authClient";

export function setupInterceptors(client: AxiosInstance) {
  client.interceptors.request.use(authInterceptor);
  client.interceptors.request.use(contextInterceptor);
}
