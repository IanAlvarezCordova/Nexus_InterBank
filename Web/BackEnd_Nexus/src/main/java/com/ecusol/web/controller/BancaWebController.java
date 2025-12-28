//ubi: src/main/java/com/ecusol/web/controller/BancaWebController.java
package com.ecusol.web.controller;

import com.ecusol.web.config.JwtTokenProvider;
import com.ecusol.web.dto.*;
import com.ecusol.web.service.BancaWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/web")
@CrossOrigin(origins = "*")
public class BancaWebController {

    @Autowired
    private BancaWebService bankingService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Integer getClienteIdCore(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Token inválido");
        String token = authHeader.substring(7);
        return jwtTokenProvider.getId(token).intValue();
    }

    private Integer getUsuarioWebId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Token inválido");
        String token = authHeader.substring(7);
        return jwtTokenProvider.getUsuarioWebId(token);
    }

    @GetMapping("/cuentas")
    public List<CuentaWebDTO> getCuentas(@RequestHeader("Authorization") String token) {
        return bankingService.misCuentas(getClienteIdCore(token));
    }

    @GetMapping("/movimientos/{numeroCuenta}")
    public List<MovimientoWebDTO> getMovimientos(@PathVariable String numeroCuenta) {
        return bankingService.misMovimientos(numeroCuenta);
    }

    @GetMapping("/movimientos-debug/{numeroCuenta}")
    public ResponseEntity<?> getMovimientosDebug(@PathVariable String numeroCuenta) {
        return ResponseEntity.ok(bankingService.getMovimientosDebug(numeroCuenta));
    }

    @PostMapping("/transferir")
    public String transferir(@RequestBody TransferenciaRequest req) {
        return bankingService.transferir(req);
    }

    @PostMapping("/solicitar-cuenta")
    public ResponseEntity<String> solicitarCuenta(@RequestHeader("Authorization") String token,
            @RequestParam Integer tipoCuentaId) {
        bankingService.solicitarCuenta(getClienteIdCore(token), tipoCuentaId);
        return ResponseEntity.ok("Cuenta creada exitosamente");
    }

    // --- CORRECCIÓN ---
    @GetMapping("/validar-destinatario/{numero}")
    public DestinatarioDTO validar(@PathVariable String numero, @RequestParam(required = false) String banco) {
        // Ahora recibimos el objeto completo del servicio
        TitularCuentaDTO titular = bankingService.validarDestinatarioCompleto(numero, banco);

        // Y lo transformamos al DTO del Front con todos los datos
        return new DestinatarioDTO(
                titular.getNumeroCuenta(),
                titular.getNombreCompleto(),
                titular.getIdentificacionParcial(),
                titular.getTipoCuenta() // <--- ¡Ahora sí viaja!
        );
    }

    @GetMapping("/sucursales")
    public List<SucursalDTO> getSucursales() {
        return bankingService.obtenerSucursales();
    }

    @PostMapping("/beneficiarios")
    public ResponseEntity<String> guardarBeneficiario(@RequestHeader("Authorization") String token,
            @RequestBody BeneficiarioDTO dto) {
        Integer idWeb = getUsuarioWebId(token);
        bankingService.guardarBeneficiario(idWeb, dto);
        return ResponseEntity.ok("Beneficiario guardado");
    }

    @GetMapping("/beneficiarios")
    public List<BeneficiarioDTO> listarBeneficiarios(@RequestHeader("Authorization") String token) {
        return bankingService.misBeneficiarios(getUsuarioWebId(token));
    }
}