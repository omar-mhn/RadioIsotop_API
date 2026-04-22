package com.projecte.radioisotopo.Service;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projecte.radioisotopo.Model.Familiar;
import com.projecte.radioisotopo.Model.Paciente;
import com.projecte.radioisotopo.Model.Departamento;
import com.projecte.radioisotopo.DTO.PacienteDTO;
import com.projecte.radioisotopo.Repository.PacienteRepository;
import com.projecte.radioisotopo.Repository.FamiliarRepository;
import com.projecte.radioisotopo.Repository.DepartamentoRepository;
import java.util.ArrayList;

import ca.uhn.fhir.parser.IParser;

@Service
public class PacienteService {

    @Autowired 
    PacienteRepository pacienteRepository;

    @Autowired
    FamiliarRepository familiarRepository;

    @Autowired
    DepartamentoRepository departamentoRepository;

    @Autowired
    private IParser fhirParser;

    // Crear un nuevo paciente
    public String createPacient(PacienteDTO dto){
        Paciente nuevoPaciente = new Paciente();
        nuevoPaciente.setNombre(dto.nombre());
        nuevoPaciente.setApellido(dto.apellido());
        nuevoPaciente.setNumTelefono(dto.numTelefono());
        nuevoPaciente.setNumDocumento(dto.numDocumento());
        nuevoPaciente.setTipoDocumento(dto.tipoDocumento());
        nuevoPaciente.setTarjetaSanitaria(dto.tarjetaSanitaria());
        nuevoPaciente.setFechaNacimiento(dto.fechaNacimiento());
        nuevoPaciente.setPeso(dto.peso());
        nuevoPaciente.setAltura(dto.altura());
        nuevoPaciente.setActivo(true);

        if (dto.departamentoId() != null) {
            Departamento d = departamentoRepository.findById(dto.departamentoId()).orElse(null);
            nuevoPaciente.setDepartamento(d);
        }
        
        if (dto.familiarIds() != null && !dto.familiarIds().isEmpty()) {
            List<Familiar> familiares = familiarRepository.findAllById(dto.familiarIds());
            nuevoPaciente.setFamiliares(familiares);
        }

        Paciente pacienteGuardado = pacienteRepository.save(nuevoPaciente);
        // Lo devolvemos traducido al mundo FHIR
        return fhirParser.encodeResourceToString(convertirAFhir(pacienteGuardado));
    }



    // Leer TODOS los pacientes
    public String leerTodosPacientes(){
        // Cuando enviamos una lista, FHIR obliga a meterlos en una "Caja" llamada Bundle
        List<Paciente>pacientes = pacienteRepository.findAll();
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);

        for(Paciente p : pacientes){
            bundle.addEntry().setResource(convertirAFhir(p)); // Metemos cada paciente en la caja
        }
        return fhirParser.encodeResourceToString(bundle);
    }

    // Leer un paciente por su ID
    public String leerPacientePorId(Long id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);

        if(paciente.isPresent()){
            return fhirParser.encodeResourceToString(convertirAFhir(paciente.get()));
        }
        return null;
    }

    // Actualizar un paciente

    public String actualizarPaciente(Long id, PacienteDTO dto) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        
        if (paciente.isPresent()) {
            Paciente pacienteExistente = paciente.get();
            
            // Actualizamos los datos locales
            pacienteExistente.setNombre(dto.nombre());
            pacienteExistente.setApellido(dto.apellido());
            pacienteExistente.setNumTelefono(dto.numTelefono());
            pacienteExistente.setNumDocumento(dto.numDocumento());
            pacienteExistente.setTipoDocumento(dto.tipoDocumento());
            pacienteExistente.setTarjetaSanitaria(dto.tarjetaSanitaria());
            pacienteExistente.setFechaNacimiento(dto.fechaNacimiento());
            pacienteExistente.setPeso(dto.peso());
            pacienteExistente.setAltura(dto.altura());
             
            // Actualizamos el departamento y los familiares
            if (dto.departamentoId() != null) {
                Departamento d = departamentoRepository.findById(dto.departamentoId()).orElse(null);
                pacienteExistente.setDepartamento(d);
            } else {
                pacienteExistente.setDepartamento(null);
            }

            if (dto.familiarIds() != null && !dto.familiarIds().isEmpty()) {
                List<Familiar> familiares = familiarRepository.findAllById(dto.familiarIds());
                pacienteExistente.setFamiliares(familiares);
            } else {
                pacienteExistente.setFamiliares(new ArrayList<>());
            }
            // Guardamos y devolvemos en formato FHIR
            Paciente pacienteActualizado = pacienteRepository.save(pacienteExistente);
            return fhirParser.encodeResourceToString(convertirAFhir(pacienteActualizado));
        }
        return null;
    }

    // Eliminar un paciente
    public boolean eliminarPaciente(Long id) {
        if (pacienteRepository.existsById(id)) {
            pacienteRepository.deleteById(id);
            return true; 
        }
        return false; 
    }

    // Obtener paciente por ID de usuario (para acceso del propio paciente)
    public String leerPacientePorIdUsuario(Long idUsuario) {
        Optional<Paciente> paciente = pacienteRepository.findByIdUsuario(idUsuario);
        if (paciente.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(paciente.get()));
        }
        return null;
    }

    // Obtener ID del paciente por ID de usuario
    public Long obtenerIdPacientePorIdUsuario(Long idUsuario) {
        Optional<Paciente> paciente = pacienteRepository.findByIdUsuario(idUsuario);
        return paciente.map(Paciente::getId).orElse(null);
    }

    // Obtener pacientes asociados a un familiar (por ID de usuario del familiar)
    public String leerPacientesPorFamiliar(Long idUsuarioFamiliar) {
        List<Paciente> pacientes = familiarRepository.findPacientesByIdUsuario(idUsuarioFamiliar);
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Paciente p : pacientes) {
            bundle.addEntry().setResource(convertirAFhir(p));
        }
        return fhirParser.encodeResourceToString(bundle);
    }

    // Verificar si un familiar tiene acceso a un paciente específico
    public boolean familiarTieneAccesoAPaciente(Long idUsuarioFamiliar, Long pacienteId) {
        List<Paciente> pacientes = familiarRepository.findPacientesByIdUsuario(idUsuarioFamiliar);
        return pacientes.stream().anyMatch(p -> p.getId().equals(pacienteId));
    }

    // Obtener todos los pacientes incluyendo eliminados (para ADMIN)
    public String leerTodosPacientesIncluyendoEliminados() {
        List<Paciente> pacientes = pacienteRepository.findAllIncludingInactive();
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Paciente p : pacientes) {
            bundle.addEntry().setResource(convertirAFhir(p));
        }
        return fhirParser.encodeResourceToString(bundle);
    }

    // Obtener solo los pacientes eliminados (para ADMIN)
    public String leerPacientesEliminados() {
        List<Paciente> pacientes = pacienteRepository.findAllInactive();
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Paciente p : pacientes) {
            bundle.addEntry().setResource(convertirAFhir(p));
        }
        return fhirParser.encodeResourceToString(bundle);
    }

    // Obtener paciente por ID incluyendo eliminados (para ADMIN)
    public String leerPacientePorIdIncluyendoEliminado(Long id) {
        Optional<Paciente> paciente = pacienteRepository.findByIdIncludingInactive(id);
        if (paciente.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(paciente.get()));
        }
        return null;
    }

    // TRADUCTOR FHIR: Mapea Paciente (Java) -> Patient (FHIR)
    private Patient convertirAFhir(Paciente miPaciente) {
        Patient fhirPatient = new Patient();
        // Asigna el ID de la base de datos al recurso FHIR
        fhirPatient.setId(miPaciente.getId().toString());
        
        // DNI or Passeport
        String systemUri = "urn:oid:2.16.840.1.113883.2.9.2.30";// (DNI)
    
        if (miPaciente.getTipoDocumento() != null && 
            miPaciente.getTipoDocumento().equalsIgnoreCase("PASAPORTE")) {
            systemUri = "http://hl7.org/fhir/sid/passport-esp"; // URI for Passeport
    }

    fhirPatient.addIdentifier()
        .setSystem(systemUri)
        .setValue(miPaciente.getNumDocumento());
            
        // Tarjeta Sanitaria (Solo si la tiene registrada)
        if (miPaciente.getTarjetaSanitaria() != null) {
            fhirPatient.addIdentifier()
                .setSystem("urn:oid:2.16.724.4.40") // Código del Sistema Nacional de Salud Español
                .setValue(miPaciente.getTarjetaSanitaria());
        }
            
        // Nombre Completo
        fhirPatient.addName()
            .addGiven(miPaciente.getNombre())
            .setFamily(miPaciente.getApellido());
            
        // Teléfono
        fhirPatient.addTelecom()
            .setSystem(ContactPoint.ContactPointSystem.PHONE)
            .setValue(miPaciente.getNumTelefono());

        // Fecha de Nacimiento
        if (miPaciente.getFechaNacimiento() != null) {
            fhirPatient.setBirthDate(miPaciente.getFechaNacimiento());
        }
        //  Mapeo del Departamento (Organization)
        // En FHIR, un paciente pertenece a una "ManagingOrganization"
        if (miPaciente.getDepartamento() != null) {
            Reference refOrg = new Reference();
            refOrg.setReference("Organization/" + miPaciente.getDepartamento().getId());
            refOrg.setDisplay(miPaciente.getDepartamento().getNombre());
            fhirPatient.setManagingOrganization(refOrg);
        }

        // Mapeo de Familiares (Contact)
        // Recorremos la lista de familiares y los añadimos como contactos del paciente
        if (miPaciente.getFamiliares() != null) {
            for (Familiar fam : miPaciente.getFamiliares()) {
                Patient.ContactComponent contactoFhir = fhirPatient.addContact();
                
                // Nombre del familiar
                contactoFhir.getName()
                    .setFamily(fam.getApellido())
                    .addGiven(fam.getNombre());
                
                // Teléfono del familiar
                contactoFhir.addTelecom()
                    .setSystem(ContactPoint.ContactPointSystem.PHONE)
                    .setValue(fam.getNumTelefono());
            }
        }
        return fhirPatient;
    }


}
