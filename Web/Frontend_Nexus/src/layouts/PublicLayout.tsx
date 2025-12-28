import { FooterApp } from '@/components/layout/FooterApp';
import { HeaderPublico } from '@/components/layout/HeaderPublico';
import { Outlet } from 'react-router-dom';

const PublicLayout = () => {
  return (
    <div className="flex flex-col min-h-screen">
      <HeaderPublico />
      <main className="flex-grow">
        <Outlet />
      </main>
      <FooterApp />
    </div>
  );
};

export default PublicLayout;