import { apiClient } from "@/lib/api/client";
import {
  SignupRequest,
  LoginRequest,
  LoginResponse,
  UserInfo,
} from "@/types/auth";
import { HEADERS, AUTH_HEADER_PREFIX } from "@/lib/api/constants/headers";
import { API_ENDPOINTS } from "@/lib/api/constants/endpoints";

export type { SignupRequest, LoginRequest };

export const authClient = {
  async signup(data: SignupRequest): Promise<void> {
    await apiClient.post(`${API_ENDPOINTS.AUTH}/signup`, data);
  },

  async login(data: LoginRequest): Promise<LoginResponse> {
    const response = await apiClient.post<LoginResponse>(
      `${API_ENDPOINTS.AUTH}/login`,
      data
    );
    return response.data;
  },

  async getMyInfo(accessToken: string): Promise<UserInfo> {
    const response = await apiClient.get<UserInfo>(`${API_ENDPOINTS.AUTH}/me`, {
      headers: {
        [HEADERS.AUTHORIZATION]: `${AUTH_HEADER_PREFIX} ${accessToken}`,
      },
    });
    return response.data;
  },

  async refreshToken(refreshToken: string): Promise<LoginResponse> {
    const response = await apiClient.post<LoginResponse>(
      `${API_ENDPOINTS.AUTH}/token/refresh`,
      {},
      {
        headers: {
          [HEADERS.X_REFRESH_TOKEN]: refreshToken,
        },
      }
    );
    return response.data;
  },

  async logout(accessToken: string, refreshToken: string): Promise<void> {
    await apiClient.post(
      `${API_ENDPOINTS.AUTH}/logout`,
      {},
      {
        headers: {
          [HEADERS.AUTHORIZATION]: `${AUTH_HEADER_PREFIX} ${accessToken}`,
          [HEADERS.X_REFRESH_TOKEN]: refreshToken,
        },
      }
    );
  },
};
