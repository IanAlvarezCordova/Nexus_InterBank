package com.cuentas.productos.cbs.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ApiError {

    private OffsetDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

    public ApiError() {}

    public ApiError(Integer status, String error, String message, String path) {
        this.timestamp = OffsetDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
