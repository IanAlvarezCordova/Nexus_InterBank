package com.nexus.ms_clientes.service;

import com.nexus.ms_clientes.dto.RegistroClientePersonaDTO;
import com.nexus.ms_clientes.dto.PersonaDTO;
import com.nexus.ms_clientes.exception.ClienteCreacionException;
import com.nexus.ms_clientes.exception.ClienteYaRegistradoException;
import com.nexus.ms_clientes.exception.ClienteNoEncontradoException;
import com.nexus.ms_clientes.mapper.PersonaMapper;
import com.nexus.ms_clientes.model.Persona;
import com.nexus.ms_clientes.repository.PersonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;

    public ClienteService(PersonaRepository personaRepository, PersonaMapper personaMapper) {
        this.personaRepository = personaRepository;
        this.personaMapper = personaMapper;
    }

    @Transactional
    public Integer crearClientePersona(RegistroClientePersonaDTO dto) {

        final String cedula = dto.getCedula();

        log.info("Intentando crear cliente persona con identificación={}", cedula);

        if (personaRepository.findByNumeroIdentificacion(cedula).isPresent()) {
            log.warn("Creación rechazada: identificación duplicada={}", cedula);
            throw new ClienteYaRegistradoException(cedula);
        }

        try {
            Persona persona = personaMapper.toEntity(dto);
            persona.setEstado("ACTIVO");
            persona.setTipoCliente("P");
            if (persona.getFechaRegistro() == null)
                persona.setFechaRegistro(java.time.LocalDate.now());

            Persona personaGuardada = personaRepository.save(persona);

            log.info("Cliente persona creado correctamente. clienteId={}, identificación={}",
                    personaGuardada.getClienteId(), cedula);

            return personaGuardada.getClienteId();

        } catch (ClienteYaRegistradoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado creando cliente persona. identificación={}", cedula, e);
            throw new ClienteCreacionException("No se pudo crear el cliente persona", e);
        }
    }

    public PersonaDTO obtenerPorId(Integer id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con id " + id));
        return personaMapper.toDTO(persona);
    }

    /**
     * Buscar cliente por número de identificación (cédula)
     */
    public PersonaDTO buscarPorCedula(String cedula) {
        log.info("Buscando cliente por cédula: {}", cedula);
        Persona persona = personaRepository.findByNumeroIdentificacion(cedula)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con cédula " + cedula));
        return personaMapper.toDTO(persona);
    }

    /**
     * Cambiar estado de un cliente (ACTIVO/INACTIVO/BLOQUEADO)
     */
    @Transactional
    public void cambiarEstado(String cedula, String nuevoEstado) {
        log.info("Cambiando estado de cliente {} a {}", cedula, nuevoEstado);
        Persona persona = personaRepository.findByNumeroIdentificacion(cedula)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con cédula " + cedula));
        persona.setEstado(nuevoEstado.toUpperCase());
        personaRepository.save(persona);
        log.info("Estado actualizado correctamente para cliente {}", cedula);
    }
}