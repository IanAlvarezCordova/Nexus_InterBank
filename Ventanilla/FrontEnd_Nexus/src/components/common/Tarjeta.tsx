// src/components/common/Tarjeta.tsx
import React from 'react';

interface TarjetaProps {
  children: React.ReactNode;
  className?: string;
}

export const Tarjeta: React.FC<TarjetaProps> = ({ children, className = '' }) => {
  return (
    <div className={`bg-white rounded-xl shadow-md p-6 ${className}`}>
      {children}
    </div>
  );
};