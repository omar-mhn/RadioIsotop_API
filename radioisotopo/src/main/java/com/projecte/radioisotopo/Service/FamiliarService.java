package com.projecte.radioisotopo.Service;

import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projecte.radioisotopo.Model.Familiar;
import com.projecte.radioisotopo.Repository.FamiliarRepository;


import ca.uhn.fhir.parser.IParser;

@Service
public class FamiliarService {

    @Autowired
    private FamiliarRepository familiarRepository;

    @Autowired
    private IParser fhirParser;

    //create
    public String crearFamiliar(Familiar f) {
        return fhirParser.encodeResourceToString(convertirAFhir(familiarRepository.save(f)));
    }
    // read All
    public String obtenerTodos() {
        List<Familiar> lista = familiarRepository.findAll();
        Bundle bundle = new Bundle().setType(Bundle.BundleType.SEARCHSET);
        for (Familiar f : lista) bundle.addEntry().setResource(convertirAFhir(f));
        return fhirParser.encodeResourceToString(bundle);
    }
    // read one
    public String obtenerPorId(Long id) {
        Optional<Familiar> famOpt = familiarRepository.findById(id);
        if (famOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(famOpt.get()));
        }
        return null;
    }
    // update
    public String actualizarFamiliar(Long id, Familiar detalles) {
        Optional<Familiar> famOpt = familiarRepository.findById(id);
        if (famOpt.isPresent()) {
            Familiar f = famOpt.get();
            f.setNombre(detalles.getNombre());
            f.setApellido(detalles.getApellido());
            f.setNumTelefono(detalles.getNumTelefono());
            f.setNumDocumento(detalles.getNumDocumento());
            f.setTipoDocumento(detalles.getTipoDocumento());
            f.setTarjetaSanitaria(detalles.getTarjetaSanitaria());
            
            return fhirParser.encodeResourceToString(convertirAFhir(familiarRepository.save(f)));
        }
        return null;
    }

    // DELETE
    public boolean eliminarFamiliar(Long id) {
        if (familiarRepository.existsById(id)) {
            familiarRepository.deleteById(id);
            return true;
        }
        return false;
    }
    // Traductor FHIR
    private RelatedPerson convertirAFhir(Familiar fam) {
        RelatedPerson fhirFam = new RelatedPerson();
        fhirFam.setId(fam.getId().toString());

        // Identifiants (DNI y tarjeta sanitaria)
        String systemUri = "urn:oid:2.16.840.1.113883.2.9.2.30"; 
        if (fam.getTipoDocumento() != null && fam.getTipoDocumento().equalsIgnoreCase("PASAPORTE")) {
            systemUri = "http://hl7.org/fhir/sid/passport-esp"; 
        }

        fhirFam.addIdentifier()
            .setSystem(systemUri)
            .setValue(fam.getNumDocumento());

        if (fam.getTarjetaSanitaria() != null) {
            fhirFam.addIdentifier()
                .setSystem("urn:oid:2.16.724.4.40")
                .setValue(fam.getTarjetaSanitaria());
        }

        // Nom
        fhirFam.addName().setFamily(fam.getApellido()).addGiven(fam.getNombre());



        return fhirFam;
    }
}
