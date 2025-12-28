package com.nexus.ms_transacciones.mapper;

import com.nexus.ms_transacciones.dto.*;
import com.nexus.ms_transacciones.model.Transaccion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TransaccionMapper {

    // Cliente -> Entidad
    @Mapping(target = "transaccionId", ignore = true)
    @Mapping(target = "estado", constant = "PENDING")
    @Mapping(target = "rolTransaccion", constant = "DEBITO")
    @Mapping(target = "tipo", constant = "TRANSFERENCIA")
    @Mapping(target = "idBancoOrigen", constant = "2") // Somos Banco 2 (Nexus)
    @Mapping(target = "idBancoDestino", source = "bancoDestinoId")
    @Mapping(target = "instructionId", ignore = true)
    @Mapping(target = "version", ignore = true)
    Transaccion solicitudToEntity(SolicitudTransferenciaDTO dto);

    // Entidad -> Switch JSON
    @Mapping(target = "idInstruccion", source = "instructionId")
    @Mapping(target = "endToEnd", source = "referencia")
    @Mapping(target = "estadoActual", source = "estado")
    @Mapping(target = "mensaje", source = "descripcion")
    SwitchTransaccionDTO entityToSwitchDto(Transaccion tx);

    MovimientoDTO entityToMovimientoDto(Transaccion tx);

    List<MovimientoDTO> entityListToMovimientoDtoList(List<Transaccion> txs);

    // Switch JSON -> Entidad (Entrada)
    @Mapping(target = "transaccionId", ignore = true)
    @Mapping(target = "instructionId", source = "idInstruccion")
    @Mapping(target = "referencia", source = "endToEnd")
    @Mapping(target = "estado", source = "estadoActual")
    @Mapping(target = "descripcion", source = "mensaje")
    @Mapping(target = "rolTransaccion", constant = "CREDITO")
    @Mapping(target = "tipo", constant = "TRANSFERENCIA")
    @Mapping(target = "version", ignore = true)
    Transaccion switchDtoToEntity(SwitchTransaccionDTO dto);

    // Entidad -> Respuesta Cliente
    @Mapping(target = "idTransaccion", source = "transaccionId")
    @Mapping(target = "fechaHora", source = "fechaEjecucion")
    @Mapping(target = "mensaje", source = "descripcion")
    RespuestaTransferenciaDTO entityToRespuestaDto(Transaccion transaccion);
}