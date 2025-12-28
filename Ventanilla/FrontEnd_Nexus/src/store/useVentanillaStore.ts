//src/store/useVentanillaStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface VentanillaState {
  token: string | null;
  usuarioEmpleado: string | null;
  nombreSucursal: string | null;
  sucursalId: number | null;
  
  login: (token: string, usuario: string, sucursal: string, sucursalId: number) => void;
  logout: () => void;
  isAutenticado: () => boolean;
}

export const useVentanillaStore = create<VentanillaState>()(
  persist(
    (set, get) => ({
      token: null,
      usuarioEmpleado: null,
      nombreSucursal: null,
      sucursalId: null,
      
      login: (token, usuario, sucursal, sucursalId) => set({ 
          token, 
          usuarioEmpleado: usuario, 
          nombreSucursal: sucursal,
          sucursalId: sucursalId
      }),
      
      logout: () => set({ token: null, usuarioEmpleado: null, nombreSucursal: null, sucursalId: null }),
      
      isAutenticado: () => !!get().token,
    }),
    { name: 'nexus-ventanilla-session' }
  )
);