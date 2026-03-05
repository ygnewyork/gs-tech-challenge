export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface AuthResponse {
  token: string;
  firstName: string;
  lastName: string;
}

export interface UserProfile {
  username: string;
  firstName: string;
  lastName: string;
  createdAt: string;
}
