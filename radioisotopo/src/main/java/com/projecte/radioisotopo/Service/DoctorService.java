package com.projecte.radioisotopo.Service;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projecte.radioisotopo.Model.Doctor;
import com.projecte.radioisotopo.Model.Departamento;
import com.projecte.radioisotopo.DTO.DoctorDTO;
import com.projecte.radioisotopo.Repository.DoctorRepository;
import com.projecte.radioisotopo.Repository.DepartamentoRepository;


import ca.uhn.fhir.parser.IParser;

@Service
public class DoctorService {

    @Autowired 
    DoctorRepository doctorRepository;

    @Autowired
    DepartamentoRepository departamentoRepository;

    @Autowired
    private IParser fhirParser;

    // create
    public String crearDoctor(DoctorDTO dto){
        Doctor newDoctor = new Doctor();
        newDoctor.setNombre(dto.nombre());
        newDoctor.setApellido(dto.apellido());
        newDoctor.setNumColegiado(dto.numColegiado());
        newDoctor.setFotoPerfil(dto.fotoPerfil());
        newDoctor.setActivo(true);
        if (dto.departamentoId() != null) {
            Departamento d = departamentoRepository.findById(dto.departamentoId()).orElse(null);
            newDoctor.setDepartamento(d);
        }
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
    public String actualizarDoctor(Long id, DoctorDTO dto) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(id);
        
        if (doctorOpt.isPresent()) {
            Doctor doctorExistente = doctorOpt.get();
            
            doctorExistente.setNombre(dto.nombre());
            doctorExistente.setApellido(dto.apellido());
            doctorExistente.setNumColegiado(dto.numColegiado());
            doctorExistente.setFotoPerfil(dto.fotoPerfil());
            // Actualizamos el departamento asignado
            if (dto.departamentoId() != null) {
                Departamento d = departamentoRepository.findById(dto.departamentoId()).orElse(null);
                doctorExistente.setDepartamento(d);
            } else {
                doctorExistente.setDepartamento(null);
            }
           
            
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

    // Obtenir les données du docteur par son ID utilisateur
    public String obtenerDoctorPorIdUsuario(Long idUsuario) {
        Optional<Doctor> doctorOpt = doctorRepository.findByIdUsuario(idUsuario);
        if (doctorOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(doctorOpt.get()));
        }
        return null;
    }

    // Obtenir l'ID du docteur par son ID utilisateur
    public Long obtenerIdDoctorPorIdUsuario(Long idUsuario) {
        Optional<Doctor> doctorOpt = doctorRepository.findByIdUsuario(idUsuario);
        return doctorOpt.map(Doctor::getId).orElse(null);
    }

    // Obtener todos los doctores incluyendo eliminados (para ADMIN)
    public String obtenerTodosLosDoctoresIncluyendoEliminados() {
        List<Doctor> doctores = doctorRepository.findAllIncludingInactive();
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Doctor d : doctores) {
            bundle.addEntry().setResource(convertirAFhir(d));
        }
        return fhirParser.encodeResourceToString(bundle);
    }

    // Obtener solo los doctores eliminados (para ADMIN)
    public String obtenerDoctoresEliminados() {
        List<Doctor> doctores = doctorRepository.findAllInactive();
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Doctor d : doctores) {
            bundle.addEntry().setResource(convertirAFhir(d));
        }
        return fhirParser.encodeResourceToString(bundle);
    }

    // Obtener doctor por ID incluyendo eliminados (para ADMIN)
    public String obtenerDoctorPorIdIncluyendoEliminado(Long id) {
        Optional<Doctor> doctorOpt = doctorRepository.findByIdIncludingInactive(id);
        if (doctorOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(doctorOpt.get()));
        }
        return null;
    }

    // TRADUCTOR FHIR: Doctor (Java) -> Practitioner (FHIR)
    private Practitioner convertirAFhir(Doctor miDoctor) {
        Practitioner fhirPractitioner = new Practitioner();
        // Asignar el ID de la base de datos al recurso FHIR
        fhirPractitioner.setId(miDoctor.getId().toString());
        
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
            
        // foto
        if (miDoctor.getFotoPerfil() != null) {
            fhirPractitioner.addPhoto()
                .setUrl(miDoctor.getFotoPerfil())
                .setContentType("image/jpeg"); // Ou selon ton format standard
        }
        // Relación con el Departamento (Organization)
        if (miDoctor.getDepartamento() != null) {
            // Creamos la referencia al departamento
            Reference refOrg = new Reference();
            refOrg.setReference("Organization/" + miDoctor.getDepartamento().getId());
            refOrg.setDisplay(miDoctor.getDepartamento().getNombre());
        }

        return fhirPractitioner;
    }
}
