import useAuth from '@/hooks/useAuth';
import { Navigate, useLocation } from 'react-router-dom';

interface RutaProtegidaProps {
  children: React.ReactElement;
}

const RutaProtegida: React.FC<RutaProtegidaProps> = ({ children }) => {
  const { isAutenticado } = useAuth();
  const location = useLocation();

  if (!isAutenticado) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
};

export default RutaProtegida;