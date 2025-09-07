export interface RegistrationRequest {
  email: string;
  password: string;
  fullName?: string;
}

export interface RegistrationResponse {
  status: string;
  userId: number;
}

export interface LoginRequest { email: string; password: string; }
export interface TokenRequest { refreshToken: string; }
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  userId: number;
  email: string;
  roles: string[];
}

export interface MeResponse {
  userId: number;
  email: string;
  roles: string[];
}
