package com.nexus.ms_clientes.controller;

import com.nexus.ms_clientes.dto.RegistroClientePersonaDTO;
import com.nexus.ms_clientes.dto.PersonaDTO;
import com.nexus.ms_clientes.repository.ClienteRepository;
import com.nexus.ms_clientes.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/personas")
    public ResponseEntity<Integer> crearPersona(@RequestBody RegistroClientePersonaDTO dto) {
        try {
            Integer id = clienteService.crearClientePersona(dto);

            URI location = URI.create(String.format("/api/v1/clientes/personas/%d", id));
            return ResponseEntity.created(location).body(id);

        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pudo crear el cliente en el servicio de Clientes",
                    ex);
        }
    }

    @GetMapping("/{id}/estado")
    public ResponseEntity<String> getEstado(@PathVariable Integer id) {
        return clienteRepository.findById(id)
                .map(c -> ResponseEntity.ok(c.getEstado()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente no encontrado con id " + id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTO> obtenerCliente(@PathVariable Integer id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    /**
     * Buscar cliente por número de identificación (cédula)
     * Usado por Ventanilla para buscar clientes
     */
    @GetMapping("/buscar/{cedula}")
    public ResponseEntity<PersonaDTO> buscarPorCedula(@PathVariable String cedula) {
        return ResponseEntity.ok(clienteService.buscarPorCedula(cedula));
    }

    /**
     * Cambiar estado de un cliente (ACTIVO/INACTIVO)
     * Usado por Ventanilla para gestión administrativa
     */
    @PostMapping("/estado")
    public ResponseEntity<String> cambiarEstado(
            @RequestParam String cedula,
            @RequestParam String estado) {
        clienteService.cambiarEstado(cedula, estado);
        return ResponseEntity.ok("Estado actualizado a " + estado);
    }
}