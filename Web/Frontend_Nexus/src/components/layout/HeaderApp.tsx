// src/components/layout/HeaderApp.tsx
import React, { useState } from 'react';
import { LogoEcuSol } from '@/components/common/LogoEcuSol';
import { useAuthStore } from '@/store/useAuthStore'; // <--- USAR EL STORE DIRECTO
import { NavLink, useNavigate } from 'react-router-dom';
import { Boton } from '@/components/common/Boton';
import { User, Menu, X } from 'lucide-react';

const enlaces = [
  { nombre: 'Principal', ruta: '/app/dashboard' },
  { nombre: 'Cuentas', ruta: '/app/cuentas' },
  { nombre: 'Transferir', ruta: '/app/transferir' },
  { nombre: 'Ubícanos', ruta: '/app/ubicanos' }, // Agregado
  { nombre: 'Ayuda', ruta: '/app/ayuda' },
];

export const HeaderApp: React.FC = () => {
  const { usuario, logout } = useAuthStore(); // Usando el store
  const navigate = useNavigate();
  const [menuAbierto, setMenuAbierto] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="bg-ecusol-primario text-gray-200 w-full relative shadow-md z-50">
      <nav className="container mx-auto px-6 py-3 flex justify-between items-center">
        
        <div className="flex items-center gap-2 cursor-pointer" onClick={() => navigate('/app/dashboard')}>
           <LogoEcuSol size={50} />
           <span className="font-bold text-white text-xl tracking-tight hidden sm:block">Nexus Bank</span>
        </div>
        
        <div className="hidden md:flex items-center space-x-1">
          {enlaces.map((enlace) => (
            <NavLink
              key={enlace.ruta}
              to={enlace.ruta}
              className={({ isActive }) =>
                `px-4 py-2 rounded-lg text-sm font-medium transition-colors ${isActive ? 'bg-white/10 text-white font-bold shadow-sm' : 'hover:bg-white/5 hover:text-white'}`
              }
            >
              {enlace.nombre}
            </NavLink>
          ))}
        </div>
        
        <div className="hidden md:flex items-center space-x-4">
          <span className="text-gray-200 flex items-center gap-2 text-sm">
            <div className="bg-ecusol-secundario p-1.5 rounded-full text-white">
                <User size={16} />
            </div>
            <span className="font-medium">{usuario}</span>
          </span>
          
          <Boton 
            onClick={handleLogout} 
            variante="secundario"
            className="bg-white/10 hover:bg-red-600 hover:text-white text-white border-none"
            tamano="pequeno"
          >
            Salir
          </Boton>
        </div>

        <div className="md:hidden">
          <button onClick={() => setMenuAbierto(!menuAbierto)} className="text-white p-2">
            {menuAbierto ? <X size={28} /> : <Menu size={28} />}
          </button>
        </div>
      </nav>

      <div className={`md:hidden absolute top-full left-0 w-full bg-ecusol-primario border-t border-white/10 shadow-lg z-20 transition-all duration-300 ease-in-out origin-top ${menuAbierto ? 'scale-y-100 opacity-100' : 'scale-y-0 opacity-0'}`}>
        <div className="flex flex-col px-6 py-4 space-y-2">
          {enlaces.map((enlace) => (
            <NavLink
              key={enlace.ruta}
              to={enlace.ruta}
              onClick={() => setMenuAbierto(false)}
              className={({ isActive }) =>
                `block px-4 py-3 rounded-lg font-medium ${isActive ? 'bg-white/20 text-white' : 'text-gray-300 hover:bg-white/10'}`
              }
            >
              {enlace.nombre}
            </NavLink>
          ))}
          <div className="border-t border-white/10 my-2 pt-2">
             <div className="flex items-center gap-2 text-gray-300 px-4 py-2">
                <User size={16} /> {usuario}
             </div>
             <button onClick={handleLogout} className="w-full text-left px-4 py-3 text-red-300 hover:bg-white/10 rounded-lg">
               Cerrar Sesión
             </button>
          </div>
        </div>
      </div>
    </header>
  );
};