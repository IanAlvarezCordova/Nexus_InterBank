package com.nexus.ms_transacciones.controller;

import com.nexus.ms_transacciones.client.CuentaClient;
import com.nexus.ms_transacciones.model.Transaccion;
import com.nexus.ms_transacciones.repository.TransaccionRepository;
import com.nexus.ms_transacciones.dto.VentanillaDTO.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/core/ventanilla")
@CrossOrigin(origins = "*")
@Slf4j
public class CoreVentanillaController {

    private final RestTemplate restTemplate;
    private final CuentaClient cuentaClient;
    private final TransaccionRepository transaccionRepository;
    private final String clientesUrl;
    private final String cuentasUrl;

    public CoreVentanillaController(
            RestTemplate restTemplate,
            CuentaClient cuentaClient,
            TransaccionRepository transaccionRepository,
            @Value("${api.clientes.url}") String clientesUrl,
            @Value("${api.cuentas.url}") String cuentasUrl) {
        this.restTemplate = restTemplate;
        this.cuentaClient = cuentaClient;
        this.transaccionRepository = transaccionRepository;
        this.clientesUrl = clientesUrl;
        this.cuentasUrl = cuentasUrl;
    }

    @GetMapping("/buscar-cliente/{cedula}")
    public ResponseEntity<ResumenClienteDTO> buscarCliente(@PathVariable String cedula) {
        log.info(">>> Core Ventanilla: Buscando cliente con cédula: {}", cedula);

        try {
            String urlCliente = clientesUrl + "/api/v1/clientes/buscar/" + cedula;
            log.info(">>> Core Ventanilla: Llamando a MS-CLIENTES: {}", urlCliente);
            Map<String, Object> clienteData = restTemplate.getForObject(urlCliente, Map.class);

            if (clienteData == null) {
                log.warn(">>> MS-CLIENTES retornó null para cédula {}", cedula);
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
                log.info(">>> Core Ventanilla: Llamando a MS-CUENTAS: {}", urlCuentas);
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

                    if ("INACTIVA".equalsIgnoreCase(cuenta.getEstado()) ||
                            "BLOQUEADA".equalsIgnoreCase(cuenta.getEstado())) {
                        log.warn("Cuenta {} está inactiva/bloqueada", cuenta.getNumeroCuenta());
                    }

                    cuentasList.add(cuenta);
                }
            }
            resumen.setCuentas(cuentasList);

            log.info(">>> Cliente encontrado: {} con {} cuentas", nombres, cuentasList.size());
            return ResponseEntity.ok(resumen);

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error(">>> Error cliente HTTP: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            log.error("Error buscando cliente: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/info-cuenta/{numeroCuenta}")
    public ResponseEntity<InfoCuentaDTO> infoCuenta(@PathVariable String numeroCuenta) {
        log.info(">>> Core Ventanilla: Info cuenta: {}", numeroCuenta);

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
            log.error("Error obteniendo info cuenta: {}", e.getMessage());
            throw new RuntimeException("Cuenta no válida o no existe");
        }
    }

    @PostMapping("/operar")
    public ResponseEntity<String> operar(@RequestBody TransaccionCajaRequest req) {
        log.info(">>> Core Ventanilla: Operación {} de {} en cuenta {}",
                req.getTipoOperacion(), req.getMonto(), req.getCuentaOrigen());

        String tipo = req.getTipoOperacion().toUpperCase();

        try {
            // Ejecutar operación sobre cuentas
            switch (tipo) {
                case "DEPOSITO":
                    cuentaClient.acreditar(req.getCuentaOrigen(), req.getMonto());
                    // Persistir transacción
                    guardarTransaccionVentanilla(req, "DEPOSITO", null);
                    return ResponseEntity.ok("TXN-DEP-" + System.currentTimeMillis());

                case "RETIRO":
                    cuentaClient.debitar(req.getCuentaOrigen(), req.getMonto());
                    guardarTransaccionVentanilla(req, "RETIRO", null);
                    return ResponseEntity.ok("TXN-RET-" + System.currentTimeMillis());

                case "TRANSFERENCIA":
                    if (req.getCuentaDestino() == null || req.getCuentaDestino().isEmpty()) {
                        throw new RuntimeException("Cuenta destino requerida para transferencias");
                    }
                    String urlValidar = cuentasUrl + "/api/v1/cuentas/por-numero/" + req.getCuentaDestino();
                    Map<String, Object> destino = restTemplate.getForObject(urlValidar, Map.class);
                    if (destino == null) {
                        throw new RuntimeException("Cuenta destino no existe");
                    }

                    cuentaClient.debitar(req.getCuentaOrigen(), req.getMonto());
                    cuentaClient.acreditar(req.getCuentaDestino(), req.getMonto());
                    guardarTransaccionVentanilla(req, "TRANSFERENCIA", req.getCuentaDestino());
                    return ResponseEntity.ok("TXN-TRF-" + System.currentTimeMillis());

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
        log.info(">>> Core Ventanilla: Cambiar estado cuenta {} a {}", numeroCuenta, estado);

        String url = cuentasUrl + "/api/v1/cuentas/" + numeroCuenta + "/estado?estado=" + estado;
        restTemplate.put(url, null);
        return ResponseEntity.ok("Estado de cuenta actualizado");
    }

    @PostMapping("/clientes/estado")
    public ResponseEntity<String> cambiarEstadoCliente(
            @RequestParam String cedula,
            @RequestParam String estado) {
        log.info(">>> Core Ventanilla: Cambiar estado cliente {} a {}", cedula, estado);

        String url = clientesUrl + "/api/v1/clientes/estado?cedula=" + cedula + "&estado=" + estado;
        restTemplate.postForEntity(url, null, String.class);
        return ResponseEntity.ok("Estado de cliente actualizado");
    }

    @DeleteMapping("/cuentas/{numeroCuenta}")
    public ResponseEntity<String> eliminarCuenta(@PathVariable String numeroCuenta) {
        log.info(">>> Core Ventanilla: Eliminar cuenta {}", numeroCuenta);

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
