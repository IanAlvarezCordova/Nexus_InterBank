
import { apiClient } from "./apiClient";
import { AuthResponse } from "@/types";


export interface RegisterData {
  cedula: string;
  nombres: string;
  apellidos: string;
  email: string;
  usuario: string;
  password: string;
  telefono: string;
  direccion: string;
}

export const authService = {

  login: async (usuario: string, clave: string) => {
    return await apiClient<AuthResponse>('/web/auth/login/web', {
      method: 'POST',
      body: JSON.stringify({ usuario, password: clave }),
    });
  },


  register: async (datos: RegisterData) => {

    return await apiClient<string>('/web/auth/register', {
      method: 'POST',
      body: JSON.stringify(datos),
    });
  }
};