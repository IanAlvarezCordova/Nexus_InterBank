package com.ecusol.web.controller;

import com.ecusol.web.service.DataSeedingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/web/util")
@CrossOrigin(origins = "*")
public class UtilController {

    @Autowired
    private DataSeedingService seedingService;

    // Endpoint: POST http://localhost:8082/api/util/poblar-bd?cantidad=10
    @PostMapping("/poblar-bd")
    public String poblarBaseDeDatos(@RequestParam(defaultValue = "10") int cantidad) {
        if (cantidad > 100)
            return "Máximo 100 registros por petición para no saturar.";
        return seedingService.poblarBaseDeDatos(cantidad);
    }
}