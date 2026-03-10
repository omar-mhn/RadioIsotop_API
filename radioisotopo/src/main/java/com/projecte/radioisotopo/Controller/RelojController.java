package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.Model.Reloj;
import com.projecte.radioisotopo.Service.RelojService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RelojController {

    @Autowired
    private RelojService relojService;

    
    @PostMapping(value = "/relojes",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crearReloj(@RequestBody Reloj nuevoReloj) {
        String fhirJson = relojService.crearReloj(nuevoReloj);
        return ResponseEntity.status(HttpStatus.CREATED).body(fhirJson);
    }

    
    @GetMapping(value = "/relojes",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodos() {
        return ResponseEntity.ok(relojService.obtenerTodos());
    }

    
    @GetMapping(value = "/relojes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorId(@PathVariable Long id) {
        String fhirJson = relojService.obtenerPorId(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    
    @PutMapping(value = "/relojes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody Reloj detalles) {
        String fhirJson = relojService.actualizarReloj(id, detalles);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

  
    @DeleteMapping("/relojes/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (relojService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}