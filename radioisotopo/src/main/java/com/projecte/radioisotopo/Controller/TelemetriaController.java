package com.projecte.radioisotopo.Controller;

    import com.projecte.radioisotopo.Model.Telemetria;
    import com.projecte.radioisotopo.Service.TelemetriaService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.projecte.radioisotopo.DTO.TelemetriaDTO;

    @RestController
    @RequestMapping("/api")
    public class TelemetriaController {

        @Autowired
        private TelemetriaService telemetriaService;

        // Recibir datos del Reloj
        @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
        @PostMapping(value = "/telemetria", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<String> guardar(@Valid @RequestBody TelemetriaDTO dto) {
            return ResponseEntity.status(HttpStatus.CREATED).body(telemetriaService.registrarDato(dto));
        }

        // Ver todo el historial 
        @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
        @GetMapping(value = "/telemetria", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<String> obtenerHistorial() {
            return ResponseEntity.ok(telemetriaService.obtenerTodas());
        }
        
    }