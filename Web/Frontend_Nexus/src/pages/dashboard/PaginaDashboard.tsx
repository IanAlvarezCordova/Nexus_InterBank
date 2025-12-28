import React from 'react';
import { useAuthStore } from '@/store/useAuthStore'; 
import { Tarjeta } from '@/components/common/Tarjeta';
import { TrendingUp, CreditCard, Send } from 'lucide-react';

const PaginaDashboard: React.FC = () => {
  
  const { usuario } = useAuthStore();

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-ecusol-primario">
          Buenos Días, {usuario || 'Cliente'}
        </h1>
        <p className="text-gray-600">Es un gusto tenerte hoy aquí.</p>
      </div>
      <Tarjeta>
        <h2 className="text-xl font-semibold mb-3 text-ecusol-primario">
          Bienvenido a tu Nexus Bank 
        </h2>
        <p className="text-gray-700">
          Usa el menú de navegación superior para gestionar tus finanzas. Aquí puedes:
        </p>
        <ul className="list-none mt-4 space-y-3 text-gray-700">
          <li className="flex items-center gap-3">
            <TrendingUp size={20} className="text-ecusol-secundario" />
            Ver tus <strong>Cuentas</strong> y saldos.
          </li>
          <li className="flex items-center gap-3">
            <Send size={20} className="text-ecusol-secundario" />
            Realizar <strong>Transferencias</strong>.
          </li>
          <li className="flex items-center gap-3">
            <CreditCard size={20} className="text-ecusol-secundario" />
            Pagar tus <strong>Servicios</strong>.
          </li>
        </ul>
      </Tarjeta>
    </div>
  );
};

export default PaginaDashboard;