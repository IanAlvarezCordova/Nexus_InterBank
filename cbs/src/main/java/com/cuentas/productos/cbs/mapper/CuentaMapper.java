package com.cuentas.productos.cbs.mapper;

import com.cuentas.productos.cbs.dto.CuentaResponse;
import com.cuentas.productos.cbs.model.Cuenta;

public class CuentaMapper {

    private CuentaMapper() {
    }

    public static CuentaResponse toResponse(Cuenta cuenta) {
        CuentaResponse dto = new CuentaResponse();
        dto.setCuentaId(cuenta.getId());
        dto.setNumeroCuenta(cuenta.getNumeroCuenta());
        dto.setClienteId(cuenta.getClienteId());
        if (cuenta.getTipoCuenta() != null) {
            dto.setTipoCuentaId(cuenta.getTipoCuenta().getId());
        }
        dto.setSucursalIdApertura(cuenta.getSucursalIdApertura());
        dto.setSaldo(cuenta.getSaldo());
        dto.setFechaApertura(cuenta.getFechaApertura());
        dto.setEstado(cuenta.getEstado());
        return dto;
    }
}
