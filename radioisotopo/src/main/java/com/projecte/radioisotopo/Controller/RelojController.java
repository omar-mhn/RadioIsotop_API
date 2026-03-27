package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.Model.Reloj;
import com.projecte.radioisotopo.DTO.RelojDTO;
import jakarta.validation.Valid;
import com.projecte.radioisotopo.Service.RelojService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RelojController {

    @Autowired
    private RelojService relojService;

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
}