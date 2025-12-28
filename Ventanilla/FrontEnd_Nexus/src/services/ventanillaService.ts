// src/services/ventanillaService.ts
import { apiClient } from "./apiClient";
import { AuthResponse, ResumenClienteDTO, VentanillaOpDTO, InfoCuentaDTO } from "../types";

export const ventanillaService = {
  loginEmpleado: async (usuario: string, clave: string) => {
    // FIX: BASE_URL ya incluye /api/ventanilla, así que usamos ruta relativa sin repetir /ventanilla
    return await apiClient<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ usuario, password: clave }),
    });
  },

  buscarClientePorCedula: async (cedula: string) => {
    return await apiClient<ResumenClienteDTO>(`/clientes/${cedula}`);
  },

  validarDestino: async (cuenta: string) => {
    return await apiClient<InfoCuentaDTO>(`/cuentas/validar/${cuenta}`);
  },

  operar: async (tipo: 'DEPOSITO' | 'RETIRO' | 'TRANSFERENCIA', datos: VentanillaOpDTO) => {
    return await apiClient<string>(`/operaciones`, {
      method: 'POST',
      body: JSON.stringify({ ...datos, tipoOperacion: tipo })
    });
  },

  // --- ADMIN ---

  cambiarEstadoCuenta: async (cuenta: string, estado: string) => {
    const s = estado.toUpperCase();
    const esInactiva = s.startsWith('IN') || s === 'FALSE';
    const finalEstado = esInactiva ? 'INACTIVA' : 'ACTIVA';

    return await apiClient<string>(`/cuenta/estado?cuenta=${cuenta}&estado=${finalEstado}`, {
      method: 'POST'
    });
  },

  cambiarEstadoCliente: async (cedula: string, estado: string) => {
    const s = estado.toUpperCase();
    const esInactivo = s.startsWith('IN') || s === 'FALSE';
    const finalEstado = esInactivo ? 'INACTIVO' : 'ACTIVA';

    return await apiClient<string>(`/cliente/estado?cedula=${cedula}&estado=${finalEstado}`, {
      method: 'POST'
    });
  },

  activarCuenta: async (numeroCuenta: string) => {
    return await apiClient<string>(`/activar-cuenta/${numeroCuenta}`, {
      method: 'POST'
    });
  },

  eliminarCuenta: async (numeroCuenta: string) => {
    return await apiClient<string>(`/cuenta/${numeroCuenta}`, {
      method: 'DELETE'
    });
  }
};