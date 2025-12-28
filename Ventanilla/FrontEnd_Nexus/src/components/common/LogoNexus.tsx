// src/components/common/LogoNexus.tsx
import React from 'react';
import logoNexus from '@/assets/logo.jpeg'; 

interface LogoNexusProps {
  className?: string;
  size?: number; // Tamaño del logo circular
}

export const LogoNexus: React.FC<LogoNexusProps> = ({ className = '', size = 50 }) => {
  return (
    <img 
      src={logoNexus} 
      alt="Logo Nexus Bank" 
      className={`rounded-full object-cover ${className}`} 
      style={{ 
        width: `${size}px`, 
        height: `${size}px` 
      }} 
    />
  );
};