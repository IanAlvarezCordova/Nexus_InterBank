export interface AuthResponse {
  token: string;
  usuario: string;
}

export interface CuentaDTO {
  cuentaId: number;
  numeroCuenta: string;
  saldo: number;
  estado: string;
  tipoCuentaId: number;
}

export interface MovimientoDTO {
  fecha: string;
  tipo: 'C' | 'D';
  monto: number;
  saldoNuevo: number;
  descripcion: string;
  operacion?: string; // DEPOSITO | RETIRO | TRANSFERENCIA
}

export interface DestinatarioDTO {
  numeroCuenta: string;
  nombreTitular: string;
  cedulaParcial: string;
  tipoCuenta?: string; 
}

export interface TransferenciaRequest {
  cuentaOrigen: string;
  cuentaDestino: string;
  monto: number;
  descripcion: string;
  bancoDestino?: string; // NEXUS, ECUASOL, ARBANCK, REPLICA
}


export interface Beneficiario {
  id?: number;
  numeroCuenta: string;
  nombreTitular: string;
  alias: string;
  email?: string;
  tipoCuenta?: string;
}