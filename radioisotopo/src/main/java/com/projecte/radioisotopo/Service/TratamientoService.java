package com.projecte.radioisotopo.Service;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projecte.radioisotopo.Model.Tratamiento;
import com.projecte.radioisotopo.Repository.TratamientoRepository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service
public class TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    private final IParser fhirParser;

    public TratamientoService() {
        FhirContext ctx = FhirContext.forR4();
        this.fhirParser = ctx.newJsonParser().setPrettyPrint(true);
    }

    // Crear un nuevo tratamiento (CarePlan)
    public String crearTratamiento(Tratamiento t) {
        Tratamiento guardado = tratamientoRepository.save(t);
        return fhirParser.encodeResourceToString(convertirAFhir(guardado));
    }

    // Obtener todos 
    public String obtenerTodos() {
        List<Tratamiento> lista = tratamientoRepository.findAll();
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Tratamiento t : lista) {
            bundle.addEntry().setResource(convertirAFhir(t));
        }
        return fhirParser.encodeResourceToString(bundle);
    }

    // Obtener un tratamiento específico por ID
    public String obtenerPorId(Long id) {
        Optional<Tratamiento> tOpt = tratamientoRepository.findById(id);
        if (tOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(tOpt.get()));
        }
        return null;
    }

    // Actualizar el tratamiento
    public String actualizarTratamiento(Long id, Tratamiento detalles) {
        Optional<Tratamiento> tOpt = tratamientoRepository.findById(id);
        if (tOpt.isPresent()) {
            Tratamiento t = tOpt.get();
            t.setTipoIsotopo(detalles.getTipoIsotopo());
            t.setDosisInicial(detalles.getDosisInicial());
            t.setFechaAdministracion(detalles.getFechaAdministracion());
            t.setFechaFinalEstimada(detalles.getFechaFinalEstimada());
            t.setEstadoTratamiento(detalles.getEstadoTratamiento());
            t.setPaciente(detalles.getPaciente());
            t.setReloj(detalles.getReloj());
            t.setDoctor(detalles.getDoctor());
            
            return fhirParser.encodeResourceToString(convertirAFhir(tratamientoRepository.save(t)));
        }
        return null;
    }

    // Eliminar registro
    public boolean eliminar(Long id) {
        if (tratamientoRepository.existsById(id)) {
            tratamientoRepository.deleteById(id);
            return true;
        }
        return false;
    }
    // TRADUCTOR FHIR: Tratamiento (Java) -> CarePlan (FHIR)
    private CarePlan convertirAFhir(Tratamiento t) {
        CarePlan cp = new CarePlan();
        cp.setId(t.getId().toString());

        // Estado y Propósito
        cp.setStatus(CarePlan.CarePlanStatus.ACTIVE);
        cp.setIntent(CarePlan.CarePlanIntent.PLAN);
        cp.setTitle("Tratamiento de Radioisótopo: " + t.getTipoIsotopo());

        // Referencia al PACIENTE (Subject)
        if (t.getPaciente() != null) {
            Reference refPac = new Reference("Patient/" + t.getPaciente().getId());
            refPac.setDisplay(t.getPaciente().getNombre() + " " + t.getPaciente().getApellido());
            cp.setSubject(refPac);
        }

        //  Referencia al DOCTOR (Author/Responsable)
        if (t.getDoctor() != null) {
            Reference refDoc = new Reference("Practitioner/" + t.getDoctor().getId());
            refDoc.setDisplay("Dr. " + t.getDoctor().getApellido());
            cp.setAuthor(refDoc);
        }

        // Referencia al RELOJ (SupportingInfo/Device)
        if (t.getReloj() != null) {
            Reference refReloj = new Reference("Device/" + t.getReloj().getId());
            refReloj.setDisplay("Reloj IoT - IMEI: " + t.getReloj().getImei());
            cp.addSupportingInfo(refReloj);
        }

        // Período de tiempo (Administración -> Fin estimado)
        Period periodo = new Period();
        periodo.setStart(t.getFechaAdministracion());
        if (t.getFechaFinalEstimada() != null) {
            periodo.setEnd(t.getFechaFinalEstimada());
        }
        cp.setPeriod(periodo);

        //  Detalles técnicos del Isótopo (Annotation)
        Annotation nota = new Annotation();
        nota.setText("Dosis inicial administrada: " + t.getDosisInicial() + " MBq");
        cp.addNote(nota);

        return cp;
    }
}