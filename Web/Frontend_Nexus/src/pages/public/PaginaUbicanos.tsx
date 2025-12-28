import { useState, useEffect } from 'react';
import { bancaService } from '@/services/bancaService';
import { Tarjeta } from '@/components/common/Tarjeta';
import { MapPin, Phone, Navigation } from 'lucide-react';

const PaginaUbicanos = () => {
  const [sucursales, setSucursales] = useState<any[]>([]);

  useEffect(() => {
   
    bancaService.getSucursales().then(setSucursales);
  }, []);

  return (
    <div className="bg-white py-16">
      <div className="container mx-auto px-6">
        <div className="text-center mb-12">
            <h1 className="text-4xl font-bold text-ecusol-primario mb-4">Nuestras Agencias</h1>
            <p className="text-gray-600">Encuentra la oficina de Nexus Bank más cercana a ti.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {sucursales.map((sucursal) => (
                <Tarjeta key={sucursal.id} className="border border-gray-100 hover:border-ecusol-secundario transition-colors group">
                    <div className="flex items-start justify-between mb-4">
                        <div className="bg-blue-50 p-3 rounded-full text-ecusol-primario group-hover:bg-ecusol-primario group-hover:text-white transition-colors">
                            <MapPin size={24} />
                        </div>
                        <a 
                            href={`https://www.google.com/maps/search/?api=1&query=${sucursal.lat},${sucursal.lng}`}
                            target="_blank"
                            rel="noreferrer"
                            className="text-xs font-bold text-ecusol-secundario flex items-center gap-1 hover:underline"
                        >
                            VER EN MAPA <Navigation size={12}/>
                        </a>
                    </div>
                    <h3 className="text-xl font-bold text-gray-800 mb-2">{sucursal.nombre}</h3>
                    <p className="text-gray-600 text-sm mb-4">{sucursal.direccion}</p>
                    <div className="flex items-center gap-2 text-gray-500 text-sm pt-4 border-t border-gray-50">
                        <Phone size={16} />
                        <span>{sucursal.telefono}</span>
                    </div>
                </Tarjeta>
            ))}
        </div>
      </div>
    </div>
  );
};

export default PaginaUbicanos;