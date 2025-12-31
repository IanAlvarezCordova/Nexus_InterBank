import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { bancaService, Beneficiario } from '@/services/bancaService';
import { CuentaDTO, DestinatarioDTO } from '@/types';
import { Boton } from '@/components/common/Boton';
import { formatCurrency } from '@/utils/formatters';
import { CheckCircle2, User, Users, Loader2, AlertTriangle, Wallet, ArrowRight, Star, Save, X, ChevronRight, UserCircle, BookUser } from 'lucide-react';
import { toast } from 'react-hot-toast';

const BANCOS_TERCEROS = [
  { id: 'ECUASOL', nombre: 'Nexus (Propio)', color: 'bg-blue-600' },
  { id: 'BANTEC', nombre: 'BanTec', color: 'bg-purple-500' },
  { id: 'ARCBANK', nombre: 'ArcBank', color: 'bg-orange-500' },
  { id: 'ECUSOL_BK', nombre: 'EcuSol', color: 'bg-green-500' }
];

const PaginaTransferencia = () => {
  const navigate = useNavigate();

  const [misCuentas, setMisCuentas] = useState<CuentaDTO[]>([]);
  const [beneficiarios, setBeneficiarios] = useState<Beneficiario[]>([]);
  const [loading, setLoading] = useState(true);

  // Modos: 'ECUASOL' (antes PROPIAS) | 'INTERBANCARIA' (antes TERCEROS)
  const [modo, setModo] = useState<'ECUASOL' | 'INTERBANCARIA'>('ECUASOL');

  // Nuevo: Subtipo para transferencias internas
  // Default a TERCERO para que no salgan "mis cuentas" de una
  const [tipoInterno, setTipoInterno] = useState<'PROPIA' | 'TERCERO'>('TERCERO');

  const [banco, setBanco] = useState<string>('ECUASOL');
  const [origen, setOrigen] = useState('');

  // Destinos
  const [destinoManual, setDestinoManual] = useState(''); // Para escribir cuenta
  const [destinoPropio, setDestinoPropio] = useState(''); // Para seleccionar de mis cuentas

  const [monto, setMonto] = useState('');
  const [descripcion, setDescripcion] = useState('');

  const [destinatarioData, setDestinatarioData] = useState<DestinatarioDTO | null>(null);
  const [validando, setValidando] = useState(false);

  const [showSaveModal, setShowSaveModal] = useState(false);
  const [aliasParaGuardar, setAliasParaGuardar] = useState('');
  const [guardandoBen, setGuardandoBen] = useState(false);

  const [showModal, setShowModal] = useState(false);
  const [procesandoTx, setProcesandoTx] = useState(false);
  const [txExitosa, setTxExitosa] = useState(false);
  const [segundos, setSegundos] = useState(30);
  const timerRef = useRef<any>(null);

  useEffect(() => {
    const cargar = async () => {
      try {
        const [datosCuentas, datosBeneficiarios] = await Promise.all([
          bancaService.getMisCuentas(),
          bancaService.getBeneficiarios()
        ]);

        const activas = datosCuentas.filter(c => c.estado && c.estado.toUpperCase().startsWith('ACTIV'));
        setMisCuentas(activas);

        if (activas.length > 0) {
          const inicial = activas[0].numeroCuenta;
          setOrigen(inicial);
          const disponibles = activas.filter(c => c.numeroCuenta !== inicial);
          if (disponibles.length > 0) setDestinoPropio(disponibles[0].numeroCuenta);
        }

        setBeneficiarios(datosBeneficiarios);

      } catch (error) { console.error(error); } finally { setLoading(false); }
    };
    cargar();
  }, []);

  const handleValidar = async (cuentaAValidar: string = destinoManual) => {
    if (!cuentaAValidar || cuentaAValidar.length < 5) return;
    setValidando(true);
    setDestinatarioData(null);
    try {
      // Si es EcuSol, el banco es ECUASOL implicito (Nexus en UI)
      const bancoDestino = modo === 'ECUASOL' ? 'ECUASOL' : banco;
      const data = await bancaService.validarDestinatario(cuentaAValidar, bancoDestino);
      setDestinatarioData(data);
      if (cuentaAValidar !== destinoManual) setDestinoManual(cuentaAValidar);
      toast.success("Cuenta verificada: " + data.nombreTitular);
    } catch (error: any) {
      const msg = error.message || "Error";
      if (msg.includes("inactiva")) toast.error("Cuenta destino INACTIVA");
      else toast.error("Cuenta no encontrada");
    } finally {
      setValidando(false);
    }
  };

  const abrirModalGuardar = () => {
    if (!destinatarioData) return;
    setAliasParaGuardar(destinatarioData.nombreTitular.split(' ')[0]);
    setShowSaveModal(true);
  };

  const confirmarGuardarBeneficiario = async () => {
    if (!aliasParaGuardar) { toast.error("Escribe un alias"); return; }
    setGuardandoBen(true);
    try {
      const nuevoBen: Beneficiario = {
        numeroCuenta: destinoManual,
        nombreTitular: destinatarioData!.nombreTitular,
        alias: aliasParaGuardar,
        email: '',
        tipoCuenta: destinatarioData!.tipoCuenta || 'Cuenta Nexus'
      };
      await bancaService.guardarBeneficiario(nuevoBen);
      toast.success("Contacto guardado exitosamente");
      const listaActualizada = await bancaService.getBeneficiarios();
      setBeneficiarios(listaActualizada);
      setShowSaveModal(false);
    } catch (e) {
      toast.error("Error al guardar contacto");
    } finally {
      setGuardandoBen(false);
    }
  };

  const seleccionarBeneficiario = (ben: Beneficiario) => {
    setDestinoManual(ben.numeroCuenta);
    handleValidar(ben.numeroCuenta);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handlePreSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!monto || parseFloat(monto) <= 0) { toast.error("Monto inválido"); return; }

    // Validaciones dependiendo del modo
    if (modo === 'ECUASOL') {
      if (tipoInterno === 'TERCERO' && !destinatarioData) { toast.error("Valida la cuenta primero"); return; }
      if (tipoInterno === 'PROPIA' && origen === destinoPropio) { toast.error("Cuentas iguales"); return; }
    } else {
      // MODO INTERBANCARIO: No requerimos validación estricta de cuenta (nombre titular)
      // porque no tenemos acceso a la base de clientes de otros bancos.
      // Solo aseguramos que se haya escrito una cuenta.
      if (!destinoManual || destinoManual.length < 5) {
        toast.error("Ingresa una cuenta válida");
        return;
      }
    }

    const cOrigen = misCuentas.find(c => c.numeroCuenta === origen);
    if (cOrigen && parseFloat(monto) > cOrigen.saldo) { toast.error("Saldo insuficiente"); return; }

    setSegundos(30);
    setShowModal(true);
    setTxExitosa(false);
  };


  useEffect(() => {
    if (showModal && !txExitosa && segundos > 0) {
      timerRef.current = setTimeout(() => setSegundos(s => s - 1), 1000);
    } else if (segundos === 0 && showModal && !txExitosa) {
      setShowModal(false); toast.error("Tiempo expirado");
    }
    return () => clearTimeout(timerRef.current);
  }, [segundos, showModal, txExitosa]);

  const getColorReloj = () => {
    if (segundos > 15) return 'text-green-500 border-green-500';
    if (segundos > 6) return 'text-yellow-500 border-yellow-500';
    return 'text-red-500 border-red-500 animate-pulse';
  };

  const confirmarTransferencia = async () => {
    setProcesandoTx(true);
    clearTimeout(timerRef.current);
    await new Promise(resolve => setTimeout(resolve, 1500));

    try {
      let finalDestino = '';
      let bancoFinal = undefined;

      if (modo === 'ECUASOL') {
        finalDestino = tipoInterno === 'PROPIA' ? destinoPropio : destinoManual;
        bancoFinal = 'ECUASOL';
      } else {
        finalDestino = destinoManual;
        bancoFinal = banco;
      }

      await bancaService.transferir({
        cuentaOrigen: origen,
        cuentaDestino: finalDestino,
        monto: parseFloat(monto),
        descripcion: descripcion || "Transferencia Web",
        bancoDestino: bancoFinal
      });
      setTxExitosa(true);
      const data = await bancaService.getMisCuentas();
      setMisCuentas(data.filter(c => c.estado === 'ACTIVA'));
    } catch (error: any) {
      toast.error(error.message);
      setShowModal(false);
    } finally {
      setProcesandoTx(false);
    }
  };

  const cerrarModalYLimpiar = () => {
    setShowModal(false);
    if (txExitosa) {
      setMonto(''); setDescripcion(''); setDestinoManual(''); setDestinatarioData(null); setTxExitosa(false);
    }
  };


  const SelectorCuenta = ({ valor, onChange, opciones, label }: any) => (
    <div className="space-y-2">
      <label className="block text-sm font-bold text-gray-700">{label}</label>
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
        {opciones.map((c: CuentaDTO) => (
          <button key={c.cuentaId} type="button" onClick={() => onChange(c.numeroCuenta)}
            className={`text-left p-4 rounded-xl border-2 transition-all flex items-center justify-between ${valor === c.numeroCuenta ? 'border-ecusol-primario bg-blue-50 ring-1 ring-ecusol-primario' : 'border-gray-200 hover:border-blue-300 bg-white'}`}>
            <div>
              <p className={`text-xs font-bold uppercase ${valor === c.numeroCuenta ? 'text-ecusol-primario' : 'text-gray-500'}`}>{c.tipoCuentaId === 1 ? 'Ahorros' : 'Corriente'}</p>
              <p className="font-mono text-sm">**** {c.numeroCuenta.slice(-4)}</p>
            </div>
            <div className="text-right">
              <p className={`font-bold ${valor === c.numeroCuenta ? 'text-ecusol-primario' : 'text-gray-700'}`}>{formatCurrency(c.saldo)}</p>
            </div>
            {valor === c.numeroCuenta && <CheckCircle2 size={18} className="text-ecusol-primario absolute top-2 right-2" />}
          </button>
        ))}
      </div>
    </div>
  );

  if (loading) return <div className="flex justify-center p-10"><Loader2 className="animate-spin text-ecusol-primario" size={40} /></div>;

  const cuentasDestino = misCuentas.filter(c => c.numeroCuenta !== origen);
  const esBeneficiarioGuardado = beneficiarios.some(b => b.numeroCuenta === destinoManual);

  return (
    <div className="max-w-5xl mx-auto pb-20">
      <h1 className="text-3xl font-bold text-ecusol-primario mb-6">Zona Transaccional</h1>

      {/* Tabs Principales */}
      <div className="bg-white p-1 rounded-xl shadow-sm border border-gray-200 flex mb-8 max-w-lg">
        <button onClick={() => { setModo('ECUASOL'); setBanco('ECUASOL'); setDestinatarioData(null); }} className={`flex-1 py-3 rounded-lg font-bold text-sm flex items-center justify-center gap-2 transition-all ${modo === 'ECUASOL' ? 'bg-ecusol-primario text-white shadow-md' : 'text-gray-500 hover:bg-gray-50'}`}>
          <img src="/logo-small.png" className="w-5 h-5 opacity-80 bg-white rounded-full p-0.5" alt="" /> Cuentas Nexus
        </button>
        <button onClick={() => { setModo('INTERBANCARIA'); setBanco('ECUSOL'); setDestinatarioData(null); }} className={`flex-1 py-3 rounded-lg font-bold text-sm flex items-center justify-center gap-2 transition-all ${modo === 'INTERBANCARIA' ? 'bg-ecusol-primario text-white shadow-md' : 'text-gray-500 hover:bg-gray-50'}`}>
          <Users size={18} /> A Otros Bancos
        </button>
      </div>

      {modo === 'INTERBANCARIA' && (
        <div className="mb-8">
          <label className="block text-sm font-bold text-gray-700 mb-3">Selecciona el Banco Destino</label>
          <select
            value={banco}
            onChange={(e) => { setBanco(e.target.value); setDestinoManual(''); setDestinatarioData(null); }}
            className="w-full p-4 border-2 border-gray-200 rounded-xl focus:border-ecusol-primario focus:ring-2 focus:ring-ecusol-primario outline-none font-bold text-gray-700 bg-white transition-all"
          >
            {BANCOS_TERCEROS.filter(b => b.id !== 'ECUASOL').map(b => (
              <option key={b.id} value={b.id}>
                {b.nombre}
              </option>
            ))}
          </select>
        </div>
      )}


      <div className="bg-white p-8 rounded-2xl shadow-lg border border-gray-100 animate-fade-in mb-10">
        <form onSubmit={handlePreSubmit} className="space-y-8">

          <SelectorCuenta label="Cuenta de Origen" valor={origen} onChange={setOrigen} opciones={misCuentas} />

          <div className="border-t border-gray-100"></div>

          {/* Sección Destino */}
          <div className="space-y-4">
            <div className="flex justify-between items-center mb-2">
              <label className="block text-sm font-bold text-gray-700">Cuenta de Destino</label>

              {modo === 'ECUASOL' && (
                <div className="flex bg-gray-100 rounded-lg p-0.5">
                  <button type="button" onClick={() => setTipoInterno('PROPIA')} className={`px-4 py-1.5 text-xs font-bold rounded-md transition-all ${tipoInterno === 'PROPIA' ? 'bg-white shadow text-ecusol-primario' : 'text-gray-500 hover:bg-gray-200'}`}>Mis Cuentas</button>
                  <button type="button" onClick={() => setTipoInterno('TERCERO')} className={`px-4 py-1.5 text-xs font-bold rounded-md transition-all ${tipoInterno === 'TERCERO' ? 'bg-white shadow text-ecusol-primario' : 'text-gray-500 hover:bg-gray-200'}`}>A otra persona</button>
                </div>
              )}
            </div>

            {modo === 'ECUASOL' && tipoInterno === 'PROPIA' ? (
              cuentasDestino.length === 0 ? (
                <div className="p-4 bg-orange-50 text-orange-700 text-sm rounded-xl border border-orange-100 flex items-center gap-2">
                  <AlertTriangle size={18} /> No tienes otras cuentas activas para transferir.
                </div>
              ) : (
                <SelectorCuenta label="" valor={destinoPropio} onChange={setDestinoPropio} opciones={cuentasDestino} />
              )
            ) : (
              // MANUAL ENTRY (Para Nexus Terceros O Interbancaria)
              <div className="bg-gray-50 p-6 rounded-xl border border-gray-200">
                <div className="mb-3 p-3 bg-white rounded-lg flex items-center gap-2 border border-gray-300">
                  <div className={`px-3 py-1 rounded-full text-white text-xs font-bold ${modo === 'ECUASOL' ? 'bg-blue-600' : (BANCOS_TERCEROS.find(b => b.id === banco)?.color || 'bg-gray-500')}`}>
                    {modo === 'ECUASOL' ? 'Nexus' : (BANCOS_TERCEROS.find(b => b.id === banco)?.nombre || banco)}
                  </div>
                  <span className="text-xs text-gray-500">Banco destino</span>
                </div>
                <div className="flex gap-3">
                  <div className="flex-1 relative">
                    <Wallet className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
                    <input
                      className="w-full pl-12 p-4 border-2 border-gray-200 rounded-xl focus:border-ecusol-primario outline-none font-mono text-lg transition-all"
                      placeholder="Ingrese número de cuenta"
                      value={destinoManual}
                      onChange={e => { setDestinoManual(e.target.value); setDestinatarioData(null) }}
                    />
                  </div>
                  <button type="button" onClick={() => handleValidar()} disabled={validando || !destinoManual} className="bg-ecusol-primario text-white px-6 rounded-xl font-bold hover:bg-blue-900 disabled:opacity-50 shadow-md">
                    {validando ? <Loader2 className="animate-spin" /> : 'Validar'}
                  </button>
                </div>

                {destinatarioData && (
                  <div className="mt-4 p-4 bg-green-100 border border-green-200 rounded-xl flex items-center justify-between gap-4 text-green-800 animate-scale-in">
                    <div className="flex items-center gap-4">
                      <div className="bg-white p-2 rounded-full shadow-sm"><CheckCircle2 size={24} className="text-green-600" /></div>
                      <div>
                        <p className="text-xs font-bold uppercase tracking-wider text-green-700">Destinatario Verificado</p>
                        <p className="font-bold text-lg">{destinatarioData.nombreTitular}</p>
                        <div className="flex gap-2 items-center">
                          <p className="text-sm opacity-80 font-mono">{destinatarioData.cedulaParcial}</p>
                          {destinatarioData.tipoCuenta && (
                            <span className="text-[9px] bg-green-200 px-2 rounded-full font-bold uppercase text-green-800">
                              {destinatarioData.tipoCuenta}
                            </span>
                          )}
                        </div>
                      </div>
                    </div>

                    {!esBeneficiarioGuardado && (
                      <button type="button" onClick={abrirModalGuardar} className="p-2 bg-white rounded-lg hover:bg-green-50 text-green-600 border border-green-200 transition-colors flex flex-col items-center gap-1 min-w-[60px]" title="Guardar en Agenda">
                        <Star size={20} />
                        <span className="text-[10px] font-bold">Guardar</span>
                      </button>
                    )}
                  </div>
                )}
              </div>
            )}
          </div>


          {/* Mostrar siempre monto salvo que sea propia y sin destino */}
          {!(modo === 'ECUASOL' && tipoInterno === 'PROPIA' && cuentasDestino.length === 0) && (
            <div className="bg-blue-50/50 p-6 rounded-xl border border-blue-100 space-y-6 animate-fade-in">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-bold text-gray-700 mb-2">Monto</label>
                  <input type="number" step="0.01" className="w-full p-4 text-2xl font-bold border border-gray-300 rounded-xl focus:ring-2 focus:ring-ecusol-primario outline-none" placeholder="0.00" value={monto} onChange={e => setMonto(e.target.value)} />
                </div>
                <div>
                  <label className="block text-sm font-bold text-gray-700 mb-2">Descripción</label>
                  <input className="w-full p-4 border border-gray-300 rounded-xl focus:ring-2 focus:ring-ecusol-primario outline-none" placeholder="Motivo" value={descripcion} onChange={e => setDescripcion(e.target.value)} />
                </div>
              </div>
              <Boton type="submit" className="w-full py-4 text-lg shadow-lg" disabled={(modo !== 'ECUASOL' || tipoInterno === 'TERCERO') && !destinatarioData}>Continuar con la Transferencia <ArrowRight size={20} /></Boton>
            </div>
          )}
        </form>
      </div>


      {beneficiarios.length > 0 && (
        <div className="animate-slide-up">
          <h3 className="text-xl font-bold text-gray-700 mb-4 flex items-center gap-2 px-2">
            <BookUser className="text-ecusol-primario" size={22} /> Agenda de Contactos
          </h3>
          <div className="bg-white rounded-2xl shadow-md border border-gray-200 overflow-hidden">
            <div className="divide-y divide-gray-100">
              {beneficiarios.map(ben => (
                <button
                  key={ben.id}
                  onClick={() => seleccionarBeneficiario(ben)}
                  className={`w-full text-left p-4 transition-all hover:bg-blue-50 flex items-center gap-4 group
                                ${destinoManual === ben.numeroCuenta ? 'bg-blue-50 border-l-4 border-ecusol-primario' : 'border-l-4 border-transparent'}
                            `}
                >
                  <div className={`w-12 h-12 rounded-full flex items-center justify-center font-bold text-lg shrink-0 shadow-sm transition-colors
                                    ${destinoManual === ben.numeroCuenta ? 'bg-ecusol-primario text-white' : 'bg-gray-100 text-gray-500 group-hover:bg-white group-hover:text-ecusol-primario'}
                            `}>
                    {ben.alias ? ben.alias.substring(0, 2).toUpperCase() : <UserCircle size={24} />}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex justify-between items-center mb-1">
                      <p className="font-bold text-gray-800 text-base">{ben.alias}</p>
                      {ben.tipoCuenta && <span className="text-[10px] text-gray-500 bg-gray-100 border border-gray-200 px-2 py-0.5 rounded-full uppercase font-bold tracking-wide">{ben.tipoCuenta}</span>}
                    </div>
                    <p className="text-sm text-gray-500 truncate">{ben.nombreTitular}</p>
                    <p className="text-xs text-gray-400 font-mono mt-0.5 flex items-center gap-1">
                      <Wallet size={10} /> {ben.numeroCuenta}
                    </p>
                  </div>
                  <div className="text-gray-300 group-hover:text-ecusol-primario group-hover:translate-x-1 transition-all">
                    <ChevronRight size={20} />
                  </div>
                </button>
              ))}
            </div>
          </div>
        </div>
      )}


      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fade-in">
          <div className="bg-white rounded-3xl shadow-2xl w-full max-w-md overflow-hidden relative">
            {!txExitosa && !procesandoTx && (
              <div className="absolute top-4 right-4 z-10">
                <div className={`w-12 h-12 rounded-full border-4 flex items-center justify-center font-bold transition-colors bg-white ${getColorReloj()}`}>{segundos}</div>
              </div>
            )}
            <div className={`p-6 text-center ${txExitosa ? 'bg-green-600' : 'bg-ecusol-primario'} text-white`}>
              <h3 className="text-xl font-bold">{txExitosa ? '¡Transferencia Exitosa!' : 'Confirmar Datos'}</h3>
            </div>
            <div className="p-8 space-y-6">
              {txExitosa ? (
                <div className="text-center space-y-6">
                  <div className="w-24 h-24 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto animate-bounce-in"><CheckCircle2 size={60} /></div>
                  <p className="text-gray-600 text-lg">Tu dinero ha sido enviado correctamente.</p>
                  <Boton onClick={cerrarModalYLimpiar} className="w-full bg-green-600 hover:bg-green-700 py-4 text-lg">Finalizar</Boton>
                </div>
              ) : (
                <>
                  <div className="space-y-4 bg-gray-50 p-5 rounded-xl border border-gray-100">
                    <div className="flex justify-between text-sm"><span className="text-gray-500">Desde</span><span className="font-bold text-gray-800">**** {origen.slice(-4)}</span></div>

                    <div className="flex justify-between text-sm">
                      <span className="text-gray-500">Para</span>
                      <span className="font-bold text-gray-800 text-right">
                        {modo === 'ECUASOL' && tipoInterno === 'PROPIA' ? 'Mi Cuenta Propia' : destinatarioData?.nombreTitular}
                      </span>
                    </div>

                    <div className="flex justify-between text-sm">
                      <span className="text-gray-500">Banco Destino</span>
                      <span className={`font-bold text-white px-3 py-1 rounded-full text-xs ${modo === 'ECUASOL' ? 'bg-blue-600' : 'bg-orange-500'}`}>
                        {modo === 'ECUASOL' ? 'Nexus' : (BANCOS_TERCEROS.find(b => b.id === banco)?.nombre || banco)}
                      </span>
                    </div>

                    <div className="flex justify-between items-center mt-4"><span className="text-gray-500 font-medium">Total</span><span className="font-bold text-3xl text-ecusol-primario">{formatCurrency(parseFloat(monto))}</span></div>
                  </div>
                  <div className="flex gap-3 pt-2">
                    <button onClick={() => setShowModal(false)} className="flex-1 py-3 rounded-xl border font-bold text-gray-600 hover:bg-gray-50">Cancelar</button>
                    <button onClick={confirmarTransferencia} className="flex-1 py-3 rounded-xl bg-ecusol-secundario text-white font-bold hover:bg-yellow-600 shadow-lg flex justify-center items-center gap-2">{procesandoTx ? <Loader2 className="animate-spin" /> : 'Confirmar'}</button>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>
      )}


      {showSaveModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fade-in">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm p-6">
            <div className="flex justify-between items-center mb-4"><h3 className="font-bold text-lg text-gray-800">Guardar Contacto</h3><button onClick={() => setShowSaveModal(false)}><X size={20} className="text-gray-400" /></button></div>
            <div className="mb-4 text-sm text-gray-600 bg-blue-50 p-3 rounded-lg">Guardando a <strong>{destinatarioData?.nombreTitular}</strong></div>
            <div className="space-y-2 mb-6">
              <label className="text-xs font-bold text-gray-500 uppercase">Alias</label>
              <input className="w-full p-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-ecusol-primario outline-none font-bold text-gray-800" value={aliasParaGuardar} onChange={e => setAliasParaGuardar(e.target.value)} placeholder="Ej: Casa, Mamá" autoFocus />
            </div>
            <div className="flex gap-2">
              <button onClick={() => setShowSaveModal(false)} className="flex-1 py-2 rounded-lg text-gray-500 font-bold hover:bg-gray-100">Cancelar</button>
              <button onClick={confirmarGuardarBeneficiario} disabled={guardandoBen || !aliasParaGuardar} className="flex-1 py-2 rounded-lg bg-ecusol-primario text-white font-bold hover:bg-blue-900 flex justify-center items-center gap-2">{guardandoBen ? <Loader2 size={16} className="animate-spin" /> : <Save size={16} />} Guardar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PaginaTransferencia;