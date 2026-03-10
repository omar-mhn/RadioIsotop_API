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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service
public class FamiliarService {

    @Autowired
    private FamiliarRepository familiarRepository;

    private final IParser fhirParser;

    public FamiliarService() {
        FhirContext ctx = FhirContext.forR4();
        this.fhirParser = ctx.newJsonParser().setPrettyPrint(true);
    }
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
            f.setEmail(detalles.getEmail());
            f.setDni(detalles.getDni());
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
    // Traducteur FHIR
    private RelatedPerson convertirAFhir(Familiar fam) {
        RelatedPerson fhirFam = new RelatedPerson();
        fhirFam.setId(fam.getId().toString());

        // Identifiants (DNI et Carte Sanitaire)
        fhirFam.addIdentifier().setSystem("urn:oid:dni").setValue(fam.getDni());
        if (fam.getTarjetaSanitaria() != null) {
            fhirFam.addIdentifier().setSystem("urn:oid:ts").setValue(fam.getTarjetaSanitaria());
        }

        // Nom
        fhirFam.addName().setFamily(fam.getApellido()).addGiven(fam.getNombre());

        // Télécom
        fhirFam.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(fam.getNumTelefono());
        if (fam.getEmail() != null) {
            fhirFam.addTelecom().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(fam.getEmail());
        }

        return fhirFam;
    }
}
