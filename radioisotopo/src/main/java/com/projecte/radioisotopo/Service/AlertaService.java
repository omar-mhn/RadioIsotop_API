package com.projecte.radioisotopo.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DetectedIssue;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projecte.radioisotopo.Model.Alerta;
import com.projecte.radioisotopo.Model.Tipo;
import com.projecte.radioisotopo.Model.Tratamiento;
import com.projecte.radioisotopo.DTO.AlertaDTO;
import com.projecte.radioisotopo.Repository.AlertaRepository;
import com.projecte.radioisotopo.Repository.TratamientoRepository;

import ca.uhn.fhir.parser.IParser;

@Service
public class AlertaService {

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private IParser fhirParser;

    // Crear una nueva alerta
    public String crearAlerta(AlertaDTO dto) {
        Alerta alerta = new Alerta();
        alerta.setTipo(Tipo.valueOf(dto.tipo().toUpperCase()));
        alerta.setMensaje(dto.mensaje());
        alerta.setFechaHora(Timestamp.from(Instant.now()));
        alerta.setActivo(true);

        if (dto.tratamientoId() != null) {
            Tratamiento t = tratamientoRepository.findById(dto.tratamientoId()).orElse(null);
            alerta.setTratamiento(t);
        }

        Alerta guardada = alertaRepository.save(alerta);
        return fhirParser.encodeResourceToString(convertirAFhir(guardada));
    }

    // Obtener todas las alertas
    public String obtenerTodas() {
        List<Alerta> alertas = alertaRepository.findAll();
        return crearBundle(alertas);
    }

    // Obtener alerta por ID
    public String obtenerPorId(Long id) {
        Optional<Alerta> alertaOpt = alertaRepository.findById(id);
        if (alertaOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(alertaOpt.get()));
        }
        return null;
    }

    // Obtener alertas por tratamiento
    public String obtenerPorTratamiento(Long tratamientoId) {
        List<Alerta> alertas = alertaRepository.findByTratamientoId(tratamientoId);
        return crearBundle(alertas);
    }

    // Obtener alertas por paciente
    public String obtenerPorPaciente(Long pacienteId) {
        List<Alerta> alertas = alertaRepository.findByTratamientoPacienteId(pacienteId);
        return crearBundle(alertas);
    }

    // Obtener alertas por tipo
    public String obtenerPorTipo(String tipo) {
        List<Alerta> alertas = alertaRepository.findByTipo(Tipo.valueOf(tipo.toUpperCase()));
        return crearBundle(alertas);
    }

    // Eliminar alerta (soft delete)
    public boolean eliminar(Long id) {
        if (alertaRepository.existsById(id)) {
            alertaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Obtener todas incluyendo eliminadas (para ADMIN)
    public String obtenerTodasIncluyendoEliminadas() {
        List<Alerta> alertas = alertaRepository.findAllIncludingInactive();
        return crearBundle(alertas);
    }

    // Obtener solo eliminadas (para ADMIN)
    public String obtenerEliminadas() {
        List<Alerta> alertas = alertaRepository.findAllInactive();
        return crearBundle(alertas);
    }

    // Obtener por ID incluyendo eliminadas (para ADMIN)
    public String obtenerPorIdIncluyendoEliminada(Long id) {
        Optional<Alerta> alertaOpt = alertaRepository.findByIdIncludingInactive(id);
        if (alertaOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(alertaOpt.get()));
        }
        return null;
    }

    // Helper para crear Bundle FHIR
    private String crearBundle(List<Alerta> alertas) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Alerta a : alertas) {
            bundle.addEntry().setResource(convertirAFhir(a));
        }
        return fhirParser.encodeResourceToString(bundle);
    }

    // TRADUCTOR FHIR: Alerta -> DetectedIssue
    private DetectedIssue convertirAFhir(Alerta alerta) {
        DetectedIssue issue = new DetectedIssue();
        issue.setId(alerta.getId().toString());

        // Tipo de alerta
        CodeableConcept code = new CodeableConcept();
        if (alerta.getTipo() == Tipo.ALERTA) {
            code.addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                .setCode("ALRT")
                .setDisplay("Alert");
            issue.setSeverity(DetectedIssue.DetectedIssueSeverity.HIGH);
        } else {
            code.addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                .setCode("REC")
                .setDisplay("Recommendation");
            issue.setSeverity(DetectedIssue.DetectedIssueSeverity.LOW);
        }
        issue.setCode(code);

        // Mensaje
        issue.setDetail(alerta.getMensaje());

        // Fecha
        issue.setIdentified(new org.hl7.fhir.r4.model.DateTimeType(alerta.getFechaHora()));

        // Referencia al paciente (a través del tratamiento)
        if (alerta.getTratamiento() != null && alerta.getTratamiento().getPaciente() != null) {
            issue.setPatient(new Reference("Patient/" + alerta.getTratamiento().getPaciente().getId()));
        }

        // Estado
        issue.setStatus(DetectedIssue.DetectedIssueStatus.FINAL);

        return issue;
    }
}
