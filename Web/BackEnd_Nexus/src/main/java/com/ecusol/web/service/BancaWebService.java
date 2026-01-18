package com.ecusol.web.service;

import com.ecusol.web.client.CoreBancarioClient;
import com.ecusol.web.dto.*;
import com.ecusol.web.model.Beneficiario;
import com.ecusol.web.model.UsuarioWeb;
import com.ecusol.web.repository.BeneficiarioRepository;
import com.ecusol.web.repository.UsuarioWebRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BancaWebService {

    @Autowired
    private CoreBancarioClient coreClient;
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
        return movsCore.stream()
                .map(m -> new MovimientoWebDTO(
                        m.getFechaEjecucion(),
                        m.getTipo(),
                        m.getMonto(),
                        BigDecimal.ZERO, 
                        m.getDescripcion()))
                .collect(Collectors.toList());
    }

    public String transferir(TransferenciaRequest req) {
        Integer bancoDestinoId = ("ECUASOL".equalsIgnoreCase(req.bancoDestino())
                || "NEXUS".equalsIgnoreCase(req.bancoDestino()))
                        ? 2
                        : 1;
        return coreClient.realizarTransferencia(req, bancoDestinoId);
    }

    public TitularCuentaDTO validarDestinatarioCompleto(String numeroCuenta, String banco) {
        boolean esInterno = "ECUASOL".equalsIgnoreCase(banco) || "NEXUS".equalsIgnoreCase(banco);

        if (!esInterno) {
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
                .sucursalIdApertura(1) 
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
        return movsCore.stream()
                .map(m -> new MovimientoWebDTO(
                        m.getFechaEjecucion(),
                        m.getTipo(),
                        m.getMonto(),
                        BigDecimal.ZERO, 
                        m.getDescripcion()))