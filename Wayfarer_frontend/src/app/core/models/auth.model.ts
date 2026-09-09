export interface AuthResponse {
  accessToken: string;
}

export interface TokenValidationResponse {
  valid: boolean;
  refreshable: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}
