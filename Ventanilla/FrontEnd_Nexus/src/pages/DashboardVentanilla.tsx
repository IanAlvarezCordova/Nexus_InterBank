//ubicacion: src/pages/DashboardVentanilla.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useVentanillaStore } from '../store/useVentanillaStore';
import { ventanillaService } from '../services/ventanillaService';
import { ResumenClienteDTO, CuentaResumenDTO } from '../types';
import { toast } from 'react-hot-toast';
import { formatCurrency } from '../utils/formatters'; 
import { 
  LogOut, Search, Wallet, ArrowRightLeft, CheckCircle2, Loader2, Ban, ShieldCheck, Unlock, Power
} from 'lucide-react';

const DashboardVentanilla = () => {
  const navigate = useNavigate();
  const { usuarioEmpleado, nombreSucursal, logout } = useVentanillaStore();

  const [busqueda, setBusqueda] = useState('');
  const [loadingBusqueda, setLoadingBusqueda] = useState(false);
  const [resumenCliente, setResumenCliente] = useState<ResumenClienteDTO | null>(null);
  const [cuentaActiva, setCuentaActiva] = useState<CuentaResumenDTO | null>(null);

  const [tab, setTab] = useState<'DEPOSITO' | 'RETIRO' | 'TRANSFERENCIA'>('DEPOSITO');
  const [monto, setMonto] = useState('');
  const [destino, setDestino] = useState(''); 
  const [nombreDestino, setNombreDestino] = useState(''); 
  const [descripcion, setDescripcion] = useState('');
  
  const [procesando, setProcesando] = useState(false);
  const [validandoDestino, setValidandoDestino] = useState(false);

  const esActivo = (estado: string) => {
      if (!estado) return false;
      const e = estado.toUpperCase();
      return e.includes('ACTIV') && !e.includes('INACTIV'); 
  };

  const handleLogout = () => { logout(); navigate('/'); };

  const handleBuscar = async () => {
    if (!busqueda) return;
    setLoadingBusqueda(true);
    setResumenCliente(null);
    setCuentaActiva(null);
    try {
      const data = await ventanillaService.buscarClientePorCedula(busqueda);
      setResumenCliente(data);
      toast.success("Cliente encontrado");
    } catch (error: any) {
      toast.error("Cliente no encontrado");
    } finally {
      setLoadingBusqueda(false);
    }
  };

  const refrescarTodo = async () => {
      if(!resumenCliente) return;
      try {
          const data = await ventanillaService.buscarClientePorCedula(resumenCliente.cedula);
          setResumenCliente(data);
          if (cuentaActiva) {
              const actualizada = data.cuentas.find(c => c.numeroCuenta === cuentaActiva.numeroCuenta);
              if (actualizada) setCuentaActiva(actualizada);
          }
      } catch(e) { console.error(e); }
  };

  // --- FUNCIONES ADMINISTRATIVAS ---

  const handleEstadoCliente = async (nuevoEstado: 'ACTIVO' | 'INACTIVO') => {
      if(!resumenCliente) return;
      const accion = nuevoEstado === 'ACTIVO' ? 'REACTIVAR' : 'INACTIVAR';
      if(!confirm(`¿Confirma que desea ${accion} al cliente?`)) return;

      setProcesando(true);
      try {
          await ventanillaService.cambiarEstadoCliente(resumenCliente.cedula, nuevoEstado);
          toast.success(`Cliente actualizado`);
          await refrescarTodo();
      } catch(e:any) { toast.error("Error al cambiar estado"); } finally { setProcesando(false); }
  };

  const toggleEstadoCuenta = async () => {
      if (!cuentaActiva) return;
      const estaActiva = esActivo(cuentaActiva.estado);
      const nuevoEstado = estaActiva ? 'INACTIVA' : 'ACTIVA';
      if(!confirm(`¿Cambiar estado a ${nuevoEstado}?`)) return;
      
      setProcesando(true);
      try {
          await ventanillaService.cambiarEstadoCuenta(cuentaActiva.numeroCuenta, nuevoEstado);
          toast.success(`Cuenta actualizada`);
          await refrescarTodo();
      } catch(e:any) { toast.error(e.message); } finally { setProcesando(false); }
  };

  const activarCuenta = async () => {
      if (!cuentaActiva) return;
      if(!confirm("¿Confirma activación?")) return;
      setProcesando(true);
      try {
          await ventanillaService.activarCuenta(cuentaActiva.numeroCuenta);
          toast.success("Cuenta Activada");
          await refrescarTodo();
      } catch(e:any) { toast.error(e.message || "Error al activar"); } finally { setProcesando(false); }
  };

  const handleEliminarCuenta = async () => {
      if (!cuentaActiva) return;
      if(!confirm(`ADVERTENCIA: ¿ELIMINAR cuenta ${cuentaActiva.numeroCuenta}?`)) return;
      setProcesando(true);
      try {
          await ventanillaService.eliminarCuenta(cuentaActiva.numeroCuenta);
          toast.success(`Cuenta eliminada.`);
          setCuentaActiva(null);
          await refrescarTodo();
      } catch(e:any) { toast.error("Error al eliminar"); } finally { setProcesando(false); }
  };

  // --- OPERACIONES DE CAJA ---

  const validarDestinoTerceros = async () => {
      if (!destino || destino.length < 8) return;
      setValidandoDestino(true);
      try {
          const info = await ventanillaService.validarDestino(destino);
          setNombreDestino(info.nombreCompleto); 
          toast.success("Destinatario: " + info.nombreCompleto);
      } catch (e) {
          toast.error("Cuenta no existe");
          setNombreDestino('');
      } finally { setValidandoDestino(false); }
  };

  const handleOperar = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!cuentaActiva) return;
    
    if (!esActivo(resumenCliente?.estado || '')) {
        toast.error("CLIENTE INACTIVO. No puede operar.");
        return;
    }

    let cuentaDestinoFinal = undefined;
    if (tab === 'TRANSFERENCIA') {
        if (!nombreDestino) { toast.error("Valide el destinatario"); return; }
        cuentaDestinoFinal = destino;
    }

    setProcesando(true);
    try {
      await ventanillaService.operar(tab, {
        numeroCuentaOrigen: cuentaActiva.numeroCuenta,
        numeroCuentaDestino: cuentaDestinoFinal,
        monto: parseFloat(monto),
        descripcion: descripcion || `Ventanilla: ${tab}`
      });
      toast.success("Transacción Exitosa");
      setMonto(''); setDestino(''); setNombreDestino(''); setDescripcion('');
      await refrescarTodo();
    } catch (error: any) { toast.error(error.message || "Error"); } finally { setProcesando(false); }
  };

  const clienteInactivo = !esActivo(resumenCliente?.estado || '');
  const cuentaEsActiva = esActivo(cuentaActiva?.estado || '');

  return (
    <div className="min-h-screen bg-gray-100 font-sans text-gray-800 pb-20">
      <header className="bg-white border-b border-gray-200 px-6 h-16 flex justify-between items-center sticky top-0 z-30 shadow-sm">
         <div className="flex items-center gap-3">
             <div className="bg-nexus-primario text-white text-xs font-bold px-2 py-1 rounded">NEXUS</div>
             <span className="font-bold text-gray-700 hidden sm:inline">Ventanilla: {nombreSucursal}</span>
         </div>
         <div className="flex items-center gap-4">
             <span className="text-sm font-bold text-nexus-primario uppercase">{usuarioEmpleado}</span>
             <button onClick={handleLogout} className="p-2 hover:bg-red-50 rounded-full text-gray-500 hover:text-red-600 transition-colors"><LogOut size={18}/></button>
         </div>
      </header>

      <main className="max-w-7xl mx-auto p-6 space-y-6">
        {/* Buscador */}
        <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-200 flex gap-4">
            <div className="flex-1 relative">
                <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
                <input 
                    type="text" 
                    placeholder="Buscar por Cédula..." 
                    className="w-full pl-12 pr-4 py-3 border rounded-lg focus:ring-2 focus:ring-nexus-primario outline-none" 
                    value={busqueda} 
                    onChange={e => setBusqueda(e.target.value)} 
                    onKeyDown={e => e.key === 'Enter' && handleBuscar()}
                    autoFocus 
                />
            </div>
            <button onClick={handleBuscar} disabled={loadingBusqueda || !busqueda} className="bg-nexus-primario text-white px-6 rounded-lg font-bold hover:bg-green-900 disabled:opacity-50 min-w-[100px] flex justify-center items-center">
                {loadingBusqueda ? <Loader2 className="animate-spin"/> : 'Buscar'}
            </button>
            {resumenCliente && <button onClick={() => {setResumenCliente(null); setCuentaActiva(null); setBusqueda('');}} className="text-red-600 font-medium hover:underline px-2">Limpiar</button>}
        </div>

        {resumenCliente && (
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-fade-in-up">
                
                {/* INFO CLIENTE */}
                <div className="lg:col-span-1 space-y-6">
                    <div className={`p-6 rounded-xl shadow-sm border transition-colors ${clienteInactivo ? 'bg-red-50 border-red-200' : 'bg-white border-gray-200'}`}>
                        <div className="flex justify-between items-start mb-4">
                            <div>
                                <h2 className="font-bold text-lg text-gray-800">{resumenCliente.nombres}</h2>
                                <p className="text-sm text-gray-500 mb-2">{resumenCliente.cedula}</p>
                                <span className={`text-xs font-bold px-2 py-1 rounded flex items-center gap-1 w-max ${clienteInactivo ? 'bg-red-200 text-red-800' : 'bg-green-100 text-green-700'}`}>
                                    {clienteInactivo ? <Ban size={12}/> : <CheckCircle2 size={12}/>} {resumenCliente.estado}
                                </span>
                            </div>
                            <div className="w-12 h-12 bg-nexus-secundario text-white rounded-full flex items-center justify-center font-bold text-xl">
                                {resumenCliente.nombres.charAt(0)}
                            </div>
                        </div>

                        <div className="border-t pt-4 mt-2">
                            <button onClick={() => handleEstadoCliente(clienteInactivo ? 'ACTIVO' : 'INACTIVO')} className={`w-full py-2 rounded-lg text-xs font-bold flex items-center justify-center gap-2 shadow-sm transition-all ${clienteInactivo ? 'bg-green-600 text-white hover:bg-green-700' : 'bg-white border border-red-200 text-red-600 hover:bg-red-50'}`}>
                                {clienteInactivo ? <><Unlock size={14}/> REACTIVAR CLIENTE</> : <><Ban size={14}/> INACTIVAR CLIENTE</>}
                            </button>
                        </div>
                    </div>

                    {/* LISTA DE PRODUCTOS */}
                    <div className="space-y-3">
                        <h3 className="text-xs font-bold text-gray-400 uppercase px-1">Productos del Cliente</h3>
                        {resumenCliente.cuentas.map((cuenta) => {
                             const estaCuentaActiva = esActivo(cuenta.estado);
                             return (
                                <div 
                                    key={cuenta.numeroCuenta}
                                    onClick={() => setCuentaActiva(cuenta)}
                                    className={`p-4 rounded-xl border-2 cursor-pointer transition-all relative overflow-hidden
                                        ${cuentaActiva?.numeroCuenta === cuenta.numeroCuenta ? 'border-nexus-primario bg-green-50 ring-1 ring-nexus-primario' : 'border-gray-200 bg-white hover:border-green-300'}
                                        ${!estaCuentaActiva ? 'opacity-70 bg-gray-100' : ''}
                                    `}
                                >
                                    <div className="flex justify-between items-center mb-2">
                                        <span className="text-xs font-bold uppercase text-gray-500">{cuenta.tipo}</span>
                                        {!estaCuentaActiva && <span className="bg-red-100 text-red-800 text-[10px] px-2 py-0.5 rounded font-bold">{cuenta.estado}</span>}
                                    </div>
                                    <p className="font-mono text-lg font-bold text-gray-800">**** {cuenta.numeroCuenta.slice(-4)}</p>
                                    <p className="text-xl font-bold text-nexus-primario mt-1">{formatCurrency(cuenta.saldo)}</p>
                                </div>
                             );
                        })}
                    </div>
                </div>

                {/* OPERACIONES */}
                <div className="lg:col-span-2">
                    {clienteInactivo ? (
                        <div className="h-full flex flex-col items-center justify-center bg-red-50 rounded-2xl border-2 border-dashed border-red-200 p-10 text-center text-red-400">
                            <Ban size={64} className="mb-4"/>
                            <h3 className="text-xl font-bold text-red-700">Cliente Inactivo</h3>
                            <p>Las operaciones están deshabilitadas.</p>
                        </div>
                    ) : cuentaActiva ? (
                        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden h-full flex flex-col">
                            <div className="bg-gray-50 border-b p-4 flex justify-between items-center">
                                <span className="font-bold text-gray-700 flex items-center gap-2"><Wallet size={18} className="text-nexus-primario"/> Operando: {cuentaActiva.numeroCuenta}</span>
                                <div className='flex gap-2 items-center'>
                                    <button onClick={handleEliminarCuenta} disabled={procesando} className="text-xs font-bold px-3 py-1 rounded flex items-center gap-1 transition-colors text-red-700 hover:bg-red-100 disabled:opacity-50">
                                        <Ban size={14}/> Eliminar
                                    </button>
                                    {/* CORREGIDO: Usamos cuentaEsActiva en lugar de accountActive */}
                                    <button onClick={cuentaEsActiva ? toggleEstadoCuenta : activarCuenta} disabled={procesando} className={`text-xs font-bold px-3 py-1 rounded flex items-center gap-1 transition-colors ${cuentaEsActiva ? 'text-red-500 hover:bg-red-50' : 'text-green-600 hover:bg-green-50'}`}>
                                        <Power size={14}/> {cuentaEsActiva ? 'Inactivar' : 'Activar'}
                                    </button>
                                </div>
                            </div>
                            
                            {!cuentaEsActiva ? (
                                <div className="flex-1 flex flex-col items-center justify-center p-10 text-center">
                                    <Ban size={48} className="text-gray-300 mb-4"/>
                                    <h3 className="text-xl font-bold text-gray-800">Cuenta Inactiva</h3>
                                    <p className="text-gray-500 mb-6 max-w-sm">No se pueden realizar transacciones sobre esta cuenta.</p>
                                    <button onClick={activarCuenta} disabled={procesando} className="bg-nexus-primario text-white px-6 py-3 rounded-xl font-bold hover:bg-green-900 flex items-center gap-2 shadow-lg">
                                        {procesando ? <Loader2 className="animate-spin"/> : <><Unlock size={18}/> Activar Ahora</>}
                                    </button>
                                </div>
                            ) : (
                                <>
                                    <div className="flex border-b">
                                        {['DEPOSITO', 'RETIRO', 'TRANSFERENCIA'].map((t) => (
                                            <button key={t} onClick={() => setTab(t as any)} className={`flex-1 py-4 font-bold text-sm border-b-4 transition-all ${tab === t ? 'border-nexus-primario text-nexus-primario bg-green-50/30' : 'border-transparent text-gray-400 hover:bg-gray-50'}`}>{t}</button>
                                        ))}
                                    </div>
                                    <div className="p-8 flex-1">
                                        <form onSubmit={handleOperar} className="max-w-md mx-auto space-y-6">
                                            {tab === 'TRANSFERENCIA' && (
                                                <div className="bg-blue-50 p-4 rounded-xl border border-blue-100">
                                                    <label className="block text-xs font-bold text-blue-700 uppercase mb-2">Cuenta Destino</label>
                                                    <div className="flex gap-2">
                                                        <input type="text" className="w-full p-3 border border-blue-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none font-mono" placeholder="Ingrese cuenta..." value={destino} onChange={e => { setDestino(e.target.value); setNombreDestino(''); }}/>
                                                        <button type="button" onClick={validarDestinoTerceros} disabled={validandoDestino || !destino} className="bg-blue-600 text-white px-4 rounded-lg hover:bg-blue-700 disabled:opacity-50"><ShieldCheck size={18}/></button>
                                                    </div>
                                                    {nombreDestino && <div className="mt-2 text-sm text-green-700 font-bold">✓ {nombreDestino}</div>}
                                                </div>
                                            )}
                                            <div>
                                                <label className="block text-xs font-bold text-gray-500 uppercase mb-2">Monto</label>
                                                <input type="number" step="0.01" className="w-full pl-4 p-4 text-3xl font-bold border-2 border-gray-200 rounded-xl outline-none focus:border-nexus-primario" placeholder="0.00" value={monto} onChange={e => setMonto(e.target.value)} required />
                                            </div>
                                            <input type="text" className="w-full p-3 border-2 border-gray-200 rounded-xl outline-none focus:border-nexus-primario" placeholder="Concepto..." value={descripcion} onChange={e => setDescripcion(e.target.value)} />
                                            <button type="submit" disabled={procesando} className="w-full py-4 bg-nexus-primario text-white rounded-xl font-bold shadow-lg hover:bg-green-900 transition-all flex justify-center items-center gap-2 disabled:opacity-50">
                                                {procesando ? <Loader2 className="animate-spin"/> : 'CONFIRMAR OPERACIÓN'}
                                            </button>
                                        </form>
                                    </div>
                                </>
                            )}
                        </div>
                    ) : (
                        <div className="h-full flex flex-col items-center justify-center bg-white rounded-2xl border-2 border-dashed border-gray-200 opacity-50">
                            <ArrowRightLeft size={48} className="text-gray-300 mb-2"/>
                            <p className="text-gray-400 font-bold">Seleccione un producto para operar</p>
                        </div>
                    )}
                </div>
            </div> 
        )}
      </main> 
    </div>
  );
};

export default DashboardVentanilla;