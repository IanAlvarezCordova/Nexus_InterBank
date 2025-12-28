// src/services/apiClient.ts
import { useVentanillaStore } from "@/store/useVentanillaStore";

const BASE_URL = '/api/ventanilla';
// const BASE_URL = import.meta.env.VITE_API_URL;

export const apiClient = async <T>(endpoint: string, options: RequestInit = {}): Promise<T> => {
  const { token, logout } = useVentanillaStore.getState();

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

    // 1. Leer respuesta como texto una sola vez
    const textBody = await response.text();
    
    // 2. Intentar parsear JSON
    let data: any = null;
    try {
        if (textBody) data = JSON.parse(textBody);
    } catch (e) {
        data = textBody;
    }

    // 3. Manejo de Errores
    if (!response.ok) {
      // Si es error de sesión (401/403) y no estamos intentando loguearnos
      if ((response.status === 401 || response.status === 403) && !endpoint.includes('/auth/login')) {
        logout();
        window.location.href = '/'; 
        throw new Error('Sesión expirada.');
      }

      // Extraer mensaje limpio
      const msg = data?.message || data?.error || (typeof data === 'string' ? data : 'Error en el servidor');
      throw new Error(msg);
    }

    return data as T;

  } catch (error: any) {
    console.error("API Error:", error);
    throw error;
  }
};