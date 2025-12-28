-- =====================================================
-- SCRIPT DE DATOS INICIALES - EJECUTAR DESPUÉS DEL PRIMER ARRANQUE
-- Comando: docker exec postgres-db-nexus psql -U postgres -f /docker-entrypoint-initdb.d/seed_data.sql
-- =====================================================

-- Tipos de Cuenta
INSERT INTO public.tipocuenta (nombre, descripcion, estado, tipoamortizacion) 
VALUES 
  ('AHORROS', 'Cuenta de Ahorros', 'ACTIVO', 'MENSUAL'),
  ('CORRIENTE', 'Cuenta Corriente', 'ACTIVO', 'MENSUAL')
ON CONFLICT DO NOTHING;

-- Verificación
SELECT 'Tipos de cuenta insertados:' as mensaje;
SELECT * FROM public.tipocuenta;
