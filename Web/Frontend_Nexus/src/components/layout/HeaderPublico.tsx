import React, { useState } from 'react';
import { LogoEcuSol } from '@/components/common/LogoEcuSol';
import { NavLink, useNavigate } from 'react-router-dom';
import { Boton } from '../common/Boton';
import { Menu, X } from 'lucide-react';

const enlacesPublicos = [
  { nombre: 'Principal', ruta: '/' },
  { nombre: 'Acerca de', ruta: '/acerca-de' },
  { nombre: 'Ayuda', ruta: '/ayuda' },
  { nombre: 'Ubícanos', ruta: '/ubicanos' },
];

export const HeaderPublico: React.FC = () => {
  const navigate = useNavigate();
  const [menuAbierto, setMenuAbierto] = useState(false);

  return (
    <header className="bg-ecusol-primario text-gray-200 w-full relative z-10">
    <nav className="container mx-auto px-6 py-4 flex justify-between items-center">
    <LogoEcuSol size={60} />        
        <div className="hidden md:flex items-center space-x-6">
          {enlacesPublicos.map((enlace) => (
            <NavLink
              key={enlace.ruta}
              to={enlace.ruta}
              className={({ isActive }) =>
                `font-medium text-gray-200 hover:text-white ${isActive ? 'text-white border-b-2 border-ecusol-secundario' : ''}`
              }
            >
              {enlace.nombre}
            </NavLink>
          ))}
        </div>
          <div className="hidden md:flex items-center space-x-3">
          <Boton 
            onClick={() => navigate('/login')} 
            variante="secundario" 
            tamano="mediano"
            className="bg-white text-ecusol-primario hover:bg-gray-100" // Fondo blanco, texto azul
          >
            Iniciar Sesión
          </Boton>
          <Boton 
            onClick={() => navigate('/registro')} 
            variante="primario" 
            tamano="mediano"
            className="bg-ecusol-secundario hover:bg-ecusol-secundario/90" // Fondo naranja
          >Registrarse          
          </Boton>
        </div>
        <div className="md:hidden">
          <button onClick={() => setMenuAbierto(!menuAbierto)} className="text-white">
            {menuAbierto ? <X size={28} /> : <Menu size={28} />}
          </button>
        </div>
      </nav>
      <div 
        className={`md:hidden absolute top-full left-0 w-full bg-white text-ecusol-gris-oscuro shadow-lg z-20 transition-all duration-300 ease-in-out ${menuAbierto ? 'opacity-100 visible' : 'opacity-0 invisible'}`}
      >
        <div className="flex flex-col px-6 py-4 space-y-3">
          {enlacesPublicos.map((enlace) => (
            <NavLink
              key={enlace.ruta}
              to={enlace.ruta}
              onClick={() => setMenuAbierto(false)}
              className={({ isActive }) =>
                `font-medium text-gray-600 hover:text-ecusol-primario ${isActive ? 'text-ecusol-primario' : ''}`
              }
            >
              {enlace.nombre}
            </NavLink>
          ))}
          <hr />
          <div className="flex flex-col space-y-3">
            <Boton onClick={() => { navigate('/login'); setMenuAbierto(false); }} variante="secundario" tamano="mediano">
              Log in
            </Boton>
            <Boton 
              onClick={() => { navigate('/registro'); setMenuAbierto(false); }} 
              variante="primario" 
              tamano="mediano"
              className="bg-ecusol-secundario hover:bg-ecusol-secundario/90" // Botón naranja
            >
              Sign Up
            </Boton>
          </div>
        </div>
      </div>
    </header>
  );
};