-- Backfill script para popular campo `tipo` en tabla transaccion
-- Ejecutar en el contenedor postgres-db-nexus, base de datos postgres, schema public

-- 1. Marcar todas las filas sin tipo como TRANSFERENCIA por defecto
-- (asumiendo que la mayoría son transferencias desde la web)
UPDATE public.transaccion
SET tipo = 'TRANSFERENCIA'
WHERE tipo IS NULL OR tipo = '';

-- 2. (Opcional) Si tienes lógica específica para inferir DEPOSITO/RETIRO desde ventanilla, añádela aquí.
-- Ejemplo conceptual:
-- UPDATE public.transaccion
-- SET tipo = 'DEPOSITO'
-- WHERE tipo = 'TRANSFERENCIA'
--   AND cuenta_destino IS NULL
--   AND rol_transaccion = 'RECEPTOR';
--
-- UPDATE public.transaccion
-- SET tipo = 'RETIRO'
-- WHERE tipo = 'TRANSFERENCIA'
--   AND cuenta_destino IS NULL
--   AND rol_transaccion = 'EMISOR';

-- 3. Verificar resultado
SELECT tipo, COUNT(*) FROM public.transaccion GROUP BY tipo;
