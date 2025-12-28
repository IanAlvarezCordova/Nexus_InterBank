import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  token: string | null;
  usuario: string | null;
  login: (token: string, usuario: string) => void;
  logout: () => void;
  isAutenticado: () => boolean;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      usuario: null,
      
      login: (token, usuario) => set({ token, usuario }),
      
      logout: () => set({ token: null, usuario: null }),

      isAutenticado: () => !!get().token,
    }),
    { name: 'ecusol-auth' }
  )
);