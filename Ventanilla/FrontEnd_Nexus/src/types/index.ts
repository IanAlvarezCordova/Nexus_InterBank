export interface AuthResponse {
  token: string;
  nombreSucursal: string;
  sucursalId: number;
}

export interface CuentaResumenDTO {
  numeroCuenta: string;
  tipo: string; 
  saldo: number;
  estado: string;
}

export interface ResumenClienteDTO {
  clienteId: number;
  nombres: string; 
  cedula: string;
  estado: string;
  cuentas: CuentaResumenDTO[];
}

export interface InfoCuentaDTO {
    numeroCuenta: string;
    nombreCompleto: string; 
    tipoCuenta: string;
}

export interface VentanillaOpDTO {
  numeroCuentaOrigen: string;
  numeroCuentaDestino?: string; 
  monto: number;
  descripcion: string;
}