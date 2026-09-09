export interface UserResponse {
  id: number;
  username: string;
  email: string;
  createdAt: string;
}

export interface UserUpdateRequest {
  username: string;
  email: string;
}
