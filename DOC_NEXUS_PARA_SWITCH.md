# 📋 Ficha Técnica - Banco NEXUS

**Para: Administrador Switch DIGICONECU (Kris Olalla)**  
**Banco: NEXUS / EcuSol**  
**Fecha: 21 Diciembre 2025**

---

## 🏦 Información del Banco

| Campo | Valor |
|-------|-------|
| **Código** | `NEXUS` |
| **Nombre** | `Banco Nexus EcuSol` |
| **BIN Solicitado** | `270100 - 270199` |
| **Puerto Webhook** | `8082` |
| **Ruta Webhook** | `/api/transacciones/webhook` |

---

## 🌐 URLs de Conexión

### Ambiente Local (Pruebas)
```
Webhook: http://host.docker.internal:8082/api/transacciones/webhook
```

### Ambiente Producción (Cuando se despliegue)
```
Webhook: http://<IP_PUBLICA>:8082/api/transacciones/webhook
```

---

## 📥 Webhook - Recibir Transferencias

**Endpoint:** `POST /api/transacciones/webhook`

### Request que NEXUS espera recibir:
```json
{
  "bancoOrigen": "ARCBANK",
  "cuentaOrigen": "2301234567",
  "cuentaDestino": "6001000001",
  "monto": 100.00,
  "referencia": "uuid-unico-idempotencia",
  "concepto": "Pago de servicios"
}
```

### Response que NEXUS retorna:

**Éxito (200 OK):**
```json
{
  "success": true,
  "message": "Transferencia recibida exitosamente",
  "cuentaAcreditada": "6001000001"
}
```

**Error - Cuenta no encontrada (404):**
```json
{
  "success": false,
  "message": "Cuenta destino no existe",
  "cuentaAcreditada": null
}
```

**Error - Proceso fallido (422):**
```json
{
  "success": false,
  "message": "Error: [descripción del error]",
  "cuentaAcreditada": null
}
```

---

## 📤 Envío de Transferencias

NEXUS envía transferencias al Switch usando:

**Endpoint del Switch:** `POST http://<SWITCH_URL>/api/v2/transfers`

### Request que NEXUS envía:
```json
{
  "instructionId": "550e8400-e29b-41d4-a716-446655440000",
  "bancoOrigen": "NEXUS",
  "bancoDestino": "ARCBANK",
  "cuentaOrigen": "6001000001",
  "cuentaDestino": "2301234567",
  "monto": 50.00,
  "moneda": "USD",
  "concepto": "Transferencia interbancaria"
}
```

---

## 🔧 Configuración del Microservicio

### Variables de Entorno
```yaml
APP_SWITCH_URL: http://host.docker.internal:9081      # Payment Processing
APP_SWITCH_NETWORK_URL: http://host.docker.internal:9082  # Network Management
BANCO_CODIGO: NEXUS
```

### Endpoints Expuestos por NEXUS
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/transacciones/webhook` | Recibir transferencias entrantes |
| GET | `/api/transacciones/bancos` | Lista de bancos (proxy al Switch) |
| GET | `/api/transacciones/health` | Health check |

---

## 📊 Cuentas de Prueba NEXUS

| Número Cuenta | Saldo | Cliente |
|---------------|-------|---------|
| `2701000001` | $5,000 | Cliente 1 |
| `2701000002` | $5,000 | Cliente 1 |
| `2701000003` | $5,000 | Cliente 2 |
| `2701000004` | $5,000 | Cliente 2 |

> ⚠️ Todas las cuentas empiezan con `2701` (BIN de NEXUS)

---

## 🔌 SQL para Registrar NEXUS en el Switch

```sql
-- Registrar banco NEXUS
INSERT INTO "Bancos" (
    "Id", "Codigo", "Nombre", "Endpoint", 
    "Estado", "EstadoCircuito", "FallosConsecutivos", "LatenciaPromedioMs"
) VALUES (
    gen_random_uuid(), 
    'NEXUS',
    'Banco Nexus EcuSol',
    'http://host.docker.internal:8082/api/transacciones/webhook',
    'Activo', 'CLOSED', 0, 0
) ON CONFLICT ("Codigo") DO UPDATE SET "Endpoint" = EXCLUDED."Endpoint";

-- Registrar BINs de NEXUS
INSERT INTO "Enrutamiento" ("Id", "BancoId", "BinInicio", "BinFin", "Activo")
SELECT gen_random_uuid(), "Id", '270100', '270199', true 
FROM "Bancos" WHERE "Codigo" = 'NEXUS';
```

---

## 🧪 Pruebas de Conectividad

### 1. Health Check de NEXUS
```bash
curl http://localhost:8082/api/transacciones/health
```
**Respuesta esperada:**
```json
{"status":"UP","service":"ms-transacciones","banco":"NEXUS"}
```

### 2. Simular Webhook (Switch → NEXUS)
```bash
curl -X POST http://localhost:8082/api/transacciones/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "bancoOrigen": "ARCBANK",
    "cuentaOrigen": "2301234567",
    "cuentaDestino": "2701000001",
    "monto": 25.00,
    "referencia": "test-'$(date +%s)'",
    "concepto": "Prueba webhook"
  }'
```

---

## 📁 Estructura del Proyecto NEXUS

```
ms-transacciones/
├── src/main/java/com/nexus/ms_transacciones/
│   ├── client/
│   │   └── SwitchClient.java          # Cliente HTTP al Switch
│   ├── controller/
│   │   ├── TransaccionClienteController.java
│   │   └── TransaccionInterbancariaController.java  # Webhook
│   ├── dto/
│   │   ├── SwitchTransferRequest.java    # Envío
│   │   ├── SwitchWebhookPayload.java     # Recepción
│   │   ├── SwitchWebhookResponse.java    # Respuesta
│   │   └── BancoDTO.java                 # Lista bancos
│   └── service/
│       └── TransaccionServiceImpl.java   # Lógica principal
└── src/main/resources/
    └── application.properties            # Configuración
```

---

## ✅ Estado de Integración

| Componente | Estado |
|------------|--------|
| Webhook implementado | ✅ Listo |
| Envío al Switch | ✅ Listo |
| Lista de bancos | ✅ Listo |
| Cuentas con BIN 600 | ✅ Creadas |
| Health check | ✅ Funcionando |
| Registro en Switch | ⏳ Pendiente (requiere admin) |

---

## 📞 Contacto

**Equipo NEXUS**  
Responsable: Stephani Rivera  
Fecha integración: 21 Diciembre 2025
