-- =====================================================
-- SCRIPT DE INICIALIZACIÓN - NEXUS MICROSERVICIOS
-- =====================================================

-- 1. Esquemas para el CORE
CREATE SCHEMA IF NOT EXISTS nexus_cuentas;
CREATE SCHEMA IF NOT EXISTS nexus_clientes;
CREATE SCHEMA IF NOT EXISTS nexus_transacciones;

-- 2. Esquema para el SERVICIO WEB
CREATE SCHEMA IF NOT EXISTS nexus_web;

-- 3. Esquema para el SERVICIO VENTANILLA
CREATE SCHEMA IF NOT EXISTS nexus_ventanilla;

-- =====================================================
-- DATOS INICIALES (se ejecutan después de que Hibernate cree las tablas)
-- Para insertar datos iniciales, ejecutar manualmente después del primer arranque:
-- =====================================================

-- Los siguientes comandos deben ejecutarse DESPUÉS de que los servicios hayan creado las tablas:
-- 
INSERT INTO public.tipocuenta (nombre, descripcion, estado, tipoamortizacion) 
VALUES 
  ('AHORROS', 'Cuenta de Ahorros', 'ACTIVO', 'MENSUAL'),
  ('CORRIENTE', 'Cuenta Corriente', 'ACTIVO', 'MENSUAL')
ON CONFLICT DO NOTHING;