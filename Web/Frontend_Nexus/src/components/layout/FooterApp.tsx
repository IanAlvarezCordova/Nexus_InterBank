import { LogoEcuSol } from '@/components/common/LogoEcuSol';
import { Mail, MapPin, Phone, Twitter, Linkedin, Facebook } from 'lucide-react';
import { Link } from 'react-router-dom';

export const FooterApp = () => {
  return (
      <footer className="bg-ecusol-gris-oscuro text-gray-300 pt-16 pb-8 mt-auto">
      <div className="container mx-auto px-6 grid grid-cols-1 md:grid-cols-4 gap-8">
        <div>
          <div className="flex items-center gap-2 mb-4">
             <LogoEcuSol size={40} />
             <span className="text-white font-bold text-lg">Nexus Bank</span>
          </div>
          <p className="text-sm text-gray-400">Innovación y seguridad financiera a tu alcance.</p>
          <div className="flex space-x-4 mt-4">
            <a href="#" className="hover:text-white transition-colors"><Twitter size={20} /></a>
            <a href="#" className="hover:text-white transition-colors"><Linkedin size={20} /></a>
            <a href="#" className="hover:text-white transition-colors"><Facebook size={20} /></a>
          </div>
        </div>

        <div>
          <h5 className="font-bold text-white mb-4 uppercase tracking-wider text-sm">Navegación</h5>
          <ul className="space-y-2 text-sm">
            <li><Link to="/app/dashboard" className="hover:text-white transition-colors">Inicio</Link></li>
            <li><Link to="/app/transferir" className="hover:text-white transition-colors">Transferencias</Link></li>
            <li><Link to="/app/cuentas" className="hover:text-white transition-colors">Mis Cuentas</Link></li>
            <li><Link to="/app/ubicanos" className="hover:text-white transition-colors">Agencias</Link></li>
          </ul>
        </div>

        <div>
          <h5 className="font-bold text-white mb-4 uppercase tracking-wider text-sm">Ayuda</h5>
          <ul className="space-y-2 text-sm">
            <li><Link to="/app/ayuda" className="hover:text-white transition-colors">Centro de Ayuda</Link></li>
            <li><a href="#" className="hover:text-white transition-colors">Bloqueos</a></li>
            <li><a href="#" className="hover:text-white transition-colors">Seguridad</a></li>
          </ul>
        </div>

        <div>
          <h5 className="font-bold text-white mb-4 uppercase tracking-wider text-sm">Contacto</h5>
          <ul className="space-y-3 text-sm">
            <li className="flex items-center gap-2 text-gray-400">
              <MapPin size={16} className="text-ecusol-secundario"/> Av. Amazonas N21 y República
            </li>
            <li className="flex items-center gap-2 text-gray-400">
              <Phone size={16} className="text-ecusol-secundario"/> 1800-Nexus (328765)
            </li>
            <li className="flex items-center gap-2 text-gray-400">
              <Mail size={16} className="text-ecusol-secundario"/> contacto@nexus.com
            </li>
          </ul>
        </div>
      </div>

      <div className="container mx-auto px-6 mt-12 pt-8 border-t border-gray-700 flex flex-col md:flex-row justify-between items-center text-xs text-gray-500">
        <p>© 2025 Nexus Bank S.A. Todos los derechos reservados.</p>
        <div className="flex space-x-6 mt-4 md:mt-0">
          <a href="#" className="hover:text-white">Privacidad</a>
          <a href="#" className="hover:text-white">Términos</a>
          <a href="#" className="hover:text-white">Cookies</a>
        </div>
      </div>
    </footer>
  );
};