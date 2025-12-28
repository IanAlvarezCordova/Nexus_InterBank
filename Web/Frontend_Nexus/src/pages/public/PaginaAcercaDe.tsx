import { Tarjeta } from '@/components/common/Tarjeta';
import { Boton } from '@/components/common/Boton';
import { MessageCircle, Phone, Mail } from 'lucide-react';

const StatItem: React.FC<{ valor: string, etiqueta: string }> = ({ valor, etiqueta }) => (
  <div className="text-center">
    <p className="text-4xl font-bold text-ecusol-azul">{valor}</p>
    <p className="text-gray-600">{etiqueta}</p>
  </div>
);

const PaginaAcercaDe = () => {
  return (
    <div className="bg-white py-20">
      <div className="container mx-auto px-6">
        <h1 className="text-4xl font-bold text-center mb-4">Acerca de Nosotros</h1>
        <p className="text-lg text-gray-600 text-center mb-16">
          Conoce al Banco Número 1 en el Ecuador y a nuestras estadísticas.
        </p>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-8 mb-20">
          <StatItem valor="2.5M+" etiqueta="Usuarios Activos" />
          <StatItem valor="$500M+" etiqueta="Transacciones Diarias" />
          <StatItem valor="4.8/5" etiqueta="Ranking en la Play Store" />
          <StatItem valor="24/7" etiqueta="Soporte a Usuario" />
        </div>
        <Tarjeta className="max-w-4xl mx-auto bg-ecusol-gris-claro">
          <h2 className="text-3xl font-bold text-center mb-4">CONTACTO DIRECTO</h2>
          <p className="text-center text-gray-600 mb-12">
            Contáctanos para poder ayudarte a resolver cualquier duda o problema que tengas.
          </p>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 text-center">
            <div>
              <Phone size={36} className="mx-auto text-ecusol-azul mb-3" />
              <h4 className="font-semibold text-lg mb-2">Llámanos</h4>
              <p className="text-gray-700">0995161310</p>
              <p className="text-gray-700">0995161210</p>
            </div>
            <div>
              <Mail size={36} className="mx-auto text-ecusol-azul mb-3" />
              <h4 className="font-semibold text-lg mb-2">Envíanos un correo</h4>
              <p className="text-gray-700">correo@nexus.com</p>
              <Boton className="mt-3" variante="secundario" tamano="pequeno">
                Direccionar
              </Boton>
            </div>
            <div>
              <MessageCircle size={36} className="mx-auto text-ecusol-azul mb-3" />
              <h4 className="font-semibold text-lg mb-2">Chat bot inteligente</h4>
              <p className="text-gray-700">Chatea con nuestro bot</p>
              <Boton className="mt-3" tamano="pequeno">
                Hazlo
              </Boton>
            </div>
          </div>
        </Tarjeta>
      </div>
    </div>
  );
};

export default PaginaAcercaDe;