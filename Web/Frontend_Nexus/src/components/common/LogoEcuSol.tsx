// src/components/common/LogoEcuSol.tsx
import React from 'react';
import logoEcuSol from '@/assets/logo.jpg'; 

interface LogoEcuSolProps {
  className?: string;
  size?: number; // Cambiamos 'width' por 'size' para un círculo
}

export const LogoEcuSol: React.FC<LogoEcuSolProps> = ({ className = '', size = 50 }) => {
  return (
    <img 
      src={logoEcuSol} 
      alt="Logo Nexus Bank" 
      className={`rounded-full object-cover ${className}`} 
        style={{ 
        width: `${size}px`, 
        height: `${size}px` 
      }} 
    />
  );
};