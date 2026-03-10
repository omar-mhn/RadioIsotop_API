package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.Model.Telemetria;
import com.projecte.radioisotopo.Service.TelemetriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TelemetriaController {

    @Autowired
    private TelemetriaService telemetriaService;

    // Recibir datos del Reloj
    @PostMapping(value = "/telemetria", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> guardar(@RequestBody Telemetria t) {
        return ResponseEntity.status(HttpStatus.CREATED).body(telemetriaService.registrarDato(t));
    }

    // Ver todo el historial 
    @GetMapping(value = "/telemetria", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerHistorial() {
        return ResponseEntity.ok(telemetriaService.obtenerTodas());
    }
}