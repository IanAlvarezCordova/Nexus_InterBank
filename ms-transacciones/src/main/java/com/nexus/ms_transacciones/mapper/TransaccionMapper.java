package com.nexus.ms_transacciones.mapper;

import com.nexus.ms_transacciones.dto.*;
import com.nexus.ms_transacciones.model.Transaccion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TransaccionMapper {

    @Mapping(target = "transaccionId", ignore = true)
    @Mapping(target = "estado", constant = "PENDING")
    @Mapping(target = "rolTransaccion", constant = "DEBITO")
    @Mapping(target = "idBancoOrigen", constant = "2")
    @Mapping(target = "idBancoDestino", source = "bancoDestinoId")
    @Mapping(target = "instructionId", ignore = true)
    @Mapping(target = "version", ignore = true)
    Transaccion solicitudToEntity(SolicitudTransferenciaDTO dto);

    @Mapping(target = "idInstruccion", source = "instructionId")
    @Mapping(target = "endToEnd", source = "referencia")
    @Mapping(target = "estadoActual", source = "estado")
    @Mapping(target = "mensaje", source = "descripcion")
    SwitchTransaccionDTO entityToSwitchDto(Transaccion tx);

    @Mapping(target = "instructionId", source = "instructionId")
    MovimientoDTO entityToMovimientoDto(Transaccion tx);

    List<MovimientoDTO> entityListToMovimientoDtoList(List<Transaccion> txs);

    @Mapping(target = "transaccionId", ignore = true)
    @Mapping(target = "instructionId", source = "idInstruccion")
    @Mapping(target = "referencia", source = "endToEnd")
    @Mapping(target = "estado", source = "estadoActual")
    @Mapping(target = "descripcion", source = "mensaje")
    @Mapping(target = "rolTransaccion", constant = "CREDITO")
    @Mapping(target = "version", ignore = true)
    Transaccion switchDtoToEntity(SwitchTransaccionDTO dto);

    @Mapping(target = "idTransaccion", source = "transaccionId")
    @Mapping(target = "fechaHora", source = "fechaEjecucion")
    @Mapping(target = "mensaje", source = "descripcion")
    RespuestaTransferenciaDTO entityToRespuestaDto(Transaccion transaccion);
}