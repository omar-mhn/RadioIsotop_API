package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.DTO.FamiliarDTO;
import jakarta.validation.Valid;
import com.projecte.radioisotopo.Service.FamiliarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FamiliarController {

    @Autowired
    private FamiliarService familiarService;

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PostMapping(value = "/familiares", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crearFamiliar(@Valid @RequestBody FamiliarDTO dto) {
        String fhirJson = familiarService.crearFamiliar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Familiar creado !");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/familiares", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodos() {
        return ResponseEntity.ok(familiarService.obtenerTodos());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/familiares/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorId(@PathVariable Long id) {
        String fhirJson = familiarService.obtenerPorId(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PutMapping(value = "/familiares/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizar(@PathVariable Long id, @Valid @RequestBody FamiliarDTO dto) {
        String fhirJson = familiarService.actualizarFamiliar(id, dto);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/familiares/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (familiarService.eliminarFamiliar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoints pour ADMIN - voir les enregistrements supprimés

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/familiares/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllFamiliaresIncluyendoEliminados() {
        return ResponseEntity.ok(familiarService.obtenerTodosIncluyendoEliminados());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/familiares/eliminados", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFamiliaresEliminados() {
        return ResponseEntity.ok(familiarService.obtenerEliminados());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/familiares/todos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFamiliarByIdIncluyendoEliminado(@PathVariable Long id) {
        String fhirJson = familiarService.obtenerPorIdIncluyendoEliminado(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }
}