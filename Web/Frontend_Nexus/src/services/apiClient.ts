import { useAuthStore } from "@/store/useAuthStore";

const BASE_URL = '/api';
// const BASE_URL = import.meta.env.VITE_API_URL;

export const apiClient = async <T>(endpoint: string, options: RequestInit = {}): Promise<T> => {
  const { token, logout } = useAuthStore.getState();

  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...options.headers,
  };

  try {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
      ...options,
      headers,
    });


    const textBody = await response.text();


    let data: any = null;
    try {
      if (textBody) data = JSON.parse(textBody);
    } catch (e) {

      data = textBody;
    }


    if (!response.ok) {
      if ((response.status === 401 || response.status === 403) && !endpoint.includes('/auth/login')) {
        logout();
        window.location.href = '/login';
        throw new Error('Sesión expirada.');
      }


      const msg = data?.message || data?.error || (typeof data === 'string' ? data : 'Error en el servidor');
      throw new Error(msg);
    }


    return data as T;

  } catch (error: any) {
    console.error("API Error:", error);
    throw error;
  }
};