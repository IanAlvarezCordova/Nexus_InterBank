# 🏦 Proyecto NEXUS - Microservicios Bancarios

Este es el respaldo del sistema Nexus funcionando con los módulos de **Core** y **Ventanilla**.

## 🚀 Inicio Rápido para un Compañero

Si acabas de descargar este proyecto, sigue estos pasos para levantarlo exactamente como está ahora:

### 1. Requisitos
- Docker y Docker Desktop instalados.
- Git (opcional).

### 2. Levantar el Sistema
Abre una terminal en la carpeta raíz y ejecuta:

```bash
# Limpiar cualquier rastro previo
docker-compose down

# Construir y levantar todo
docker-compose up -d --build
```

### 3. Cargar Datos del Respaldo (Importante)
Para que tengas los mismos clientes, cuentas y tipos de cuenta que yo usé, importa el script de backup:

```bash
docker exec -i postgres-db-nexus psql -U postgres < backup_nexus_completo.sql
```

### 4. Accesos a los Módulos

| Módulo | URL | Usuario / Contraseña |
|--------|-----|----------------------|
| **Ventanilla (Frontend)** | [http://localhost:81](http://localhost:81) | `admin` / `admin` |
| **Banca Web (Frontend)** | [http://localhost:80](http://localhost:80) | (Tus usuarios registrados) |
| **Gateway / API** | [http://localhost:9080](http://localhost:9080) | N/A |

---

## 🛠️ Estructura del Proyecto

- `Ventanilla/`: Backend y Frontend del módulo de caja.
- `ms-transacciones/`: Orquestador Core y lógica de transferencias.
- `ms-clientes/`: Gestión de datos de personas.
- `cbs/`: (Core Banking Server) Gestión de cuentas y saldos.
- `gateway-server/`: Puerta de enlace para todas las peticiones.

---

## 🔍 Notas de Verificación
- El **usuario admin** de Ventanilla se crea automáticamente al iniciar el container `ventanilla-backend`.
- El **Ciclo de Dependencias** entre Gateway y Ventanilla ha sido corregido (Ventanilla ahora depende de Postgres).
- Se corrigió el error de **NullPointerException** al buscar clientes con cuentas sin tipo especificado.
- El **Gateway** ahora rutea correctamente hacia `/api/core/ventanilla/**`.

---
*Respaldo generado el 2025-12-21*
