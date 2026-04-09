package com.projecte.radioisotopo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.projecte.radioisotopo.Model.Usuario;
import com.projecte.radioisotopo.DTO.AlertaDTO;
import com.projecte.radioisotopo.Service.AlertaService;
import com.projecte.radioisotopo.Service.PacienteService;

@RestController
@RequestMapping("/api")
public class AlertaController {

    @Autowired
    private AlertaService alertaService;

    @Autowired
    private PacienteService pacienteService;

    // Crear una nueva alerta
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PostMapping(value = "/alertas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crearAlerta(@Valid @RequestBody AlertaDTO dto) {
        String fhirJson = alertaService.crearAlerta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(fhirJson);
    }

    // Obtener todas las alertas
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/alertas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodas() {
        return ResponseEntity.ok(alertaService.obtenerTodas());
    }

    // Obtener alerta por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/alertas/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorId(@PathVariable Long id) {
        String fhirJson = alertaService.obtenerPorId(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    // Obtener alertas por tratamiento
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/alertas/tratamiento/{tratamientoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorTratamiento(@PathVariable Long tratamientoId) {
        return ResponseEntity.ok(alertaService.obtenerPorTratamiento(tratamientoId));
    }

    // Obtener alertas por paciente (para ADMIN/DOCTOR)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/alertas/paciente/{pacienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(alertaService.obtenerPorPaciente(pacienteId));
    }

    // Obtener mis alertas (para PACIENTE)
    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping(value = "/alertas/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerMisAlertas(@AuthenticationPrincipal Usuario usuario) {
        Long pacienteId = pacienteService.obtenerIdPacientePorIdUsuario(usuario.getId());
        if (pacienteId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(alertaService.obtenerPorPaciente(pacienteId));
    }

    // Obtener alertas por tipo
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/alertas/tipo/{tipo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(alertaService.obtenerPorTipo(tipo));
    }

    // Eliminar alerta (soft delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/alertas/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (alertaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoints para ADMIN - ver registros eliminados

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/alertas/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodasIncluyendoEliminadas() {
        return ResponseEntity.ok(alertaService.obtenerTodasIncluyendoEliminadas());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/alertas/eliminados", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerEliminadas() {
        return ResponseEntity.ok(alertaService.obtenerEliminadas());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/alertas/todos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorIdIncluyendoEliminada(@PathVariable Long id) {
        String fhirJson = alertaService.obtenerPorIdIncluyendoEliminada(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }
}
