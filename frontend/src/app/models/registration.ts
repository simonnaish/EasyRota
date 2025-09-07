export interface RegistrationRequest {
  email: string;
  password: string;
  fullName?: string;
}

export interface RegistrationResponse {
  status: string;
  userId: number;
}
