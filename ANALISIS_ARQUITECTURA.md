# Análisis y Arquitectura del Proyecto Nexus InterBank

Este documento describe la estructura actual del sistema bancario "Nexus", identificando los componentes activos, su función y cómo se comunican. Este análisis se basa en la configuración de `docker-compose.yml` y el Gateway.

## 1. Visión General
El sistema está diseñado con una arquitectura de **Microservicios** orquestados detrás de un **API Gateway**.

El sistema se divide en tres grandes áreas de negocio:
1.  **Core Bancario**: Lógica central (clientes, cuentas, transacciones).
2.  **Sitio Web (Web)**: Portal para que los clientes realicen operaciones online.
3.  **Ventanilla**: Aplicación para el personal del banco (cajeros).

Todas las peticiones desde el exterior (Web o Ventanilla) pasan primero por el **Gateway** (`nexus-gateway`), que las redirige al servicio correspondiente.

## 2. Componentes Activos

### A. Infraestructura y Datos
*   **Base de Datos Relacional (PostgreSQL)**: Contenedor `postgres-db-nexus`. Almacena datos transaccionales, clientes y cuentas.
*   **Base de Datos Documental (MongoDB)**: Contenedor `mongo-db-nexus`. Almacena datos geográficos (sucursales, ubicaciones).
*   **API Gateway**: Contenedor `nexus-gateway` (Puerto `9080`). Es la puerta de entrada única. Nada habla directamente con los microservicios sin pasar por aquí (o entre ellos internamente).

### B. Core Bancario (Microservicios)
Estos servicios manejan la lógica pura del negocio.
*   **ms-transacciones** (`nexus-ms-transacciones`):
    *   Maneja depósitos, retiros y transferencias.
    *   Es el punto de comunicación con el **Switch Transaccional** (conexión externa con otros bancos).
    *   **Puerto interno**: 8082.
*   **ms-cuentas** (`nexus-ms-cuentas`):
    *   Gestiona saldos, creación de cuentas, tasas de interés.
    *   **Puerto interno**: 8083.
*   **ms-clientes** (`nexus-ms-clientes`):
    *   Información personal de los usuarios.
    *   **Puerto interno**: 8084.
*   **ms-geografia** (`nexus-ms-geografia`):
    *   Ubicación de cajeros, sucursales y feriados. Usa MongoDB.
    *   **Puerto interno**: 8081.

### C. Sitio Web (Cliente Final)
*   **Frontend**: `front-web`. Interfaz de usuario para clientes. (Puerto `8080` externo, mapeado al 80 del contenedor).
*   **Backend**: `web-backend`. API específica para servir al frontend web. Maneja autenticación de usuarios web y orquesta llamadas al Core.

### D. Ventanilla (Personal del Banco)
*   **Frontend**: `front-ventanilla`. Interfaz para cajeros. (Puerto `81` externo).
*   **Backend**: `ventanilla-backend`. API específica para la ventanilla. (Puerto `8085` externo).

## 3. Flujo de Comunicación

1.  **Usuario entra al sitio web**: El navegador va a `localhost:8080`.
2.  **Usuario hace login**: El frontend llama al Gateway (`localhost:9080/api/auth/...`).
    *   El **Gateway** redirige esa petición a `web-backend`.
3.  **Usuario hace una transferencia**:
    *   El frontend llama al Gateway.
    *   El Gateway redirige a `web-backend`.
    *   `web-backend` procesa la lógica y llama al Gateway (o internamente a los servicios) para invocar a `ms-transacciones`.
    *   `ms-transacciones` verifica saldo con `ms-cuentas` y registra el movimiento.

## 4. Limpieza Realizada
Se han eliminado del proyecto los siguientes elementos que no formaban parte de esta arquitectura activa:
*   Carpeta `ms-transaccion BANTEC` (Código muerto/ejemplo antiguo).
*   Archivos de texto sueltos (`gateway.txt`, `nexus_ms_...txt`).

## 5. Próximos Pasos Recomendados
*   **Docker Compose**: Para levantar el sistema limpio use `docker-compose up -d --build`.
*   **Transacciones**: Si necesita modificar lógica de transacciones, el servicio clave es `ms-transacciones`.
*   **Puertos**: Si necesita mover puertos, recuerde actualizar tanto el `metrics` del `docker-compose.yml` como las rutas en el `application.yml` del Gateway si cambia puertos internos.
