import apiClient from "@/lib/api/client";
import { authInterceptor } from "@/lib/api/interceptors/authInterceptor";
import { AxiosInstance } from "axios";

// 인증 인터셉터 설정
setupAuthInterceptors(apiClient);

export { apiClient };
export * from "@/lib/api/authClient";

export function setupAuthInterceptors(client: AxiosInstance) {
  client.interceptors.request.use(authInterceptor);
}
