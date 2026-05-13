package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.Model.Reloj;
import com.projecte.radioisotopo.Model.Usuario;
import com.projecte.radioisotopo.DTO.RelojDTO;
import jakarta.validation.Valid;
import com.projecte.radioisotopo.Service.RelojService;
import com.projecte.radioisotopo.Service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RelojController {

    @Autowired
    private RelojService relojService;

    @Autowired
    private PacienteService pacienteService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/relojes",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crearReloj(@Valid @RequestBody RelojDTO dto) {
        String fhirJson = relojService.crearReloj(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(fhirJson);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/relojes",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodos() {
        return ResponseEntity.ok(relojService.obtenerTodos());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/relojes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorId(@PathVariable Long id) {
        String fhirJson = relojService.obtenerPorId(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PutMapping(value = "/relojes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizar(@PathVariable Long id, @Valid @RequestBody RelojDTO dto) {
        String fhirJson = relojService.actualizarReloj(id, dto);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/relojes/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (relojService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // PACIENTE: obtener el reloj vinculado a su propio tratamiento
    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping(value = "/relojes/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMiReloj(@AuthenticationPrincipal Usuario usuario) {
        Long pacienteId = pacienteService.obtenerIdPacientePorIdUsuario(usuario.getId());
        if (pacienteId == null) return ResponseEntity.notFound().build();
        String fhirJson = relojService.obtenerPorPaciente(pacienteId);
        if (fhirJson == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(fhirJson);
    }

    // FAMILIAR: obtener el reloj del paciente vinculado
    @PreAuthorize("hasRole('FAMILIAR')")
    @GetMapping(value = "/relojes/mis-pacientes/{pacienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRelojMiPaciente(
            @PathVariable Long pacienteId, @AuthenticationPrincipal Usuario usuario) {
        if (!pacienteService.familiarTieneAccesoAPaciente(usuario.getId(), pacienteId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        String fhirJson = relojService.obtenerPorPaciente(pacienteId);
        if (fhirJson == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(fhirJson);
    }

    // Obtener relojes disponibles
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/relojes/disponibles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerDisponibles() {
        return ResponseEntity.ok(relojService.obtenerDisponibles());
    }

    // Obtener relojes con batería baja
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/relojes/bateria-baja", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerBateriaBaja(@RequestParam(defaultValue = "20") Integer nivel) {
        return ResponseEntity.ok(relojService.obtenerBateriaBaja(nivel));
    }

    // ADMIN: Obtener todos incluyendo eliminados
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/relojes/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodosIncluyendoEliminados() {
        return ResponseEntity.ok(relojService.obtenerTodosIncluyendoEliminados());
    }

    // ADMIN: Obtener solo eliminados
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/relojes/eliminados", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerEliminados() {
        return ResponseEntity.ok(relojService.obtenerEliminados());
    }

    // ADMIN: Obtener por ID incluyendo eliminado
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/relojes/todos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorIdIncluyendoEliminado(@PathVariable Long id) {
        String fhirJson = relojService.obtenerPorIdIncluyendoEliminado(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }
}