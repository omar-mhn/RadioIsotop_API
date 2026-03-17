package com.projecte.radioisotopo.Controller;

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

import com.projecte.radioisotopo.Model.Paciente;
import com.projecte.radioisotopo.Service.PacienteService;


@RestController
@RequestMapping("/api")
public class PacienteController {

    @Autowired
    PacienteService pacienteService;

    @PostMapping(value = "/pacientes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> crearPaciente(@RequestBody Paciente nuevoPaciente) {
        String fhirJson = pacienteService.createPacient(nuevoPaciente);
        return ResponseEntity.status(HttpStatus.CREATED).body("patient created");
    }

    @GetMapping(value = "/pacientes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllPacientes() {
        return ResponseEntity.ok(pacienteService.leerTodosPacientes());
    }

    @GetMapping(value = "/pacientes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPacienteById(@PathVariable Long id) {
        String fhirJson = pacienteService.leerPacientePorId(id);
        
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build(); 
    }

    @PutMapping(value = "/pacientes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> actualizarPaciente(@PathVariable Long id, @RequestBody Paciente pacienteActualizado) {
        String fhirJson = pacienteService.actualizarPaciente(id, pacienteActualizado);
        
        if (fhirJson != null) {
            return ResponseEntity.ok(fhirJson);
        }
        return ResponseEntity.notFound().build();
    }

    
    @DeleteMapping("/pacientes/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        boolean eliminado = pacienteService.eliminarPaciente(id);
        
        if (eliminado) {
            return ResponseEntity.noContent().build(); 
        }
        return ResponseEntity.notFound().build();
    }
}
