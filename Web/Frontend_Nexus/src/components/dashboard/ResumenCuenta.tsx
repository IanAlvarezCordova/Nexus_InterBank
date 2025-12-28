//src/components/dashboard/ResumenCuenta.tsx
import { Tarjeta } from '@/components/common/Tarjeta';
import { formatCurrency } from '@/utils/formatters';
import { CuentaDTO } from '@/types';
import { ArrowRightLeft, CreditCard, FileText, PlusCircle, Eye, EyeOff, Wallet, Briefcase } from 'lucide-react';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface ResumenCuentaProps {
  cuenta: CuentaDTO;
}

export const ResumenCuenta: React.FC<ResumenCuentaProps> = ({ cuenta }) => {
  const [visible, setVisible] = useState(true);
  const navigate = useNavigate();
  const esAhorros = cuenta.tipoCuentaId === 1; // 1=Ahorros, 2=Corriente

  // Estilos dinámicos
  const estiloIcono = esAhorros ? 'bg-blue-50 text-ecusol-primario' : 'bg-gray-100 text-gray-800';
  const estiloSaldo = esAhorros ? 'text-ecusol-primario' : 'text-gray-800';
  
  const IconoBotonAccion = ({ icono, etiqueta, onClick }: any) => (
    <button onClick={onClick} className="flex flex-col items-center text-gray-500 hover:text-ecusol-primario transition-colors group">
      <div className="p-2 rounded-full group-hover:bg-blue-50 transition-colors">
        {React.cloneElement(icono, { size: 20 })}
      </div>
      <span className="text-[10px] font-medium mt-1">{etiqueta}</span>
    </button>
  );

  return (
    <div className={`rounded-2xl p-6 shadow-sm border transition-all hover:shadow-md bg-white border-gray-200 relative overflow-hidden`}>

      <div className={`absolute top-0 left-0 w-full h-1 ${esAhorros ? 'bg-ecusol-secundario' : 'bg-gray-800'}`}></div>

      <div className="flex justify-between items-start mb-4">
        <div className="flex items-center gap-3">
           <div className={`p-3 rounded-xl ${estiloIcono}`}>
             {esAhorros ? <Wallet size={24} /> : <Briefcase size={24} />}
           </div>
           <div>
             <h3 className="text-sm font-bold text-gray-600 uppercase tracking-wide">
                {esAhorros ? 'Ahorros' : 'Corriente'}
             </h3>
             <span className="text-xs text-gray-400 font-mono tracking-wider">**** {cuenta.numeroCuenta.slice(-4)}</span>
           </div>
        </div>
        <span className={`px-2 py-1 rounded text-[10px] font-bold ${cuenta.estado === 'ACTIVA' ? 'bg-green-50 text-green-700' : 'bg-gray-100 text-gray-500'}`}>
            {cuenta.estado}
        </span>
      </div>
      
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-baseline gap-2">
            <span className={`text-3xl font-bold ${estiloSaldo}`}>
            {visible ? formatCurrency(cuenta.saldo) : '$ ****.**'}
            </span>
        </div>
        <button onClick={() => setVisible(!visible)} className="text-gray-300 hover:text-gray-500 transition-colors">
          {visible ? <EyeOff size={18} /> : <Eye size={18} />}
        </button>
      </div>

      <div className="grid grid-cols-4 gap-1 pt-4 border-t border-gray-50">
        <IconoBotonAccion icono={<ArrowRightLeft />} etiqueta="Transferir" onClick={() => navigate('/app/transferir')} />
        <IconoBotonAccion icono={<CreditCard />} etiqueta="Pagar" />
        <IconoBotonAccion icono={<FileText />} etiqueta="Historial" onClick={() => navigate(`/app/cuentas/${cuenta.numeroCuenta}`)} />
        <IconoBotonAccion icono={<PlusCircle />} etiqueta="Opciones" />
      </div>
    </div>
  );
};