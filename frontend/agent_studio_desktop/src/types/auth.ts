export interface SignupRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
}

export interface Organization {
  id: string;
  name: string;
}

export interface UserInfo {
  userId: string;
  email: string;
  organization: Organization;
  planName: string;
  totalQuota: number;
  usedQuota: number;
  remainingQuota: number;
}

export interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  userId: string | null;
  userInfo: UserInfo | null;
  isAuthenticated: boolean;
}
