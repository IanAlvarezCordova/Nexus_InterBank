package com.ecusol.ventanilla.service;

import com.ecusol.ventanilla.client.CoreClient;
import com.ecusol.ventanilla.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentanillaService {

    @Autowired private CoreClient coreClient;

    public ResumenClienteDTO buscarCliente(String cedula) {
        return coreClient.buscarCliente(cedula);
    }

    public String realizarOperacion(VentanillaOpDTO op) {
        // --- CORRECCIÓN DE MAPEO (CRÍTICO) ---
        // Convertimos del DTO del Frontend al DTO del Core
        TransaccionCajaRequest req = new TransaccionCajaRequest();
        
        req.setTipoOperacion(op.getTipoOperacion());
        req.setCuentaOrigen(op.getNumeroCuentaOrigen());   // Mapeo explícito
        req.setCuentaDestino(op.getNumeroCuentaDestino()); // Mapeo explícito
        req.setMonto(op.getMonto());
        req.setDescripcion(op.getDescripcion());

        // Enviamos el objeto correcto al cliente HTTP
        return coreClient.operar(req);
    }
    
    public InfoCuentaDTO validarCuenta(String numero) {
        return coreClient.validarCuenta(numero);
    }

    // --- FUNCIONES ADMINISTRATIVAS ---

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
}