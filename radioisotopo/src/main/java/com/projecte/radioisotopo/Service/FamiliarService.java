package com.projecte.radioisotopo.Service;

import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projecte.radioisotopo.Model.Familiar;
import com.projecte.radioisotopo.DTO.FamiliarDTO;
import com.projecte.radioisotopo.Repository.FamiliarRepository;


import ca.uhn.fhir.parser.IParser;

@Service
public class FamiliarService {

    @Autowired
    private FamiliarRepository familiarRepository;

    @Autowired
    private IParser fhirParser;

    //create
    public String crearFamiliar(FamiliarDTO dto) {
        Familiar f = new Familiar();
        f.setIdUsuario(dto.idUsuario());
        f.setNombre(dto.nombre());
        f.setApellido(dto.apellido());
        f.setNumTelefono(dto.numTelefono());
        f.setNumDocumento(dto.numDocumento());
        f.setTipoDocumento(dto.tipoDocumento());
        f.setTarjetaSanitaria(dto.tarjetaSanitaria());
        f.setActivo(true);
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
    public String actualizarFamiliar(Long id, FamiliarDTO dto) {
        Optional<Familiar> famOpt = familiarRepository.findById(id);
        if (famOpt.isPresent()) {
            Familiar f = famOpt.get();
            f.setNombre(dto.nombre());
            f.setApellido(dto.apellido());
            f.setNumTelefono(dto.numTelefono());
            f.setNumDocumento(dto.numDocumento());
            f.setTipoDocumento(dto.tipoDocumento());
            f.setTarjetaSanitaria(dto.tarjetaSanitaria());
            
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

    // Obtener todos los familiares incluyendo eliminados (para ADMIN)
    public String obtenerTodosIncluyendoEliminados() {
        List<Familiar> lista = familiarRepository.findAllIncludingInactive();
        Bundle bundle = new Bundle().setType(Bundle.BundleType.SEARCHSET);
        for (Familiar f : lista) bundle.addEntry().setResource(convertirAFhir(f));
        return fhirParser.encodeResourceToString(bundle);
    }

    // Obtener solo los familiares eliminados (para ADMIN)
    public String obtenerEliminados() {
        List<Familiar> lista = familiarRepository.findAllInactive();
        Bundle bundle = new Bundle().setType(Bundle.BundleType.SEARCHSET);
        for (Familiar f : lista) bundle.addEntry().setResource(convertirAFhir(f));
        return fhirParser.encodeResourceToString(bundle);
    }

    // Obtener familiar por ID incluyendo eliminados (para ADMIN)
    public String obtenerPorIdIncluyendoEliminado(Long id) {
        Optional<Familiar> famOpt = familiarRepository.findByIdIncludingInactive(id);
        if (famOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(famOpt.get()));
        }
        return null;
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
