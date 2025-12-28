// src/components/dashboard/GraficoGastos.tsx
import { Tarjeta } from '@/components/common/Tarjeta';
import { BarChart } from 'lucide-react';

export const GraficoGastos = () => {
  return (
    <Tarjeta className="w-full">
      <h3 className="text-lg font-semibold mb-4">Gráfico Gastos en el Mes</h3>
      <div className="h-64 bg-gray-100 rounded-lg flex items-center justify-center flex-col text-gray-400">
        <BarChart size={48} />
        <p className="mt-2">Datos del gráfico no disponibles</p>
      </div>
      <div className="mt-4 space-y-1">
        <p className="text-gray-700">Gasto Total: <span className="font-bold">$ XXXX.XX</span></p>
        <p className="text-gray-700">Mayor transferencia: <span className="font-bold">$ XXXX.XX</span></p>
      </div>
    </Tarjeta>
  );
};