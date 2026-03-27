package com.projecte.radioisotopo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.projecte.radioisotopo.Model.Doctor;
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

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/doctores", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllDoctores() {
        return ResponseEntity.ok(doctorService.obtenerTodosLosDoctoresFhir());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(value = "/doctores/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDoctorById(@PathVariable Long id) {
        String fhirJson = doctorService.obtenerDoctorFhirPorId(id);
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
}
