package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.Model.Departamento;
import com.projecte.radioisotopo.Service.DepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DepartamentoController {

    @Autowired
    private DepartamentoService departamentoService;

    
    @PostMapping(value = "/departamentos",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crearDepartamento(@RequestBody Departamento dep) {
        String fhirJson = departamentoService.crearDepartamento(dep);
        return ResponseEntity.status(HttpStatus.CREATED).body("Departamento creado!");
    }

    @GetMapping(value = "/departamentos",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodos() {
        return ResponseEntity.ok(departamentoService.obtenerTodos());
    }

  
    @GetMapping(value = "/departamentos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorId(@PathVariable Long id) {
        String fhirJson = departamentoService.obtenerPorId(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping(value = "/departamentos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody Departamento dep) {
        String fhirJson = departamentoService.actualizarDepartamento(id, dep);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/departamentos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (departamentoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}