package com.projecte.radioisotopo.Service;


import java.util.List;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projecte.radioisotopo.Model.Telemetria;
import com.projecte.radioisotopo.Model.Tratamiento;
import com.projecte.radioisotopo.DTO.TelemetriaDTO;
import com.projecte.radioisotopo.Repository.TelemetriaRepository;
import com.projecte.radioisotopo.Repository.TratamientoRepository;

import ca.uhn.fhir.parser.IParser;

@Service
public class TelemetriaService {

    @Autowired
    private TelemetriaRepository telemetriaRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private IParser fhirParser;

    // Registrar una nueva lectura de la pulsera
    public String registrarDato(TelemetriaDTO dto) {
        Telemetria t = new Telemetria();
        t.setFrecuenciaCardiaca(dto.frecuenciaCardiaca());
        t.setRadiacionActual(dto.radiacionActual());
        t.setTemperatura(dto.temperatura());
        t.setPasosAcumulados(dto.pasosAcumulados());
        t.setFechaHora(dto.fechaHora());
        
        if (dto.tratamientoId() != null) {
            t.setTratamiento(tratamientoRepository.findById(dto.tratamientoId()).orElse(null));
        }
        
        Telemetria guardado = telemetriaRepository.save(t);
        return fhirParser.encodeResourceToString(convertirAFhir(guardado));
    }

    // Obtener historial de telemetría
    public String obtenerTodas() {
        List<Telemetria> lista = telemetriaRepository.findAll();
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Telemetria t : lista) {
            bundle.addEntry().setResource(convertirAFhir(t));
        }
        return fhirParser.encodeResourceToString(bundle);
    }

    // TRADUCTOR FHIR: Telemetria -> Observation (Vital Signs)
   
    private Observation convertirAFhir(Telemetria t) {
        Observation obs = new Observation();
        obs.setId(t.getId().toString());
        obs.setStatus(Observation.ObservationStatus.FINAL);
        
        // Categoría: Signos Vitales
        obs.addCategory().addCoding()
            .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
            .setCode("vital-signs")
            .setDisplay("Vital Signs");

        // Fecha y hora de la medición
        obs.setEffective(new DateTimeType(t.getFechaHora()));

        // Vínculo con el Paciente (a través del Tratamiento)
        if (t.getTratamiento() != null && t.getTratamiento().getPaciente() != null) {
            obs.setSubject(new Reference("Patient/" + t.getTratamiento().getPaciente().getId()));
        }

        // Componente: Frecuencia Cardíaca
        if (t.getFrecuenciaCardiaca() != null) {
            Observation.ObservationComponentComponent heartRate = obs.addComponent();
            heartRate.getCode().addCoding().setSystem("http://loinc.org").setCode("8867-4").setDisplay("Heart rate");
            heartRate.setValue(new Quantity().setValue(t.getFrecuenciaCardiaca()).setUnit("bpm"));
        }

        // Componente: Temperatura
        if (t.getTemperatura() != null) {
            Observation.ObservationComponentComponent temp = obs.addComponent();
            temp.getCode().addCoding().setSystem("http://loinc.org").setCode("8310-5").setDisplay("Body temperature");
            temp.setValue(new Quantity().setValue(t.getTemperatura()).setUnit("Cel"));
        }

        // Componente: Radiación Actual (Dato clave de tu proyecto)
        Observation.ObservationComponentComponent radiation = obs.addComponent();
        radiation.getCode().addCoding().setSystem("http://loinc.org").setCode("93006-5").setDisplay("Radiation level");
        radiation.setValue(new Quantity().setValue(t.getRadiacionActual()).setUnit("µSv/h"));

        // Componente: Pasos Acumulados
        Observation.ObservationComponentComponent steps = obs.addComponent();
        steps.getCode().addCoding().setSystem("http://loinc.org").setCode("55423-8").setDisplay("Steps");
        steps.setValue(new Quantity().setValue(t.getPasosAcumulados()).setUnit("steps"));

        return obs;
    }
}