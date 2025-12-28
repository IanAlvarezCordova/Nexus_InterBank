import { MovimientoDTO } from '@/types';
import { formatCurrency } from '@/utils/formatters';
import { Tarjeta } from '../common/Tarjeta';
import { ChevronRight, ArrowDownLeft, ArrowUpRight } from 'lucide-react';

interface ItemMovimientoProps {
  movimiento: MovimientoDTO;
}

const ItemMovimiento: React.FC<ItemMovimientoProps> = ({ movimiento }) => {
  const esCredito = movimiento.tipo === 'C';
  const colorMonto = esCredito ? 'text-green-600' : 'text-red-600';
  const signo = esCredito ? '+' : '-';

  return (
    <Tarjeta className="w-64 flex-shrink-0 border border-gray-100 mr-4">
      <div className="flex justify-between items-start mb-2">
         <span className={`p-1 rounded ${esCredito ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
            {esCredito ? <ArrowDownLeft size={16}/> : <ArrowUpRight size={16}/>}
         </span>
         <span className="text-xs text-gray-400">{new Date(movimiento.fecha).toLocaleDateString()}</span>
      </div>
      <p className="font-semibold text-sm text-gray-700 mb-1">{esCredito ? 'Depósito / Recibido' : 'Retiro / Enviado'}</p>
      <p className={`text-xl font-bold ${colorMonto}`}>
        {signo}{formatCurrency(movimiento.monto)}
      </p>
    </Tarjeta>
  );
};

interface MovimientosRecientesProps {
  movimientos: MovimientoDTO[];
}

export const MovimientosRecientes: React.FC<MovimientosRecientesProps> = ({ movimientos }) => {
  if (!movimientos || movimientos.length === 0) return null;

  return (
    <div className="w-full mt-8">
      <div className="flex justify-between items-center mb-4 px-1">
        <h2 className="text-lg font-bold text-gray-700">Actividad Reciente</h2>
      </div>
      <div className="flex overflow-x-auto pb-4 scrollbar-hide">
        {movimientos.map((mov, i) => (
          <ItemMovimiento key={i} movimiento={mov} />
        ))}
      </div>
    </div>
  );
};