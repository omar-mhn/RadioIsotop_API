package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.Model.Familiar;
import com.projecte.radioisotopo.Service.FamiliarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FamiliarController {

    @Autowired
    private FamiliarService familiarService;

    // 1. CREATE - POST /api/familiares
    @PostMapping(value = "/familiares", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crearFamiliar(@RequestBody Familiar nuevoFamiliar) {
        String fhirJson = familiarService.crearFamiliar(nuevoFamiliar);
        return ResponseEntity.status(HttpStatus.CREATED).body(fhirJson);
    }

    // 2. READ ALL - GET /api/familiares
    @GetMapping(value = "/familiares", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerTodos() {
        return ResponseEntity.ok(familiarService.obtenerTodos());
    }

    // 3. READ ONE - GET /api/familiares/{id}
    @GetMapping(value = "/familiares/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerPorId(@PathVariable Long id) {
        String fhirJson = familiarService.obtenerPorId(id);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    // 4. UPDATE - PUT /api/familiares/{id}
    @PutMapping(value = "/familiares/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody Familiar detalles) {
        String fhirJson = familiarService.actualizarFamiliar(id, detalles);
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    // 5. DELETE - DELETE /api/familiares/{id}
    @DeleteMapping("/familiares/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (familiarService.eliminarFamiliar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}