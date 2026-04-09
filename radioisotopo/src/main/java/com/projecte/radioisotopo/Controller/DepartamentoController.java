package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.Model.Departamento;
import com.projecte.radioisotopo.DTO.DepartamentoDTO;
import jakarta.validation.Valid;
import com.projecte.radioisotopo.Service.DepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DepartamentoController {

    @Autowired
    private DepartamentoService departamentoService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/departamentos",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crearDepartamento(@Valid @RequestBody DepartamentoDTO depDTO) {
        String fhirJson = departamentoService.crearDepartamento(depDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Departamento creado!");
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PACIENTE', 'FAMILIAR')")
    @GetMapping(value = "/departamentos",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodos() {
        return ResponseEntity.ok(departamentoService.obtenerTodos());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PACIENTE', 'FAMILIAR')")
    @GetMapping(value = "/departamentos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorId(@PathVariable Long id) {
        String fhirJson = departamentoService.obtenerPorId(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/departamentos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizar(@PathVariable Long id, @Valid @RequestBody DepartamentoDTO depDTO) {
        String fhirJson = departamentoService.actualizarDepartamento(id, depDTO);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/departamentos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (departamentoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ADMIN: Obtener todos incluyendo eliminados
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/departamentos/todos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodosIncluyendoEliminados() {
        return ResponseEntity.ok(departamentoService.obtenerTodosIncluyendoEliminados());
    }

    // ADMIN: Obtener solo eliminados
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/departamentos/eliminados", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerEliminados() {
        return ResponseEntity.ok(departamentoService.obtenerEliminados());
    }

    // ADMIN: Obtener por ID incluyendo eliminado
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/departamentos/todos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorIdIncluyendoEliminado(@PathVariable Long id) {
        String fhirJson = departamentoService.obtenerPorIdIncluyendoEliminado(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }
}