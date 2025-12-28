import { FooterApp } from '@/components/layout/FooterApp';
import { HeaderApp } from '@/components/layout/HeaderApp';
import { Outlet } from 'react-router-dom';

const AppLayout = () => {
  return (
    <div className="flex flex-col min-h-screen">
      <HeaderApp />
      <main className="flex-grow container mx-auto px-6 py-12">
        <Outlet />
      </main>
      <FooterApp />
    </div>
  );
};

export default AppLayout;