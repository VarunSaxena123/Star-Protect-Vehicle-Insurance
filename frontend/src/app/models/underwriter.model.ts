export interface Underwriter {
  underwriterId: string;
  name: string;
  dob: string;
  joiningDate: string;
  password?: string;
  createdDate?: string;
}

export interface UnderwriterSearchResult {
  found: boolean;
  underwriterId?: string;
  name?: string;
  dob?: string;
  age?: number;
  joiningDate?: string;
  message?: string;
}

export interface RegisterUnderwriterRequest {
  name: string;
  dob: string;
  joiningDate: string;
  password: string;
}

export interface UpdatePasswordRequest {
  underwriterId: string;
  newPassword: string;
  confirmPassword: string;
}