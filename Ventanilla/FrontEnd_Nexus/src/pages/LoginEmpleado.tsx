//ubi: src/pages/LoginEmpleado.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ventanillaService } from '../services/ventanillaService';
import { useVentanillaStore } from '../store/useVentanillaStore';
import { LogoNexus } from '../components/common/LogoNexus';
import { Boton } from '../components/common/Boton';
import { Input } from '../components/common/Input';
import { toast } from 'react-hot-toast';
import { ShieldCheck, Loader2 } from 'lucide-react';

const LoginEmpleado = () => {
  const navigate = useNavigate();
  const setSesion = useVentanillaStore(state => state.login);
  
  const [usuario, setUsuario] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const data = await ventanillaService.loginEmpleado(usuario, password);
      
      setSesion(
          data.token, 
          usuario, 
          data.nombreSucursal || 'Sucursal Central', 
          data.sucursalId || 1
      );
      
      toast.success(`Bienvenido a ${data.nombreSucursal}`);
      navigate('/dashboard');
      
    } catch (error: any) {
      console.error(error);
      toast.error(error.message || "Error de conexión con Ventanilla");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex bg-gray-100 font-sans">
      {/* Izquierda */}
      <div className="hidden lg:flex w-1/2 bg-nexus-primario items-center justify-center relative overflow-hidden">
        <div className="absolute inset-0 bg-black/10"></div>
        <div className="text-center z-10 text-white p-12">
          <ShieldCheck size={80} className="mx-auto mb-6 opacity-90" />
          <h1 className="text-4xl font-bold mb-2">Portal de Ventanilla</h1>
          <p className="text-nexus-secundario font-medium text-xl">Sistema Interno Banco Nexus</p>
          <div className="mt-10 bg-white/10 p-4 rounded-lg text-sm text-left max-w-sm mx-auto backdrop-blur-md">
             <p>⚠️ <strong>Acceso Restringido</strong></p>
             <p className="opacity-80 mt-1">Este sistema es para uso exclusivo de personal autorizado. Toda actividad es monitoreada.</p>
          </div>
        </div>
      </div>

      {/* Derecha */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-8">
        <div className="w-full max-w-md bg-white p-10 rounded-3xl shadow-2xl">
          <div className="flex flex-col items-center mb-8">
            <LogoNexus size={70} className="mb-4" />
            <h2 className="text-2xl font-bold text-gray-800">Acceso de Empleado</h2>
            <p className="text-gray-500">Ingrese sus credenciales corporativas</p>
          </div>

          <form onSubmit={handleLogin} className="space-y-6">
            <Input 
              id="user" 
              label="Usuario" 
              placeholder="Ej: cajero1"
              value={usuario}
              onChange={e => setUsuario(e.target.value)}
              autoFocus
            />
            
            <Input 
              id="pass" 
              type="password" 
              label="Contraseña" 
              placeholder="••••••••"
              value={password}
              onChange={e => setPassword(e.target.value)}
            />

            <Boton 
                type="submit" 
                disabled={loading}
                className="w-full py-4 text-lg shadow-lg mt-4"
            >
                {loading ? <Loader2 className="animate-spin mx-auto" /> : 'Ingresar al Sistema'}
            </Boton>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoginEmpleado;