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
import com.projecte.radioisotopo.DTO.DoctorDTO;
import com.projecte.radioisotopo.Service.DoctorService;

@RestController
@RequestMapping("/api")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/doctores", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crearDoctor(@Valid @RequestBody DoctorDTO dto) {
        String fhirJson = doctorService.crearDoctor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Doctor Creado !");
    }

    // Seulement ADMIN peut voir tous les docteurs
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/doctores", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllDoctores() {
        return ResponseEntity.ok(doctorService.obtenerTodosLosDoctoresFhir());
    }

    // ADMIN peut voir n'importe quel docteur par ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/doctores/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDoctorById(@PathVariable Long id) {
        String fhirJson = doctorService.obtenerDoctorFhirPorId(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint pour que le docteur puisse voir ses propres données
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping(value = "/doctores/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMisDatos(@AuthenticationPrincipal Usuario usuario) {
        String fhirJson = doctorService.obtenerDoctorPorIdUsuario(usuario.getId());
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/doctores/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizarDoctor(@PathVariable Long id, @Valid @RequestBody DoctorDTO dto) {
        String fhirJson = doctorService.actualizarDoctor(id, dto);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/doctores/{id}")
    public ResponseEntity<Void> eliminarDoctor(@PathVariable Long id) {
        if (doctorService.eliminarDoctor(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoints pour ADMIN - voir les enregistrements supprimés

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/doctores/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllDoctoresIncluyendoEliminados() {
        return ResponseEntity.ok(doctorService.obtenerTodosLosDoctoresIncluyendoEliminados());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/doctores/eliminados", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDoctoresEliminados() {
        return ResponseEntity.ok(doctorService.obtenerDoctoresEliminados());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/doctores/todos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDoctorByIdIncluyendoEliminado(@PathVariable Long id) {
        String fhirJson = doctorService.obtenerDoctorPorIdIncluyendoEliminado(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }
}
