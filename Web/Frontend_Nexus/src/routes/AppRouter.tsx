import React, { lazy, Suspense } from 'react';
import { Route, Routes } from 'react-router-dom';
import AppLayout from '@/layouts/AppLayout';
import PublicLayout from '@/layouts/PublicLayout';
import RutaProtegida from './RutaProtegida';
import { Loader2 } from 'lucide-react';

const PaginaDashboard = lazy(() => import('@/pages/dashboard/PaginaDashboard'));
const PaginaLogin = lazy(() => import('@/pages/auth/PaginaLogin'));
const PaginaRegistro = lazy(() => import('@/pages/auth/PaginaRegistro'));
const PaginaPrincipal = lazy(() => import('@/pages/public/PaginaPrincipal'));
const PaginaAcercaDe = lazy(() => import('@/pages/public/PaginaAcercaDe'));
const PaginaTransferencia = lazy(() => import('@/pages/transfers/PaginaTransferencia'));
const PaginaCuentas = lazy(() => import('@/pages/cuentas/PaginaCuentas'));
const PaginaDetalleCuenta = lazy(() => import('@/pages/cuentas/PaginaDetalleCuenta'));
const PaginaUbicanos = lazy(() => import('@/pages/public/PaginaUbicanos'));
const PaginaAyuda = lazy(() => import('@/pages/public/PaginaAyuda'));

const Loader = () => (
  <div className="flex justify-center items-center h-screen text-ecusol-primario">
    <div className="flex flex-col items-center gap-2">
      <Loader2 size={48} className="animate-spin" />
      <p className="font-semibold">Cargando Nexus Bank...</p>
    </div>
  </div>
);

const AppRouter = () => {
  return (
    <Suspense fallback={<Loader />}>
      <Routes>
        
        <Route element={<PublicLayout />}>
          <Route path="/" element={<PaginaPrincipal />} />
          <Route path="/acerca-de" element={<PaginaAcercaDe />} />
          <Route path="/ayuda" element={<PaginaAyuda />} />
          <Route path="/ubicanos" element={<PaginaUbicanos />} />
          <Route path="/login" element={<PaginaLogin />} />
          <Route path="/registro" element={<PaginaRegistro />} />
        </Route>

        
        <Route path="/app" element={<RutaProtegida><AppLayout /></RutaProtegida>}>
          <Route path="dashboard" element={<PaginaDashboard />} />
          <Route path="transferir" element={<PaginaTransferencia />} />          
          <Route path="cuentas" element={<PaginaCuentas />} />
          <Route path="cuentas/:numeroCuenta" element={<PaginaDetalleCuenta />} />
          
          
          <Route path="ubicanos" element={<PaginaUbicanos />} />
          <Route path="ayuda" element={<PaginaAyuda />} />
          <Route path="acerca-de" element={<PaginaAcercaDe />} />
        </Route>
        
        <Route path="*" element={<div className="p-10 text-center">Página no encontrada</div>} />
      </Routes>
    </Suspense>
  );
};

export default AppRouter;