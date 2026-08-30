export interface LoginRequest {
  userId: string;
  password: string;
  role: string;
}

export interface LoginResponse {
  success: boolean;
  role: string | null;
  userId: string | null;
  name: string | null;
  message: string;
}

export interface CurrentUser {
  userId: string;
  name: string;
  role: string;
}