package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.Model.Usuario;
import com.projecte.radioisotopo.Service.PacienteService;
import com.projecte.radioisotopo.Service.TelemetriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.projecte.radioisotopo.DTO.TelemetriaDTO;

@RestController
@RequestMapping("/api")
public class TelemetriaController {

    @Autowired
    private TelemetriaService telemetriaService;

    @Autowired
    private PacienteService pacienteService;

    // Recibir datos del Reloj
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PostMapping(value = "/telemetria", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> guardar(@Valid @RequestBody TelemetriaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(telemetriaService.registrarDato(dto));
    }

    // Ver todo el historial (solo admin y doctor)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/telemetria", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerHistorial() {
        return ResponseEntity.ok(telemetriaService.obtenerTodas());
    }

    // Endpoint pour que le patient puisse accéder à sa propre télémétrie
    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping(value = "/telemetria/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerMiTelemetria(@AuthenticationPrincipal Usuario usuario) {
        Long pacienteId = pacienteService.obtenerIdPacientePorIdUsuario(usuario.getId());
        if (pacienteId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(telemetriaService.obtenerPorPaciente(pacienteId));
    }

    // Obtener telemetría por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/telemetria/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorId(@PathVariable Long id) {
        String fhirJson = telemetriaService.obtenerPorId(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    // Obtener telemetría por tratamiento
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/telemetria/tratamiento/{tratamientoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorTratamiento(@PathVariable Long tratamientoId) {
        return ResponseEntity.ok(telemetriaService.obtenerPorTratamiento(tratamientoId));
    }

    // Obtener telemetría por paciente (para ADMIN/DOCTOR)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/telemetria/paciente/{pacienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(telemetriaService.obtenerPorPaciente(pacienteId));
    }

    // FAMILIAR: obtener la telemetría de un paciente vinculado
    @PreAuthorize("hasRole('FAMILIAR')")
    @GetMapping(value = "/telemetria/mis-pacientes/{pacienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTelemetriaMiPaciente(
            @PathVariable Long pacienteId, @AuthenticationPrincipal Usuario usuario) {
        if (!pacienteService.familiarTieneAccesoAPaciente(usuario.getId(), pacienteId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(telemetriaService.obtenerPorPaciente(pacienteId));
    }

    // ADMIN: Obtener todos incluyendo eliminados
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/telemetria/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodosIncluyendoEliminados() {
        return ResponseEntity.ok(telemetriaService.obtenerTodosIncluyendoEliminados());
    }

    // ADMIN: Obtener solo eliminados
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/telemetria/eliminados", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerEliminados() {
        return ResponseEntity.ok(telemetriaService.obtenerEliminados());
    }

    // ADMIN: Obtener por ID incluyendo eliminado
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/telemetria/todos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorIdIncluyendoEliminado(@PathVariable Long id) {
        String fhirJson = telemetriaService.obtenerPorIdIncluyendoEliminado(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }
}