import { Boton } from '@/components/common/Boton';
import { Tarjeta } from '@/components/common/Tarjeta';
import { CheckCircle, Download, ShieldCheck, UserPlus } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import React from 'react';
const Beneficio: React.FC<{ titulo: string, descripcion: string, icono: React.ReactElement }> = ({ titulo, descripcion, icono }) => (
  <Tarjeta className="text-center">
    <div className="flex justify-center mb-4 text-ecusol-azul">
      {React.cloneElement(icono, { size: 48, strokeWidth: 1.5 })}
    </div>
    <h3 className="text-xl font-semibold mb-2">{titulo}</h3>
    <p className="text-gray-600">{descripcion}</p>
  </Tarjeta>
);

const PaginaPrincipal = () => {
  const navigate = useNavigate();

  return (
    <>
      <section className="bg-white py-24">
        <div className="container mx-auto px-6 text-center">
          <h1 className="text-5xl font-extrabold mb-4">¡Comienza Ahora!</h1>
          <p className="text-xl text-gray-700 max-w-2xl mx-auto mb-8">
            Forma parte del mejor Banco del Ecuador.
            <br />
            Seguro, Rápido y tu mejor amigo.
          </p>
          <div className="flex justify-center gap-4">
            <Boton tamano="grande" onClick={() => navigate('/login')} icono={<UserPlus />}>
              Inicia sesión para acceder
            </Boton>
          </div>
        </div>
      </section>
      <section className="py-20 bg-ecusol-gris-claro">
        <div className="container mx-auto px-6">
          <h2 className="text-3xl font-bold text-center mb-12">Beneficios a los que puedes acceder</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <Beneficio 
              titulo="Cuenta de ahorros"
              descripcion="La opción ideal para ahorrar de manera segura y obtener rendimientos."
              icono={<ShieldCheck />}
            />
            <Beneficio 
              titulo="Cuenta corriente"
              descripcion="Disfruta de una cuenta flexible para tus transacciones diarias."
              icono={<CheckCircle />}
            />
            <Beneficio 
              titulo="Créditos"
              descripcion="Accede a préstamos personales con condiciones cómodas."
              icono={<UserPlus />}
            />
          </div>
        </div>
      </section>
    </>
  );
};

export default PaginaPrincipal;