import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService, RegisterData } from '@/services/authService';
import { Boton } from '@/components/common/Boton';
import { Input } from '@/components/common/Input';
import { LogoEcuSol } from '@/components/common/LogoEcuSol';
import { toast } from 'react-hot-toast';
import { User, Lock, Mail, Phone, MapPin, CreditCard } from 'lucide-react';

const PaginaRegistro = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  
  const [formData, setFormData] = useState<RegisterData>({
    cedula: '',
    nombres: '',
    apellidos: '',
    email: '',
    usuario: '',
    password: '',
    telefono: '',
    direccion: ''
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { id, value } = e.target;
    if (id === 'usuario') {
      setFormData({ ...formData, [id]: value.toUpperCase().replace(/[^A-Z0-9]/g, '') });
    } else {
      setFormData({ ...formData, [id]: value });
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.cedula.length < 10) { toast.error("Cédula inválida"); return; }
    if (formData.password.length < 4) { toast.error("Contraseña muy corta"); return; }

    setLoading(true);
    try {
      
      await authService.register(formData);
      
      toast.success("¡Cuenta creada exitosamente!");
      toast("Ahora inicia sesión con tu usuario", { icon: '🔐' });
      
      setTimeout(() => navigate('/login'), 2000);
    } catch (err: any) {
      toast.error(err.message || 'Error al registrar.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-ecusol-primario/5 py-10 px-4 flex justify-center items-center">
      <div className="max-w-4xl w-full bg-white rounded-3xl shadow-2xl overflow-hidden flex flex-col md:flex-row">
        
        
        <div className="bg-ecusol-primario p-10 text-white md:w-1/3 flex flex-col justify-between">
          <div>
            <LogoEcuSol size={50} className="text-white mb-6" />
            <h2 className="text-3xl font-bold mb-4">Únete a Nexus Bank</h2>
            <p className="opacity-80">Crea tu cuenta digital en minutos y accede a la banca del futuro.</p>
          </div>
          <div className="mt-10 space-y-4 text-sm opacity-70">
            <p>✓ Cuentas sin costo de mantenimiento</p>
            <p>✓ Transferencias inmediatas</p>
            <p>✓ Seguridad garantizada</p>
          </div>
        </div>

        
        <div className="p-10 md:w-2/3">
          <h2 className="text-2xl font-bold text-gray-800 mb-6">Formulario de Registro</h2>
          <form onSubmit={handleSubmit} className="space-y-6">
            
            
            <div className="space-y-4">
                <h3 className="text-sm font-bold text-ecusol-secundario uppercase tracking-wider flex items-center gap-2">
                    <CreditCard size={16}/> Datos Personales
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <Input id="cedula" label="Cédula" value={formData.cedula} onChange={handleChange} required maxLength={10} placeholder="1700000000" />
                    <Input id="telefono" label="Celular" value={formData.telefono} onChange={handleChange} required placeholder="099..." />
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <Input id="nombres" label="Nombres" value={formData.nombres} onChange={handleChange} required />
                    <Input id="apellidos" label="Apellidos" value={formData.apellidos} onChange={handleChange} required />
                </div>
                <Input id="direccion" label="Dirección" value={formData.direccion} onChange={handleChange} required prefix={(<MapPin size={16}/> as unknown) as string} />
            </div>

            <div className="border-t border-gray-100"></div>

            
            <div className="space-y-4">
                <h3 className="text-sm font-bold text-ecusol-secundario uppercase tracking-wider flex items-center gap-2">
                    <Lock size={16}/> Datos de Acceso
                </h3>
                <Input id="email" type="email" label="Correo Electrónico" value={formData.email} onChange={handleChange} required prefix={(<Mail size={16}/> as unknown) as string} />
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <Input 
                        id="usuario" 
                        label="Usuario Deseado" 
                        value={formData.usuario} 
                        onChange={handleChange} 
                        required 
                        className="uppercase font-bold text-ecusol-primario"
                        prefix={(<User size={16}/> as unknown) as string}
                    />
                    <Input 
                        id="password" 
                        type="password" 
                        label="Contraseña" 
                        value={formData.password} 
                        onChange={handleChange} 
                        required 
                        prefix={(<Lock size={16}/> as unknown) as string}
                    />
                </div>
            </div>

            <Boton type="submit" disabled={loading} className="w-full py-4 text-lg shadow-lg mt-4">
              {loading ? 'Procesando Registro...' : 'Crear mi Cuenta'}
            </Boton>
          </form>
        </div>
      </div>
    </div>
  );
};

export default PaginaRegistro;