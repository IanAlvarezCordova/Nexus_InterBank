//ubi: src/main/java/com/ecusol/ventanilla/dto/ResumenClienteDTO.java
package com.ecusol.ventanilla.dto;
import lombok.Data;
import java.util.List;

@Data
public class ResumenClienteDTO {
    private Integer clienteId;
    private String nombres;
    private String cedula;
    private String estado;
    private List<CuentaResumenDTO> cuentas;
}