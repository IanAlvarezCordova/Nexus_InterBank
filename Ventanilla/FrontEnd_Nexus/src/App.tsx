//src/App.tsx
import { Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { useVentanillaStore } from '@/store/useVentanillaStore';

import LoginEmpleado from '@/pages/LoginEmpleado';
import DashboardVentanilla from '@/pages/DashboardVentanilla';

// Componente para proteger rutas (Solo empleados logueados)
const RutaPrivada = ({ children }: { children: JSX.Element }) => {
  const token = useVentanillaStore(state => state.token);
  return token ? children : <Navigate to="/" />;
};

function App() {
  return (
    <>
      {/* ELIMINADO: <BrowserRouter> (Ya está en main.tsx) */}
      
      <Routes>
        {/* Login es la ruta pública inicial */}
        <Route path="/" element={<LoginEmpleado />} />
        
        {/* Dashboard protegido */}
        <Route path="/dashboard" element={
          <RutaPrivada><DashboardVentanilla /></RutaPrivada>
        } />
        
        {/* Cualquier ruta desconocida redirige al login */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>

      {/* Notificaciones Globales */}
      <Toaster position="top-right" toastOptions={{
         duration: 4000,
         style: { 
           background: '#1F2937', 
           color: '#fff',
           borderRadius: '12px',
           padding: '16px'
         }
      }}/>
      
      {/* ELIMINADO: </BrowserRouter> */}
    </>
  );
}

export default App;