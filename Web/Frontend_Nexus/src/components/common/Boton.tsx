//src/components/common/Boton.tsx
import React from 'react';

type VarianteBoton = 'primario' | 'secundario' | 'peligro';
type TamanoBoton = 'pequeno' | 'mediano' | 'grande';

interface BotonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children: React.ReactNode;
  variante?: VarianteBoton;
  tamano?: TamanoBoton;
  icono?: React.ReactElement;
}

export const Boton: React.FC<BotonProps> = ({ 
  children, 
  variante = 'primario', 
  tamano = 'mediano', 
  icono, 
  className = '', 
  ...props 
}) => {
  
  const estilosBase = 'font-bold rounded-lg transition-all duration-300 flex items-center justify-center gap-2 disabled:opacity-50';

  const estilosVariante = {
    primario: 'bg-ecusol-primario text-white hover:bg-ecusol-primario/90', 
    secundario: 'bg-gray-200 text-ecusol-gris-oscuro hover:bg-gray-300',
    peligro: 'bg-red-600 text-white hover:bg-red-700',
  };

  const estilosTamano = {
    pequeno: 'py-1 px-3 text-sm',
    mediano: 'py-2 px-5 text-base',
    grande: 'py-3 px-7 text-lg',
  };

  return (
    <button
      className={`${estilosBase} ${estilosVariante[variante]} ${estilosTamano[tamano]} ${className}`}
      {...props}
    >
      {icono && React.cloneElement(icono as React.ReactElement<any>, { size: 18 })}
      {children}
    </button>
  );
};