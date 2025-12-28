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
          primario: '#1A5D3B',    // Verde Oscuro Nexus
          secundario: '#D4AF37',  // Dorado Nexus
          terciario: '#E8B923',   // Amarillo Dorado
          fondo: '#F4F6F8',       // Gris suave para fondos
          texto: '#1F2937',       // Gris oscuro para lectura
          rojo: '#DC2626',
          verde: '#16A34A',
          'gris-claro': '#F3F4F6', // Gris muy suave para fondos
          'gris-oscuro': '#374151' // Gris oscuro para textos secundarios
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      }
    },
  },
  plugins: [],
}