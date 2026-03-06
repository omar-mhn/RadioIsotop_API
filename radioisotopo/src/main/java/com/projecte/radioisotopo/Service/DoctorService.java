package com.projecte.radioisotopo.Service;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projecte.radioisotopo.Model.Doctor;
import com.projecte.radioisotopo.Repository.DoctorRepository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service
public class DoctorService {

    @Autowired 
    DoctorRepository doctorRepository;

    private final IParser fhirParser;

    
    public DoctorService(){
        FhirContext ctx = FhirContext.forR4();
        this.fhirParser = ctx.newJsonParser().setPrettyPrint(true);
    }
    // create
    public String crearDoctor(Doctor newDoctor){
        Doctor doctorGuardado = doctorRepository.save(newDoctor);
        return fhirParser.encodeResourceToString(convertirAFhir(doctorGuardado));
    }
    // read all
    public String obtenerTodosLosDoctoresFhir() {
        List<Doctor> doctores = doctorRepository.findAll();
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Doctor d : doctores) {
            bundle.addEntry().setResource(convertirAFhir(d));
        }
        return fhirParser.encodeResourceToString(bundle);
    }
    // read one
    public String obtenerDoctorFhirPorId(Long id) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(id);
        if (doctorOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(doctorOpt.get()));
        }
        return null;
    }
    // update
    public String actualizarDoctor(Long id, Doctor detallesActualizados) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(id);
        
        if (doctorOpt.isPresent()) {
            Doctor doctorExistente = doctorOpt.get();
            
            doctorExistente.setNombre(detallesActualizados.getNombre());
            doctorExistente.setApellido(detallesActualizados.getApellido());
            doctorExistente.setEmail(detallesActualizados.getEmail());
            doctorExistente.setNumColegiado(detallesActualizados.getNumColegiado());
            doctorExistente.setRol(detallesActualizados.getRol());
           
            
            Doctor doctorActualizado = doctorRepository.save(doctorExistente);
            return fhirParser.encodeResourceToString(convertirAFhir(doctorActualizado));
        }
        return null;
    }

    // delete
    public boolean eliminarDoctor(Long id) {
        if (doctorRepository.existsById(id)) {
            doctorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // TRADUCTOR FHIR: Doctor (Java) -> Practitioner (FHIR)
    private Practitioner convertirAFhir(Doctor miDoctor) {
        Practitioner fhirPractitioner = new Practitioner();
        
        // Número de Colegiado (Licencia Médica)
        if (miDoctor.getNumColegiado() != null) {
            fhirPractitioner.addIdentifier()
                .setSystem("urn:system:colegio-medico-espana") // Identificador ficticio para el colegio médico
                .setValue(miDoctor.getNumColegiado());
        }
            
        // Nombre Completo
        fhirPractitioner.addName()
            .addGiven(miDoctor.getNombre())
            .setFamily(miDoctor.getApellido());
            
        // Email
        if (miDoctor.getEmail() != null) {
            fhirPractitioner.addTelecom()
                .setSystem(ContactPoint.ContactPointSystem.EMAIL)
                .setValue(miDoctor.getEmail());
        }

        return fhirPractitioner;
    }
}
