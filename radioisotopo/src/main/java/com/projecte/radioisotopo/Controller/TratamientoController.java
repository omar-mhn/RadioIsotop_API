package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.Model.Tratamiento;
import com.projecte.radioisotopo.Service.TratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    //Crear un nuevo tratamiento
    @PostMapping(value = "/tratamientos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crear(@RequestBody Tratamiento t) {
        String fhirJson = tratamientoService.crearTratamiento(t);
        return ResponseEntity.status(HttpStatus.CREATED).body(fhirJson);
    }

    // Ver todos los planes de tratamiento
    @GetMapping(value = "/tratamientos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodos() {
        return ResponseEntity.ok(tratamientoService.obtenerTodos());
    }

    // Ver un tratamiento por ID 
    @GetMapping(value = "/tratamientos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorId(@PathVariable Long id) {
        String fhirJson = tratamientoService.obtenerPorId(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    // Modificar un tratamiento existente 
    @PutMapping(value = "/tratamientos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody Tratamiento t) {
        String fhirJson = tratamientoService.actualizarTratamiento(id, t);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    // Borrar un tratamiento 
    @DeleteMapping("/tratamientos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (tratamientoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}