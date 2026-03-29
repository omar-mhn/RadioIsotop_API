package com.projecte.radioisotopo.Controller;

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

import com.projecte.radioisotopo.Model.Usuario;
import com.projecte.radioisotopo.DTO.PacienteDTO;
import com.projecte.radioisotopo.Service.PacienteService;


@RestController
@RequestMapping("/api")
public class PacienteController {

    @Autowired
    PacienteService pacienteService;

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PostMapping(value = "/pacientes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crearPaciente(@Valid @RequestBody PacienteDTO dto) {
        String fhirJson = pacienteService.createPacient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("patient created");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/pacientes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllPacientes() {
        return ResponseEntity.ok(pacienteService.leerTodosPacientes());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/pacientes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPacienteById(@PathVariable Long id) {
        String fhirJson = pacienteService.leerPacientePorId(id);
        
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build(); 
    }

    // Endpoint pour que le patient puisse accéder à ses propres données
    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping(value = "/pacientes/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMisDatos(@AuthenticationPrincipal Usuario usuario) {
        String fhirJson = pacienteService.leerPacientePorIdUsuario(usuario.getId());
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint pour que le familiar puisse accéder aux données de ses patients
    @PreAuthorize("hasRole('FAMILIAR')")
    @GetMapping(value = "/pacientes/mis-pacientes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMisPacientes(@AuthenticationPrincipal Usuario usuario) {
        String fhirJson = pacienteService.leerPacientesPorFamiliar(usuario.getId());
        return ResponseEntity.ok(fhirJson);
    }

    // Endpoint pour que le familiar puisse accéder à un patient spécifique (s'il y a accès)
    @PreAuthorize("hasRole('FAMILIAR')")
    @GetMapping(value = "/pacientes/mis-pacientes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPacienteByIdParaFamiliar(
            @PathVariable Long id, 
            @AuthenticationPrincipal Usuario usuario) {
        if (!pacienteService.familiarTieneAccesoAPaciente(usuario.getId(), id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String fhirJson = pacienteService.leerPacientePorId(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PutMapping(value = "/pacientes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizarPaciente(@PathVariable Long id, @Valid @RequestBody PacienteDTO dto) {
        String fhirJson = pacienteService.actualizarPaciente(id, dto);
        
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/pacientes/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        boolean eliminado = pacienteService.eliminarPaciente(id);
        
        if (eliminado) {
            return ResponseEntity.noContent().build(); 
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoints pour ADMIN - voir les enregistrements supprimés

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/pacientes/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllPacientesIncluyendoEliminados() {
        return ResponseEntity.ok(pacienteService.leerTodosPacientesIncluyendoEliminados());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/pacientes/eliminados", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPacientesEliminados() {
        return ResponseEntity.ok(pacienteService.leerPacientesEliminados());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/pacientes/todos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPacienteByIdIncluyendoEliminado(@PathVariable Long id) {
        String fhirJson = pacienteService.leerPacientePorIdIncluyendoEliminado(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }
}
