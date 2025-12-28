import { useEffect, useState } from 'react';

import { bancaService } from '../../services/bancaService';
import { CuentaDTO } from '../../types';
import { formatCurrency } from '../../utils/formatters';
import { useNavigate } from 'react-router-dom';
import { PlusCircle, Lock, Briefcase, ArrowRight, Loader2, ShieldCheck, CreditCard, AlertCircle } from 'lucide-react';
import { Boton } from '../../components/common/Boton';
import { toast } from 'react-hot-toast';

const PaginaCuentas = () => {
  const [cuentas, setCuentas] = useState<CuentaDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [creando, setCreando] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [tipoCuenta, setTipoCuenta] = useState('1'); 

  const navigate = useNavigate();

  const cargarCuentas = async () => {
    try {
      const data = await bancaService.getMisCuentas();
      setCuentas(data);
    } catch (error) { console.error(error); } finally { setLoading(false); }
  };

  useEffect(() => { cargarCuentas(); }, []);

  const handleConfirmarSolicitud = async () => {
    setCreando(true);
    try {
      await bancaService.solicitarCuenta(parseInt(tipoCuenta));
      toast.success("Solicitud enviada.");
      setShowModal(false);
      await cargarCuentas();
    } catch (error: any) {
      toast.error(error.message || "Error");
    } finally {
      setCreando(false);
    }
  };

  
  const verificarInactividad = (estado: string) => {
      const e = estado.toUpperCase();
      return e === 'INACTIVA' || e === 'INACTIVO' || e === 'BLOQUEADA' || e === 'BLOQUEADO';
  };

  if (loading) return <div className="flex justify-center p-10"><Loader2 className="animate-spin text-ecusol-primario" size={40} /></div>;

  return (
    <div className="space-y-8">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-ecusol-primario">Mis Productos</h1>
        <Boton onClick={() => setShowModal(true)} tamano="pequeno" icono={<PlusCircle size={18}/>} variante="secundario">
           Nueva Cuenta
        </Boton>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-8">
          {cuentas.map((cuenta) => {
            const esInactiva = verificarInactividad(cuenta.estado);
            const esAhorros = cuenta.tipoCuentaId === 1;
            
            let cardStyle = "";
            let textStyle = "";
            let iconColor = "";

            if (esInactiva) {
                cardStyle = "bg-slate-200 border-2 border-slate-300 cursor-not-allowed grayscale opacity-90";
                textStyle = "text-slate-500";
                iconColor = "text-slate-400";
            } else if (esAhorros) {
                cardStyle = "bg-gradient-to-br from-green-800 to-ecusol-primario text-white shadow-green-200 hover:-translate-y-2 hover:shadow-2xl cursor-pointer";
                textStyle = "text-white";
                iconColor = "text-green-200";
            } else {
                cardStyle = "bg-gradient-to-br from-gray-900 to-black text-white shadow-gray-400 hover:-translate-y-2 hover:shadow-2xl cursor-pointer";
                textStyle = "text-white";
                iconColor = "text-gray-400";
            }
            
            return (
              <div 
                key={cuenta.cuentaId} 
                className={`relative rounded-2xl p-6 shadow-lg transition-all duration-300 overflow-hidden h-56 flex flex-col justify-between group ${cardStyle}`}
                onClick={() => !esInactiva && navigate(`/app/cuentas/${cuenta.numeroCuenta}`)}
              >
                
                <div className="absolute right-0 top-0 opacity-10 transform translate-x-8 -translate-y-8 group-hover:scale-110 transition-transform duration-500">
                    {esInactiva ? <Lock size={150} /> : (esAhorros ? <ShieldCheck size={180} /> : <Briefcase size={180} />)}
                </div>

                
                <div className="flex justify-between items-start z-10">
                   <div className={`flex items-center gap-2 opacity-90 ${textStyle}`}>
                      <CreditCard size={20} />
                      <span className="font-bold uppercase text-xs tracking-widest">Nexus Bank</span>
                   </div>
                   
                   
                   <span className={`text-[10px] px-2 py-1 rounded font-bold border uppercase tracking-wide ${
                      esInactiva 
                      ? 'bg-red-100 text-red-600 border-red-200' 
                      : 'bg-white/10 text-white border-white/20 backdrop-blur-sm'
                   }`}>
                      {cuenta.estado}
                   </span>
                </div>

                
                <div className={`w-12 h-9 rounded-md my-2 z-10 shadow-sm flex items-center justify-center opacity-90 ${esInactiva ? 'bg-slate-400' : 'bg-gradient-to-r from-yellow-200 to-yellow-500'}`}>
                    <div className="w-full h-[1px] bg-black/10"></div>
                </div>

                
                <div className="z-10">
                   <p className={`font-mono text-lg tracking-widest mb-1 opacity-90 shadow-black drop-shadow-sm ${textStyle}`}>
                      **** **** **** {cuenta.numeroCuenta.slice(-4)}
                   </p>
                   
                   <div className="flex justify-between items-end">
                      <div>
                          <p className={`text-[10px] uppercase opacity-70 ${textStyle}`}>
                              {esInactiva ? 'Cuenta Bloqueada' : 'Saldo Disponible'}
                          </p>
                          <p className={`text-3xl font-bold ${textStyle}`}>
                              {esInactiva ? '---' : formatCurrency(cuenta.saldo)}
                          </p>
                      </div>
                      {!esInactiva ? (
                          <ArrowRight className={`opacity-0 group-hover:opacity-100 -translate-x-4 group-hover:translate-x-0 transition-all duration-300 ${textStyle}`}/>
                      ) : (
                          <AlertCircle className="text-red-400 opacity-50" />
                      )}
                   </div>
                </div>
              </div>
            );
          })}
      </div>

      
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fade-in">
          <div className="bg-white rounded-3xl shadow-2xl w-full max-w-md overflow-hidden">
            <div className="bg-ecusol-primario p-6 text-white text-center">
              <h3 className="text-xl font-bold">Solicitar Producto</h3>
            </div>
            <div className="p-8 space-y-4">
                <p className="text-gray-600 text-sm text-center mb-4">Se creará una cuenta <strong>INACTIVA</strong> hasta su aprobación.</p>
                <div className="space-y-3">
                    <label className="flex items-center p-4 border rounded-xl cursor-pointer hover:border-ecusol-primario transition-colors">
                        <input type="radio" name="tipo" value="1" checked={tipoCuenta === '1'} onChange={(e) => setTipoCuenta(e.target.value)} className="w-5 h-5 accent-ecusol-primario" />
                        <div className="ml-4"><span className="block font-bold text-gray-800">Cuenta de Ahorros</span></div>
                    </label>
                    <label className="flex items-center p-4 border rounded-xl cursor-pointer hover:border-ecusol-primario transition-colors">
                        <input type="radio" name="tipo" value="2" checked={tipoCuenta === '2'} onChange={(e) => setTipoCuenta(e.target.value)} className="w-5 h-5 accent-ecusol-primario" />
                        <div className="ml-4"><span className="block font-bold text-gray-800">Cuenta Corriente</span></div>
                    </label>
                </div>
                <div className="flex gap-3 mt-6">
                    <button onClick={() => setShowModal(false)} className="flex-1 py-3 rounded-xl border font-bold text-gray-600">Cancelar</button>
                    <button onClick={handleConfirmarSolicitud} disabled={creando} className="flex-1 py-3 rounded-xl bg-ecusol-secundario text-white font-bold hover:bg-yellow-600">
                        {creando ? <Loader2 className="animate-spin mx-auto"/> : 'Solicitar'}
                    </button>
                </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PaginaCuentas;