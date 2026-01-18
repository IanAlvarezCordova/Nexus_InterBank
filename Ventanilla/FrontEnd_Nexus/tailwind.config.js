/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        nexus: {
          primario: '#1A5D3B',    
          secundario: '#D4AF37', 
          terciario: '#E8B923',   
          fondo: '#F4F6F8',      
          texto: '#1F2937',       
          rojo: '#DC2626',
          verde: '#16A34A',
          'gris-claro': '#F3F4F6', 
          'gris-oscuro': '#374151' 
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      }
    },
  },
  plugins: [],
}