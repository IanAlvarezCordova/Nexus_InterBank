# 🏦 Guía de Integración con Switch DIGICONECU

**Documento para Bancos Participantes**  
Última actualización: 21 Diciembre 2025

---

## 📋 Resumen

Esta guía explica cómo integrar tu banco con el **Switch DIGICONECU** para realizar transferencias interbancarias en tiempo real.

---

## 🎯 Requisitos Previos

1. **Asignación de BIN**: Solicita un rango de BINs único al administrador del Switch (Kris Olalla)
2. **Código de Banco**: Define un código único (ej: `MIBANCO`, `ARCBANK`, `NEXUS`)
3. **Servidor con Docker**: Para ejecutar tus microservicios
4. **IP Pública o ngrok**: Para que el Switch pueda llamar a tu webhook

---

## 🔧 Arquitectura de Integración

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│   Tu Frontend   │ ──────► │  Tu Backend     │ ──────► │ Switch DIGICONECU│
│                 │         │ (ms-transacción)│         │  Puerto 9081     │
└─────────────────┘         └────────┬────────┘         └────────┬─────────┘
                                     │                           │
                                     ▼                           ▼
                            Webhook entrante            Otros Bancos
                            /api/transacciones/webhook  (NEXUS, ARCBANK, etc)
```

---

## 📌 Paso 1: Configuración de Variables de Entorno

En tu `docker-compose.yml` o `application.properties`:

```yaml
# docker-compose.yml
ms-transacciones:
  environment:
    # URL del Switch (Local vs Producción)
    APP_SWITCH_URL: http://host.docker.internal:9081      # Local
    # APP_SWITCH_URL: http://34.44.123.236:9080           # Producción
    
    # URL para Network Management (lista de bancos)
    APP_SWITCH_NETWORK_URL: http://host.docker.internal:9082
    
    # Tu código de banco registrado en el Switch
    BANCO_CODIGO: MIBANCO
```

```properties
# application.properties
api.switch.url=${APP_SWITCH_URL:http://host.docker.internal:9081}
api.switch.network.url=${APP_SWITCH_NETWORK_URL:http://host.docker.internal:9082}
banco.codigo=${BANCO_CODIGO:MIBANCO}
```

---

## 📌 Paso 2: DTOs Requeridos

### 2.1 Request para ENVIAR transferencias al Switch

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwitchTransferRequest {

    @JsonProperty("instructionId")
    private String instructionId;       // UUID único

    @JsonProperty("bancoOrigen")
    private String bancoOrigen;         // Tu código: "MIBANCO"

    @JsonProperty("bancoDestino")
    private String bancoDestino;        // "NEXUS", "ARCBANK", etc.

    @JsonProperty("cuentaOrigen")
    private String cuentaOrigen;        // Cuenta de tu banco

    @JsonProperty("cuentaDestino")
    private String cuentaDestino;       // Cuenta del banco destino

    @JsonProperty("monto")
    private BigDecimal monto;

    @JsonProperty("moneda")
    private String moneda;              // "USD"

    @JsonProperty("concepto")
    private String concepto;            // Descripción
}
```

### 2.2 Payload del Webhook (cuando RECIBES una transferencia)

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwitchWebhookPayload {

    @JsonProperty("bancoOrigen")
    private String bancoOrigen;         // "NEXUS", "ARCBANK", etc.

    @JsonProperty("cuentaOrigen")
    private String cuentaOrigen;

    @JsonProperty("cuentaDestino")
    private String cuentaDestino;       // Cuenta de TU banco

    @JsonProperty("monto")
    private BigDecimal monto;

    @JsonProperty("referencia")
    private String referencia;          // UUID - usar para idempotencia

    @JsonProperty("concepto")
    private String concepto;
}
```

### 2.3 Respuesta del Webhook

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwitchWebhookResponse {
    private boolean success;
    private String message;
    private String cuentaAcreditada;
}
```

---

## 📌 Paso 3: Cliente HTTP para el Switch

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class SwitchClient {

    private final RestTemplate restTemplate;

    @Value("${api.switch.url}")
    private String switchUrl;

    @Value("${api.switch.network.url:${api.switch.url}}")
    private String switchNetworkUrl;

    @Value("${banco.codigo}")
    private String bancoCodigo;

    /**
     * ENVIAR transferencia a otro banco via Switch
     * Endpoint: POST /api/v2/transfers
     */
    public SwitchTransferResponse enviarTransferencia(SwitchTransferRequest request) {
        String url = switchUrl + "/api/v2/transfers";
        log.info("📤 Enviando transferencia: {} -> {}", 
                 request.getCuentaOrigen(), request.getCuentaDestino());
        
        try {
            ResponseEntity<SwitchTransferResponse> response = 
                restTemplate.postForEntity(url, request, SwitchTransferResponse.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage());
            throw new RuntimeException("Error con el Switch: " + e.getMessage());
        }
    }

    /**
     * CONSULTAR lista de bancos disponibles
     * Endpoint: GET /api/v1/red/bancos
     */
    public List<BancoDTO> obtenerBancos() {
        String url = switchNetworkUrl + "/api/v1/red/bancos";
        try {
            ResponseEntity<List<BancoDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<BancoDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String getBancoCodigo() {
        return bancoCodigo;
    }
}
```

---

## 📌 Paso 4: Controlador del Webhook

```java
@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final TransaccionRepository repository;
    private final CuentaService cuentaService;
    private final SwitchClient switchClient;

    /**
     * RECIBIR transferencias de otros bancos
     * El Switch llama a este endpoint cuando alguien te envía dinero
     */
    @PostMapping("/webhook")
    public ResponseEntity<SwitchWebhookResponse> recibirTransferencia(
            @RequestBody SwitchWebhookPayload payload) {
        
        log.info("📥 Webhook recibido: {} -> {} por ${}", 
                 payload.getBancoOrigen(), 
                 payload.getCuentaDestino(), 
                 payload.getMonto());
        
        try {
            // 1. Verificar idempotencia (no procesar duplicados)
            if (repository.existsByInstructionId(payload.getReferencia())) {
                return ResponseEntity.ok(new SwitchWebhookResponse(
                    true, "Ya procesada", payload.getCuentaDestino()
                ));
            }
            
            // 2. Validar que la cuenta destino existe en tu banco
            Cuenta cuenta = cuentaService.buscarPorNumero(payload.getCuentaDestino());
            if (cuenta == null) {
                return ResponseEntity.status(404).body(new SwitchWebhookResponse(
                    false, "Cuenta no encontrada", null
                ));
            }
            
            // 3. Acreditar el monto
            cuentaService.acreditar(payload.getCuentaDestino(), payload.getMonto());
            
            // 4. Registrar la transacción
            Transaccion tx = new Transaccion();
            tx.setInstructionId(payload.getReferencia());
            tx.setCuentaOrigen(payload.getCuentaOrigen());
            tx.setCuentaDestino(payload.getCuentaDestino());
            tx.setMonto(payload.getMonto());
            tx.setDescripcion("Recibido de " + payload.getBancoOrigen());
            tx.setEstado("COMPLETED");
            repository.save(tx);
            
            return ResponseEntity.ok(new SwitchWebhookResponse(
                true, "Transferencia acreditada", payload.getCuentaDestino()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new SwitchWebhookResponse(
                false, "Error: " + e.getMessage(), null
            ));
        }
    }

    /**
     * Lista de bancos disponibles (para el frontend)
     */
    @GetMapping("/bancos")
    public ResponseEntity<List<BancoDTO>> obtenerBancos() {
        return ResponseEntity.ok(switchClient.obtenerBancos());
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "banco", switchClient.getBancoCodigo()
        ));
    }
}
```

---

## 📌 Paso 5: Lógica de Transferencias Salientes

En tu servicio de transacciones, cuando el usuario quiere enviar dinero a otro banco:

```java
public RespuestaDTO realizarTransferencia(SolicitudDTO solicitud) {
    
    // 1. Determinar si es interna o externa
    boolean esInterna = "MIBANCO".equalsIgnoreCase(solicitud.getBancoDestino());
    
    // 2. Debitar la cuenta origen (tu banco)
    cuentaService.debitar(solicitud.getCuentaOrigen(), solicitud.getMonto());
    
    if (esInterna) {
        // TRANSFERENCIA INTERNA: acreditar directamente
        cuentaService.acreditar(solicitud.getCuentaDestino(), solicitud.getMonto());
        
    } else {
        // TRANSFERENCIA EXTERNA: enviar al Switch
        SwitchTransferRequest request = SwitchTransferRequest.builder()
            .instructionId(UUID.randomUUID().toString())
            .bancoOrigen(switchClient.getBancoCodigo())  // "MIBANCO"
            .bancoDestino(solicitud.getBancoDestino())   // "NEXUS", "ARCBANK"
            .cuentaOrigen(solicitud.getCuentaOrigen())
            .cuentaDestino(solicitud.getCuentaDestino())
            .monto(solicitud.getMonto())
            .moneda("USD")
            .concepto(solicitud.getDescripcion())
            .build();
        
        SwitchTransferResponse response = switchClient.enviarTransferencia(request);
        
        if (!response.isSuccess()) {
            // COMPENSACIÓN: devolver el dinero
            cuentaService.acreditar(solicitud.getCuentaOrigen(), solicitud.getMonto());
            throw new RuntimeException("Switch rechazó: " + response.getError());
        }
    }
    
    return new RespuestaDTO("OK", "Transferencia exitosa");
}
```

---

## 📌 Paso 6: Registrarse en el Switch

### 6.1 Ambiente Local
Ejecuta estos comandos SQL en el contenedor del Switch:

```sql
-- Conectar a la BD del Switch
docker exec -i postgres-network psql -U postgres -d NetworkManagementDB

-- 1. Registrar tu banco
INSERT INTO "Bancos" (
    "Id", "Codigo", "Nombre", "Endpoint", 
    "Estado", "EstadoCircuito", "FallosConsecutivos", "LatenciaPromedioMs"
) VALUES (
    gen_random_uuid(), 
    'MIBANCO',                                          -- Tu código único
    'Mi Banco S.A.',                                    -- Nombre display
    'http://host.docker.internal:8082/api/transacciones/webhook', -- Tu webhook
    'Activo', 'CLOSED', 0, 0
);

-- 2. Registrar tus BINs (solicita un rango al administrador)
INSERT INTO "Enrutamiento" ("Id", "BancoId", "BinInicio", "BinFin", "Activo")
SELECT gen_random_uuid(), "Id", '700000', '709999', true 
FROM "Bancos" WHERE "Codigo" = 'MIBANCO';

-- 3. Verificar
SELECT "Codigo", "Nombre", "Endpoint" FROM "Bancos";
```

### 6.2 Ambiente Producción
Contacta al administrador del Switch (Kris Olalla) con:

| Campo | Valor |
|-------|-------|
| Código | `MIBANCO` |
| Nombre | `Mi Banco S.A.` |
| IP Pública | `X.X.X.X` |
| Puerto Webhook | `8082` (o el que uses) |
| BIN Solicitado | Rango de 6 dígitos (ej: `700000-709999`) |

---

## 📌 Paso 7: Pruebas

### 7.1 Health Check
```bash
curl http://localhost:8082/api/transacciones/health
```

### 7.2 Lista de Bancos
```bash
curl http://localhost:9082/api/v1/red/bancos
```

### 7.3 Enviar Transferencia a otro banco
```bash
curl -X POST http://localhost:9081/api/v2/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "instructionId": "test-123",
    "bancoOrigen": "MIBANCO",
    "cuentaOrigen": "7001234567",
    "cuentaDestino": "2701000001",
    "monto": 50.00,
    "moneda": "USD",
    "concepto": "Prueba"
  }'
```

### 7.4 Simular Webhook entrante
```bash
curl -X POST http://localhost:8082/api/transacciones/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "bancoOrigen": "NEXUS",
    "cuentaOrigen": "6001000001",
    "cuentaDestino": "7001234567",
    "monto": 25.00,
    "referencia": "test-456",
    "concepto": "Prueba webhook"
  }'
```

---

## 🎯 Checklist de Integración

- [ ] Código de banco definido (ej: `MIBANCO`)
- [ ] BINs asignados (ej: `700000-709999`)
- [ ] Cuentas creadas con números que empiecen con tu BIN
- [ ] `APP_SWITCH_URL` configurada
- [ ] `BANCO_CODIGO` configurada
- [ ] Endpoint `/api/transacciones/webhook` implementado
- [ ] Webhook accesible desde el Switch
- [ ] Banco registrado en base de datos del Switch
- [ ] Prueba de envío exitosa
- [ ] Prueba de recepción (webhook) exitosa

---

## 🆘 Troubleshooting

| Error | Causa | Solución |
|-------|-------|----------|
| `Connection refused` | Switch no está corriendo | Verificar `docker-compose up` del Switch |
| `422 Unprocessable Entity` | BIN no registrado | Ejecutar SQL de registro |
| `404 Not Found` | Código de banco incorrecto | Verificar `BANCO_CODIGO` |
| `504 Gateway Timeout` | Webhook no accesible | Usar `host.docker.internal` |
| `AM04 Insufficient Funds` | Sin saldo prefondeo | Recargar en Account Balance |

---

## 📞 Soporte

- **Switch Admin**: Kris Olalla
- **IP Switch (Producción)**: `34.44.123.236`
- **Puertos**:
  - API Gateway: `9080`
  - Payment Processing: `9081`
  - Network Management: `9082`
  - Account Balance: `9083`
