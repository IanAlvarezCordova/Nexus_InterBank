package com.nexus.ms_transacciones.controller;

import com.nexus.ms_transacciones.client.CuentaClient;
import com.nexus.ms_transacciones.client.SwitchClient;
import com.nexus.ms_transacciones.dto.IsoMensajeDTO;
import com.nexus.ms_transacciones.dto.VentanillaDTO;
import com.nexus.ms_transacciones.dto.SolicitudTransferenciaDTO;
import com.nexus.ms_transacciones.model.Transaccion;
import com.nexus.ms_transacciones.repository.TransaccionRepository;
import com.nexus.ms_transacciones.service.TransaccionService;
import static com.nexus.ms_transacciones.dto.VentanillaDTO.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/core/ventanilla")
@CrossOrigin(origins = "*")
@Slf4j
public class CoreVentanillaController {

    private final RestTemplate restTemplate;
    private final CuentaClient cuentaClient;
    private final SwitchClient switchClient;
    private final TransaccionRepository transaccionRepository;
    private final TransaccionService transaccionService;
    private final String clientesUrl;
    private final String cuentasUrl;

    @Value("${banco.codigo:NEXUS}")
    private String bancoCodigo;

    public CoreVentanillaController(
            RestTemplate restTemplate,
            CuentaClient cuentaClient,
            SwitchClient switchClient,
            TransaccionRepository transaccionRepository,
            TransaccionService transaccionService,
            @Value("${api.clientes.url}") String clientesUrl,
            @Value("${api.cuentas.url}") String cuentasUrl) {
        this.restTemplate = restTemplate;
        this.cuentaClient = cuentaClient;
        this.switchClient = switchClient;
        this.transaccionRepository = transaccionRepository;
        this.transaccionService = transaccionService;
        this.clientesUrl = clientesUrl;
        this.cuentasUrl = cuentasUrl;
    }

    @GetMapping("/buscar-cliente/{cedula}")
    public ResponseEntity<ResumenClienteDTO> buscarCliente(@PathVariable String cedula) {
        log.info(">>> Core Ventanilla: Buscando cliente con cédula: {}", cedula);

        try {
            String urlCliente = clientesUrl + "/api/v1/clientes/buscar/" + cedula;
            Map<String, Object> clienteData = restTemplate.getForObject(urlCliente, Map.class);

            if (clienteData == null) {
                return ResponseEntity.notFound().build();
            }

            String estadoCliente = (String) clienteData.get("estado");
            if ("BLOQUEADO".equalsIgnoreCase(estadoCliente)) {
                throw new RuntimeException("Cliente bloqueado. Contacte a soporte.");
            }

            Integer clienteId = (Integer) clienteData.get("clienteId");
            String nombres = clienteData.get("nombres") + " " + clienteData.get("apellidos");

            List<Map<String, Object>> cuentasData = new ArrayList<>();
            try {
                String urlCuentas = cuentasUrl + "/api/v1/cuentas?clienteId=" + clienteId;
                cuentasData = restTemplate.getForObject(urlCuentas, List.class);
            } catch (Exception exCuentas) {
                log.error(">>> ERROR obteniendo cuentas: {}", exCuentas.getMessage());
            }

            ResumenClienteDTO resumen = new ResumenClienteDTO();
            resumen.setClienteId(clienteId);
            resumen.setNombres(nombres);
            resumen.setCedula(cedula);
            resumen.setEstado(estadoCliente);

            List<CuentaResumenDTO> cuentasList = new ArrayList<>();
            if (cuentasData != null) {
                for (Map<String, Object> c : cuentasData) {
                    CuentaResumenDTO cuenta = new CuentaResumenDTO();
                    cuenta.setNumeroCuenta((String) c.get("numeroCuenta"));
                    cuenta.setSaldo(new BigDecimal(c.get("saldo").toString()));
                    cuenta.setEstado((String) c.get("estado"));

                    Integer tipoCuentaId = (Integer) c.get("tipoCuentaId");
                    String tipoNombre = obtenerNombreTipoCuenta(tipoCuentaId);
                    cuenta.setTipo(tipoNombre);
                    cuenta.setTipoCuentaId(tipoCuentaId);

                    cuentasList.add(cuenta);
                }
            }
            resumen.setCuentas(cuentasList);

            return ResponseEntity.ok(resumen);

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/info-cuenta/{numeroCuenta}")
    public ResponseEntity<InfoCuentaDTO> infoCuenta(@PathVariable String numeroCuenta) {
        try {
            String urlCuenta = cuentasUrl + "/api/v1/cuentas/por-numero/" + numeroCuenta;
            Map<String, Object> cuentaData = restTemplate.getForObject(urlCuenta, Map.class);

            if (cuentaData == null) {
                return ResponseEntity.notFound().build();
            }

            Integer clienteId = (Integer) cuentaData.get("clienteId");
            Integer tipoCuentaId = (Integer) cuentaData.get("tipoCuentaId");

            String urlCliente = clientesUrl + "/api/v1/clientes/" + clienteId;
            Map<String, Object> clienteData = restTemplate.getForObject(urlCliente, Map.class);

            InfoCuentaDTO info = new InfoCuentaDTO();
            info.setNumeroCuenta(numeroCuenta);
            info.setNombreCompleto(clienteData.get("nombres") + " " + clienteData.get("apellidos"));
            info.setTipoCuenta(obtenerNombreTipoCuenta(tipoCuentaId));

            return ResponseEntity.ok(info);

        } catch (Exception e) {
            throw new RuntimeException("Cuenta no válida o no existe");
        }
    }

    @GetMapping("/movimientos/{numeroCuenta}")
    public ResponseEntity<List<com.nexus.ms_transacciones.dto.MovimientoDTO>> obtenerMovimientos(
            @PathVariable String numeroCuenta) {
        try {
            List<com.nexus.ms_transacciones.dto.MovimientoDTO> movimientos = transaccionService
                    .obtenerMovimientosPorCuenta(numeroCuenta);
            return ResponseEntity.ok(movimientos);
        } catch (Exception e) {
            log.error("Error obteniendo movimientos: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/devoluciones")
    public ResponseEntity<?> iniciarDevolucion(@RequestBody Map<String, String> payload) {
        String originalTxId = payload.get("originalTxId");
        String motivo = payload.getOrDefault("motivo", "AC04"); // Código ISO default (Closed Account)

        log.info(">>> Iniciando devolución manual (pacs.004) para TX: {}", originalTxId);

        try {
            // Convertir String a UUID si es necesario, o manejar string directo en servicio
            // si se cambia
            // Por ahora asumimos que el instructionId es un UUID válido
            UUID uuidInstruccion = UUID.fromString(originalTxId);

            transaccionService.iniciarDevolucion(uuidInstruccion, motivo);

            return ResponseEntity.ok(Map.of("message", "Solicitud de devolución enviada al Switch correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("El ID de transacción no tiene un formato válido (UUID)");
        } catch (Exception e) {
            log.error("Error enviando devolución: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error al procesar devolución: " + e.getMessage());
        }
    }

    @PostMapping("/operar")
    public ResponseEntity<String> operar(@RequestBody TransaccionCajaRequest req) {
        log.info(">>> Core Ventanilla: Operación {} de {} en cuenta {}",
                req.getTipoOperacion(), req.getMonto(), req.getCuentaOrigen());

        String tipo = req.getTipoOperacion().toUpperCase();

        try {
            switch (tipo) {
                case "DEPOSITO":
                    cuentaClient.acreditar(req.getCuentaOrigen(), req.getMonto());
                    transaccionService.guardarTransaccionVentanilla(req, "DEPOSITO", null);
                    return ResponseEntity.ok("TXN-DEP-" + System.currentTimeMillis());

                case "RETIRO":
                    cuentaClient.debitar(req.getCuentaOrigen(), req.getMonto());
                    transaccionService.guardarTransaccionVentanilla(req, "RETIRO", null);
                    return ResponseEntity.ok("TXN-RET-" + System.currentTimeMillis());

                case "TRANSFERENCIA":
                    if (req.getCuentaDestino() == null || req.getCuentaDestino().isEmpty()) {
                        throw new RuntimeException("Cuenta destino requerida para transferencias");
                    }

                    // Si viene bancoDestino y es diferente al mío, es Interbancaria
                    String bancoDest = req.getBancoDestino();
                    boolean esInterbancaria = bancoDest != null && !bancoDest.isEmpty()
                            && !bancoDest.equals(bancoCodigo) && !bancoDest.equals("ECUASOL"); // Asumimos ECUASOL =
                                                                                               // NEXUS for legacy or
                                                                                               // check exact code

                    if (esInterbancaria) {
                        log.info(">>> Iniciando Transferencia INTERBANCARIA hacia {}", bancoDest);
                        // Delegar a TransaccionService para que use SwitchClient

                        SolicitudTransferenciaDTO txDto = new SolicitudTransferenciaDTO();
                        txDto.setCuentaOrigen(req.getCuentaOrigen());
                        txDto.setCuentaDestino(req.getCuentaDestino());
                        txDto.setMonto(req.getMonto());
                        txDto.setBancoDestinoCodigo(bancoDest);
                        txDto.setDescripcion(req.getDescripcion());

                        transaccionService.realizarTransferencia(txDto);
                        return ResponseEntity.ok("TXN-SW-" + System.currentTimeMillis());

                    } else {
                        // LOCAL
                        try {
                            String urlValidar = cuentasUrl + "/api/v1/cuentas/por-numero/" + req.getCuentaDestino();
                            restTemplate.getForObject(urlValidar, Map.class);
                        } catch (Exception e) {
                            throw new RuntimeException("Cuenta destino no existe");
                        }

                        cuentaClient.debitar(req.getCuentaOrigen(), req.getMonto());
                        cuentaClient.acreditar(req.getCuentaDestino(), req.getMonto());
                        transaccionService.guardarTransaccionVentanilla(req, "TRANSFERENCIA", req.getCuentaDestino());
                        return ResponseEntity.ok("TXN-TRF-" + System.currentTimeMillis());
                    }

                default:
                    throw new RuntimeException("Tipo de operación no válido: " + tipo);
            }
        } catch (Exception e) {
            log.error("Error en operación: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/cuentas/{numeroCuenta}/estado")
    public ResponseEntity<String> cambiarEstadoCuenta(
            @PathVariable String numeroCuenta,
            @RequestParam String estado) {
        String url = cuentasUrl + "/api/v1/cuentas/" + numeroCuenta + "/estado?estado=" + estado;
        restTemplate.put(url, null);
        return ResponseEntity.ok("Estado de cuenta actualizado");
    }

    @PostMapping("/clientes/estado")
    public ResponseEntity<String> cambiarEstadoCliente(
            @RequestParam String cedula,
            @RequestParam String estado) {
        String url = clientesUrl + "/api/v1/clientes/estado?cedula=" + cedula + "&estado=" + estado;
        restTemplate.postForEntity(url, null, String.class);
        return ResponseEntity.ok("Estado de cliente actualizado");
    }

    @DeleteMapping("/cuentas/{numeroCuenta}")
    public ResponseEntity<String> eliminarCuenta(@PathVariable String numeroCuenta) {
        String url = cuentasUrl + "/api/v1/cuentas/" + numeroCuenta;
        restTemplate.delete(url);
        return ResponseEntity.ok("Cuenta eliminada");
    }

    private String obtenerNombreTipoCuenta(Integer tipoCuentaId) {
        if (tipoCuentaId == null)
            return "Cuenta";
        try {
            String url = cuentasUrl + "/api/tipos-cuenta/" + tipoCuentaId;
            Map<String, Object> tipo = restTemplate.getForObject(url, Map.class);
            return tipo != null ? (String) tipo.get("nombre") : "Cuenta";
        } catch (Exception e) {
            return "Cuenta #" + tipoCuentaId;
        }
    }
}