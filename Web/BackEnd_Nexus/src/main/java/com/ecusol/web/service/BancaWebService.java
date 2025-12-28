//ubi: src/main/java/com/ecusol/web/service/BancaWebService.java
package com.ecusol.web.service;

import com.ecusol.web.client.CoreBancarioClient;
import com.ecusol.web.client.TransaccionesClient;
import com.ecusol.web.dto.*;
import com.ecusol.web.model.Beneficiario;
import com.ecusol.web.model.UsuarioWeb;
import com.ecusol.web.repository.BeneficiarioRepository;
import com.ecusol.web.repository.UsuarioWebRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BancaWebService {

    @Autowired
    private CoreBancarioClient coreClient;
    @Autowired
    private TransaccionesClient txClient;
    @Autowired
    private BeneficiarioRepository beneficiarioRepo;
    @Autowired
    private UsuarioWebRepository usuarioWebRepo;

    public List<CuentaWebDTO> misCuentas(Integer clienteIdCore) {
        return coreClient.obtenerCuentasPorCliente(clienteIdCore).stream()
                .map(c -> new CuentaWebDTO(
                        c.getCuentaId().longValue(),
                        c.getNumeroCuenta(),
                        c.getSaldo(),
                        c.getEstado(),
                        c.getTipoCuentaId().longValue()))
                .collect(Collectors.toList());
    }

    public List<MovimientoWebDTO> misMovimientos(String cuenta) {
        var movsCore = coreClient.obtenerMovimientos(cuenta);
        
        // Si no hay movimientos, retornar lista vacía
        if (movsCore == null || movsCore.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        // Obtener saldo actual de la cuenta
        var cuentaInfo = coreClient.buscarCuenta(cuenta);
        BigDecimal saldoActual = cuentaInfo != null ? cuentaInfo.getSaldo() : BigDecimal.ZERO;
        
        // Obtener movimientos de transacciones para mapear tipo
        var movsTx = txClient.obtenerMovimientosPorCuenta(cuenta);
        var mapaTipos = movsTx.stream()
                .collect(java.util.stream.Collectors.toMap(
                        MovimientoTxDTO::transaccionId,
                        m -> m.tipo() != null ? m.tipo() : "TRANSFERENCIA",
                        (a, b) -> a));
        
        // Ordenar movimientos por fecha ascendente
        var movsOrdenados = movsCore.stream()
                .filter(m -> m != null && m.getMonto() != null && m.getFechaEjecucion() != null)
                .sorted((a, b) -> a.getFechaEjecucion().compareTo(b.getFechaEjecucion()))
                .collect(Collectors.toList());
        
        if (movsOrdenados.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        // Construir lista con saldos calculados
        // El saldoNuevo es el saldo DESPUÉS de cada movimiento
        List<MovimientoWebDTO> resultado = new java.util.ArrayList<>();
        
        // Primero, calcular saldos hacia adelante partiendo del saldo actual
        // Necesitamos ir hacia atrás para obtener el saldo inicial
        BigDecimal saldoCalculado = saldoActual.stripTrailingZeros();
        
        // Iterar hacia atrás para calcular el saldo inicial
        for (int i = movsOrdenados.size() - 1; i >= 0; i--) {
            var mov = movsOrdenados.get(i);
            // Revertir la operación: si fue crédito, restar; si fue débito, sumar
            if ("C".equals(mov.getTipo())) {
                saldoCalculado = saldoCalculado.subtract(mov.getMonto());
            } else {
                saldoCalculado = saldoCalculado.add(mov.getMonto());
            }
        }
        
        // Ahora saldoCalculado contiene el saldo inicial
        // Recorrer hacia adelante construyendo los DTOs
        for (var mov : movsOrdenados) {
            BigDecimal monto = mov.getMonto();
            // Aplicar el movimiento
            if ("C".equals(mov.getTipo())) {
                saldoCalculado = saldoCalculado.add(monto);
            } else {
                saldoCalculado = saldoCalculado.subtract(monto);
            }
            
            // Enrich con operacion desde transacciones
            String operacion = mapaTipos.getOrDefault(mov.getTransaccionId(), "TRANSFERENCIA");
            
            resultado.add(new MovimientoWebDTO(
                    mov.getFechaEjecucion(),
                    mov.getTipo(),
                    monto.doubleValue(),
                    saldoCalculado.doubleValue(),
                    mov.getDescripcion(),
                    operacion));
        }
        
        // Invertir para mostrar del más reciente al más antiguo
        java.util.Collections.reverse(resultado);
        return resultado;
    }

    public Map<String, Object> getMovimientosDebug(String cuenta) {
        Map<String, Object> debug = new java.util.HashMap<>();
        var movsCore = coreClient.obtenerMovimientos(cuenta);
        var cuentaInfo = coreClient.buscarCuenta(cuenta);
        
        debug.put("cuenta", cuenta);
        debug.put("movimientosCount", movsCore != null ? movsCore.size() : 0);
        debug.put("saldoActual", cuentaInfo != null ? cuentaInfo.getSaldo() : null);
        
        if (movsCore != null && !movsCore.isEmpty()) {
            List<Map<String, Object>> movs = new java.util.ArrayList<>();
            for (var m : movsCore) {
                Map<String, Object> mov = new java.util.HashMap<>();
                mov.put("transaccionId", m.getTransaccionId());
                mov.put("monto", m.getMonto());
                mov.put("fechaEjecucion", m.getFechaEjecucion());
                mov.put("rolTransaccion", m.getRolTransaccion());
                mov.put("tipo", m.getTipo());
                mov.put("descripcion", m.getDescripcion());
                movs.add(mov);
            }
            debug.put("movimientos", movs);
        }
        
        return debug;
    }

    public String transferir(TransferenciaRequest req) {
        // Map bancoDestino string to ID
        // 2 = EcuSol/Nexus (Interno), 1 = Otros Bancos
        Integer bancoDestinoId = ("ECUASOL".equalsIgnoreCase(req.bancoDestino())
                || "NEXUS".equalsIgnoreCase(req.bancoDestino()))
                        ? 2
                        : 1;
        return coreClient.realizarTransferencia(req, bancoDestinoId);
    }

    public TitularCuentaDTO validarDestinatarioCompleto(String numeroCuenta, String banco) {
        // Si el banco no es NEXUS/ECUASOL, es interbancario
        boolean esInterno = "ECUASOL".equalsIgnoreCase(banco) || "NEXUS".equalsIgnoreCase(banco);

        if (!esInterno) {
            // Para interbancarios, no podemos validar en nuestro CORE
            // Retornamos un titular genérico
            return new TitularCuentaDTO(numeroCuenta, "Cuenta Interbancaria", "***", "Cuenta Externa");
        }

        try {
            return coreClient.validarTitular(numeroCuenta);
        } catch (Exception e) {
            throw new RuntimeException("Cuenta no existe");
        }
    }

    public void solicitarCuenta(Integer clienteIdCore, Integer tipoCuentaId) {
        CrearCuentaRequest req = CrearCuentaRequest.builder()
                .clienteId(clienteIdCore)
                .tipoCuentaId(tipoCuentaId)
                .sucursalIdApertura(1) // FIX: Sucursal Matriz por defecto
                .saldoInicial(BigDecimal.ZERO)
                .build();
        coreClient.crearCuenta(req);
    }

    public List<SucursalDTO> obtenerSucursales() {
        return coreClient.obtenerSucursales();
    }

    public void guardarBeneficiario(Integer usuarioWebId, BeneficiarioDTO dto) {
        UsuarioWeb usuario = usuarioWebRepo.findById(usuarioWebId)
                .orElseThrow(() -> new RuntimeException("Usuario Web no encontrado"));

        Beneficiario b = new Beneficiario();
        b.setUsuarioWeb(usuario);
        b.setNumeroCuentaDestino(dto.numeroCuenta());
        b.setNombreTitular(dto.nombreTitular());
        b.setAlias(dto.alias());

        // Guardar Tipo de Cuenta correctamente
        b.setTipoCuenta(dto.tipoCuenta() != null ? dto.tipoCuenta() : "Desconocido");

        b.setFechaRegistro(java.time.LocalDateTime.now());
        beneficiarioRepo.save(b);
    }

    public List<BeneficiarioDTO> misBeneficiarios(Integer usuarioWebId) {
        return beneficiarioRepo.findByUsuarioWeb_UsuarioWebId(usuarioWebId).stream()
                .map(b -> new BeneficiarioDTO(
                        b.getBeneficiarioId(),
                        b.getNumeroCuentaDestino(),
                        b.getNombreTitular(),
                        b.getAlias(),
                        b.getTipoCuenta()))
                .collect(Collectors.toList());
    }
}