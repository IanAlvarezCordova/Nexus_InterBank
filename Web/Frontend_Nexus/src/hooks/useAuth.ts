import { useAuthStore } from "@/store/useAuthStore";

const useAuth = () => {
  const isAutenticado = useAuthStore((state) => state.isAutenticado);
  const usuario = useAuthStore((state) => state.usuario);
  const login = useAuthStore((state) => state.login);
  const logout = useAuthStore((state) => state.logout);

  return {
    isAutenticado,
    usuario,
    login,
    logout,
  };
};

export default useAuth;