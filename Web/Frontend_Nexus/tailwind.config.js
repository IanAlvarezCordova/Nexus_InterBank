/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        ecusol: {
          primario: '#0a652c',    
          secundario: '#c7a335',   
          acento: '#F4F7FA',       
          texto: '#1A1A1A',        

          rojo: '#D32F2F',
          verde: '#388E3C',

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
