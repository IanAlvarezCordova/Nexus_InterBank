import { useState } from 'react';
import { ChevronDown, ChevronUp, HelpCircle } from 'lucide-react';

const FAQItem = ({ pregunta, respuesta }: { pregunta: string, respuesta: string }) => {
    const [abierto, setAbierto] = useState(false);
    return (
        <div className="border-b border-gray-200">
            <button 
                className="w-full flex justify-between items-center py-4 text-left focus:outline-none"
                onClick={() => setAbierto(!abierto)}
            >
                <span className="font-bold text-gray-700">{pregunta}</span>
                {abierto ? <ChevronUp className="text-ecusol-secundario"/> : <ChevronDown className="text-gray-400"/>}
            </button>
            {abierto && (
                <div className="pb-4 text-gray-600 text-sm animate-fade-in">
                    {respuesta}
                </div>
            )}
        </div>
    );
};

const PaginaAyuda = () => {
  return (
    <div className="bg-gray-50 min-h-screen py-16">
      <div className="container mx-auto px-6 max-w-3xl">
        <div className="text-center mb-12">
            <HelpCircle size={48} className="mx-auto text-ecusol-primario mb-4" />
            <h1 className="text-4xl font-bold text-gray-800 mb-4">Centro de Ayuda</h1>
            <p className="text-gray-600">Preguntas frecuentes sobre nuestros servicios.</p>
        </div>

        <div className="bg-white rounded-2xl shadow-sm p-8">
            <FAQItem 
                pregunta="¿Cómo desbloqueo mi usuario?" 
                respuesta="Si bloqueaste tu usuario por intentos fallidos, debes esperar 30 minutos o acercarte a una ventanilla para el desbloqueo inmediato." 
            />
            <FAQItem 
                pregunta="¿Cuál es el monto máximo de transferencia?" 
                respuesta="El límite diario para transferencias web es de $5,000. Para montos superiores, acércate a una agencia." 
            />
            <FAQItem 
                pregunta="¿Cómo obtengo mi estado de cuenta?" 
                respuesta="Ingresa a la sección 'Mis Cuentas', selecciona tu cuenta y haz clic en el botón 'Historial'. Allí podrás descargar tus movimientos." 
            />
            <FAQItem 
                pregunta="¿Tienen costo las transferencias interbancarias?" 
                respuesta="Las transferencias directas tienen un costo de $0.40. Las transferencias dentro de EcuSol son gratuitas." 
            />
        </div>
      </div>
    </div>
  );
};

export default PaginaAyuda;