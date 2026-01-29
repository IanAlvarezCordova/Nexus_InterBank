package com.ecusol.ventanilla.service;

import com.ecusol.ventanilla.client.CoreClient;
import com.ecusol.ventanilla.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentanillaService {

    @Autowired
    private CoreClient coreClient;

    public ResumenClienteDTO buscarCliente(String cedula) {
        return coreClient.buscarCliente(cedula);
    }

    public String realizarOperacion(VentanillaOpDTO op) {

        TransaccionCajaRequest req = new TransaccionCajaRequest();

        req.setTipoOperacion(op.getTipoOperacion());
        req.setCuentaOrigen(op.getNumeroCuentaOrigen());
        req.setCuentaDestino(op.getNumeroCuentaDestino());
        req.setMonto(op.getMonto());
        req.setDescripcion(op.getDescripcion());

        return coreClient.operar(req);
    }

    public InfoCuentaDTO validarCuenta(String numero) {
        return coreClient.validarCuenta(numero);
    }

    public void cambiarEstadoCuenta(String cuenta, String estado) {
        coreClient.cambiarEstadoCuenta(cuenta, estado);
    }

    public void activarCuenta(String cuenta) {
        coreClient.cambiarEstadoCuenta(cuenta, "ACTIVA");
    }

    public void cambiarEstadoCliente(String cedula, String estado) {
        coreClient.cambiarEstadoCliente(cedula, estado);
    }

    public void eliminarCuenta(String cuenta) {
        coreClient.eliminarCuenta(cuenta);
    }

    public void iniciarDevolucion(String originalTxId, String motivo) {
        java.util.Map<String, String> payload = new java.util.HashMap<>();
        payload.put("originalTxId", originalTxId);
        payload.put("motivo", motivo);
        coreClient.iniciarDevolucion(payload);
    }
}