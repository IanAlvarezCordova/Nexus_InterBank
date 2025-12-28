import AppRouter from '@/routes/AppRouter'
import { Toaster } from 'react-hot-toast'; 

function App() {
  return (
    <>
      <AppRouter />
      <Toaster 
         position="top-center"
         reverseOrder={false}
         toastOptions={{
           duration: 4000,
           style: {
             background: '#333',
             color: '#fff',
           },
         }}
      />
    </>
  )
}

export default App