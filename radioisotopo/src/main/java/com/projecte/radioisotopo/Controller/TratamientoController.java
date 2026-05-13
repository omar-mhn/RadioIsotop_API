package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.Model.Tratamiento;
import com.projecte.radioisotopo.Model.Usuario;
import com.projecte.radioisotopo.Service.TratamientoService;
import com.projecte.radioisotopo.Service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.projecte.radioisotopo.DTO.TratamientoDTO;

@RestController
@RequestMapping("/api")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private PacienteService pacienteService;

    
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PostMapping(value = "/tratamientos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crear(@Valid @RequestBody TratamientoDTO dto) {
        String fhirJson = tratamientoService.crearTratamiento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(fhirJson);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/tratamientos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodos() {
        return ResponseEntity.ok(tratamientoService.obtenerTodos());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/tratamientos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorId(@PathVariable Long id) {
        String fhirJson = tratamientoService.obtenerPorId(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PutMapping(value = "/tratamientos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizar(@PathVariable Long id, @Valid @RequestBody TratamientoDTO dto) {
        String fhirJson = tratamientoService.actualizarTratamiento(id, dto);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/tratamientos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (tratamientoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Obtener tratamientos por paciente (ADMIN/DOCTOR)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/tratamientos/paciente/{pacienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(tratamientoService.obtenerPorPaciente(pacienteId));
    }

    // PACIENTE: obtener su propio tratamiento
    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping(value = "/tratamientos/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMiTratamiento(@AuthenticationPrincipal Usuario usuario) {
        Long pacienteId = pacienteService.obtenerIdPacientePorIdUsuario(usuario.getId());
        if (pacienteId == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(tratamientoService.obtenerPorPaciente(pacienteId));
    }

    // FAMILIAR: obtener el tratamiento de un paciente vinculado
    @PreAuthorize("hasRole('FAMILIAR')")
    @GetMapping(value = "/tratamientos/mis-pacientes/{pacienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTratamientoMiPaciente(
            @PathVariable Long pacienteId, @AuthenticationPrincipal Usuario usuario) {
        if (!pacienteService.familiarTieneAccesoAPaciente(usuario.getId(), pacienteId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(tratamientoService.obtenerPorPaciente(pacienteId));
    }

    // Obtener tratamientos por doctor
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/tratamientos/doctor/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(tratamientoService.obtenerPorDoctor(doctorId));
    }

    // Obtener tratamientos activos
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/tratamientos/activos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerActivos() {
        return ResponseEntity.ok(tratamientoService.obtenerActivos());
    }

    // ADMIN: Obtener todos incluyendo eliminados
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/tratamientos/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodosIncluyendoEliminados() {
        return ResponseEntity.ok(tratamientoService.obtenerTodosIncluyendoEliminados());
    }

    // ADMIN: Obtener solo eliminados
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/tratamientos/eliminados", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerEliminados() {
        return ResponseEntity.ok(tratamientoService.obtenerEliminados());
    }

    // ADMIN: Obtener por ID incluyendo eliminado
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/tratamientos/todos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorIdIncluyendoEliminado(@PathVariable Long id) {
        String fhirJson = tratamientoService.obtenerPorIdIncluyendoEliminado(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }
}