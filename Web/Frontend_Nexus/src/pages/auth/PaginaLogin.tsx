import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';
import { authService } from '@/services/authService';
import { Boton } from '@/components/common/Boton';
import { Input } from '@/components/common/Input';
import { LogoEcuSol } from '@/components/common/LogoEcuSol';
import { toast } from 'react-hot-toast';
import { Loader2 } from 'lucide-react';

const PaginaLogin = () => {
  const [usuario, setUsuario] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  
  const navigate = useNavigate();
  const setAuth = useAuthStore(state => state.login);

  const handleUserChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const valorLimpio = e.target.value.toUpperCase().replace(/[^A-Z0-9]/g, '');
    setUsuario(valorLimpio);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      const response = await authService.login(usuario, password);

      setAuth(response.token, response.usuario);
      
      toast.success(`¡Bienvenido, ${response.usuario}!`);

      setTimeout(() => {
         navigate('/app/dashboard');
      }, 1000);

    } catch (err: any) {
      toast.error(err.message || 'Error de conexión');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen bg-white">
      <div className="hidden lg:flex lg:w-1/2 bg-ecusol-primario items-center justify-center relative overflow-hidden">
        <div className="absolute inset-0 opacity-20 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')]"></div>
        <div className="z-10 text-center text-white p-12">
          <h1 className="text-5xl font-bold mb-6">Nexus Bank</h1>
          <p className="text-xl text-ecusol-secundario">Tu banca digital segura y rápida.</p>
        </div>
      </div>

      <div className="w-full lg:w-1/2 flex items-center justify-center p-8 bg-gray-50">
        <div className="w-full max-w-md bg-white p-8 rounded-2xl shadow-xl">
          <div className="flex justify-center mb-6">
            <LogoEcuSol size={60} className="text-ecusol-primario" />
          </div>
          <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">Iniciar Sesión</h2>

          <form onSubmit={handleSubmit} className="space-y-6">
            <Input 
              id="user" 
              label="Usuario" 
              value={usuario} 
              onChange={handleUserChange} 
              placeholder="Ej: IANALVAREZ"
              className="uppercase font-bold tracking-wider text-ecusol-primario"
            />
            <Input 
              id="pass" 
              type="password" 
              label="Contraseña" 
              value={password} 
              onChange={e => setPassword(e.target.value)} 
              placeholder="••••••••"
            />

            <Boton type="submit" disabled={loading} className="w-full py-3 shadow-lg">
              {loading ? <Loader2 className="animate-spin" /> : 'Ingresar'}
            </Boton>
          </form>
          
          <div className="mt-6 text-center text-sm">
            <span className="text-gray-600">¿Nuevo cliente? </span>
            <span className="text-ecusol-secundario font-bold cursor-pointer hover:underline" onClick={() => navigate('/registro')}>
              Regístrate aquí
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PaginaLogin;