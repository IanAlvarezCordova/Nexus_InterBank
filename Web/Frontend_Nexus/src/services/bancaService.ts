
import { apiClient } from "./apiClient";
import { CuentaDTO, DestinatarioDTO, MovimientoDTO, TransferenciaRequest } from "@/types";


export interface Beneficiario {
  tipoCuenta: any;
  id?: number;
  numeroCuenta: string;
  nombreTitular: string;
  alias: string;
  email?: string;
}

export interface Sucursal {
  id: number;
  nombre: string;
  direccion: string;
  telefono: string;
  lat: number;
  lng: number;
}

export const bancaService = {
  getMisCuentas: async () => {
    return await apiClient<CuentaDTO[]>('/web/cuentas');
  },

  getMovimientos: async (numeroCuenta: string) => {
    return await apiClient<MovimientoDTO[]>(`/web/movimientos/${numeroCuenta}`);
  },

  validarDestinatario: async (numeroCuenta: string, banco?: string) => {
    const url = banco 
      ? `/web/validar-destinatario/${numeroCuenta}?banco=${banco}`
      : `/web/validar-destinatario/${numeroCuenta}`;
    return await apiClient<DestinatarioDTO>(url);
  },

  transferir: async (data: TransferenciaRequest) => {
    return await apiClient<string>('/web/transferir', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },

  solicitarCuenta: async (tipoCuentaId: number) => {
    return await apiClient<string>(`/web/solicitar-cuenta?tipoCuentaId=${tipoCuentaId}`, {
      method: 'POST'
    });
  },

  
  getBeneficiarios: async () => {
    return await apiClient<Beneficiario[]>('/web/beneficiarios');
  },

  guardarBeneficiario: async (data: Beneficiario) => {
    return await apiClient<string>('/web/beneficiarios', {
        method: 'POST',
        body: JSON.stringify(data)
    });
  },

  getSucursales: async () => {
    
    return await apiClient<Sucursal[]>('/web/sucursales'); 
  }
};