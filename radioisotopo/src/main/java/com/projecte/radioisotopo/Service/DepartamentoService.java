package com.projecte.radioisotopo.Service;


import com.projecte.radioisotopo.Model.Departamento;
import com.projecte.radioisotopo.Repository.DepartamentoRepository;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Organization;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    private final IParser fhirParser;

    public DepartamentoService() {
        FhirContext ctx = FhirContext.forR4();
        this.fhirParser = ctx.newJsonParser().setPrettyPrint(true);
    }

    // Create
    public String crearDepartamento(Departamento dep) {
        return fhirParser.encodeResourceToString(convertirAFhir(departamentoRepository.save(dep)));
    }
    // read all
    public String obtenerTodos() {
        List<Departamento> lista = departamentoRepository.findAll();
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Departamento d : lista) bundle.addEntry().setResource(convertirAFhir(d));
        return fhirParser.encodeResourceToString(bundle);
    }
    // read one
    public String obtenerPorId(Long id) {
        Optional<Departamento> departamentoOpt = departamentoRepository.findById(id);
        if(departamentoOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(departamentoOpt.get()));
        }
        return null;
    }
    // uppdte
    public String actualizarDepartamento(Long id, Departamento detallesActualizados) {
        Optional<Departamento> depOpt = departamentoRepository.findById(id);

        if (depOpt.isPresent()) {
            Departamento depExistente = depOpt.get();

            
            depExistente.setNombre(detallesActualizados.getNombre());
            depExistente.setCentro(detallesActualizados.getCentro());
            depExistente.setUbicacion(detallesActualizados.getUbicacion());
            depExistente.setEmail(detallesActualizados.getEmail());
            

            
            Departamento depActualizado = departamentoRepository.save(depExistente);
            return fhirParser.encodeResourceToString(convertirAFhir(depActualizado));
        }
        return null; 
    }
    // delete 
    public boolean eliminar(Long id) {
        if (departamentoRepository.existsById(id)) {
            departamentoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // TRADUCTOR FHIR: Departamento (Java) -> Organization (FHIR)
    private Organization convertirAFhir(Departamento dep) {
        Organization fhirOrg = new Organization();

        // ID lógico
        fhirOrg.setId(dep.getId().toString());

        // Nombre del departamento (ej: "Radiología")
        fhirOrg.setName(dep.getNombre());

        // El centro médico se añade como un Alias (ej: "Hospital Clínico")
        if (dep.getCentro() != null) {
            fhirOrg.addAlias(dep.getCentro());
        }

        // Contacto (Email)
        if (dep.getEmail() != null) {
            fhirOrg.addTelecom()
                .setSystem(ContactPoint.ContactPointSystem.EMAIL)
                .setValue(dep.getEmail());
        }

        // Dirección / Ubicación
        if (dep.getUbicacion() != null) {
            fhirOrg.addAddress().setText(dep.getUbicacion());
        }

        return fhirOrg;
    }
}
