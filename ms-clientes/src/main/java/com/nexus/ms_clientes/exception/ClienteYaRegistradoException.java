package com.nexus.ms_clientes.exception;

public class ClienteYaRegistradoException extends RuntimeException {
    public ClienteYaRegistradoException(String cedula) {
        super("La cédula ya se encuentra registrada en Clientes: " + cedula);
    }
}
