import { Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { useVentanillaStore } from '@/store/useVentanillaStore';

import LoginEmpleado from '@/pages/LoginEmpleado';
import DashboardVentanilla from '@/pages/DashboardVentanilla';

const RutaPrivada = ({ children }: { children: JSX.Element }) => {
  const token = useVentanillaStore(state => state.token);
  return token ? children : <Navigate to="/" />;
};

function App() {
  return (
    <>
      
      <Routes>
        <Route path="/" element={<LoginEmpleado />} />
        
        <Route path="/dashboard" element={
          <RutaPrivada><DashboardVentanilla /></RutaPrivada>
        } />
        
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>

      <Toaster position="top-right" toastOptions={{
         duration: 4000,
         style: { 
           background: '#1F2937', 
           color: '#fff',
           borderRadius: '12px',
           padding: '16px'
         }
      }}/>
      
    </>
  );
}

export default App;