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
import com.projecte.radioisotopo.Repository.PacienteRepository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service
public class PacienteService {

    @Autowired 
    PacienteRepository pacienteRepository;

    private final IParser fhirParser;

    public PacienteService() {
        FhirContext ctx = FhirContext.forR4();
        this.fhirParser = ctx.newJsonParser().setPrettyPrint(true);
    }
    // Crear un nuevo paciente
    public String createPacient(Paciente nuevoPaciente){
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

    public String actualizarPaciente(Long id, Paciente detallesActualizados) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        
        if (paciente.isPresent()) {
            Paciente pacienteExistente = paciente.get();
            
            // Actualizamos los datos locales
            pacienteExistente.setNombre(detallesActualizados.getNombre());
            pacienteExistente.setApellido(detallesActualizados.getApellido());
            pacienteExistente.setNumTelefono(detallesActualizados.getNumTelefono());
            pacienteExistente.setEmail(detallesActualizados.getEmail());
            pacienteExistente.setDni(detallesActualizados.getDni());
            pacienteExistente.setTarjetaSanitaria(detallesActualizados.getTarjetaSanitaria());
            pacienteExistente.setFechaNacimiento(detallesActualizados.getFechaNacimiento());
            pacienteExistente.setPeso(detallesActualizados.getPeso());
            pacienteExistente.setAltura(detallesActualizados.getAltura());
            
            // Actualizamos el departamento y los familiares
            pacienteExistente.setDepartamento(detallesActualizados.getDepartamento());
            pacienteExistente.setFamiliares(detallesActualizados.getFamiliares());
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

    // TRADUCTOR FHIR: Mapea Paciente (Java) -> Patient (FHIR)
    private Patient convertirAFhir(Paciente miPaciente) {
        Patient fhirPatient = new Patient();
        // Asigna el ID de la base de datos al recurso FHIR
        fhirPatient.setId(miPaciente.getId().toString());
        
        // DNI
        fhirPatient.addIdentifier()
            .setSystem("urn:oid:2.16.840.1.113883.2.9.2.30")
            .setValue(miPaciente.getDni());
            
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

        // Email (Solo si lo tiene registrado)
        if (miPaciente.getEmail() != null) {
            fhirPatient.addTelecom()
                .setSystem(ContactPoint.ContactPointSystem.EMAIL)
                .setValue(miPaciente.getEmail());
        }

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
