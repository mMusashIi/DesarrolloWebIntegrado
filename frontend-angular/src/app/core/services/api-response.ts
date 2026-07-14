export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  error?: string;
  timestamp?: string;
}

export function unwrapData<T>(response: ApiResponse<T>): T {
  return response.data;
}
