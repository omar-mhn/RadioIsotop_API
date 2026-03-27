package com.projecte.radioisotopo.Service;

import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projecte.radioisotopo.Model.Reloj;
import com.projecte.radioisotopo.DTO.RelojDTO;
import com.projecte.radioisotopo.Repository.RelojRepository;

import ca.uhn.fhir.parser.IParser;

@Service
public class RelojService {

    @Autowired
    private RelojRepository relojRepository;

    @Autowired
    private IParser fhirParser;

    // Create
    public String crearReloj(RelojDTO dto) {
        Reloj newReloj = new Reloj();
        newReloj.setImei(dto.imei());
        newReloj.setMacAddress(dto.macAddress());
        newReloj.setEstadoReloj(dto.estadoReloj());
        newReloj.setBateriaActual(dto.bateriaActual());
        newReloj.setActivo(true);
        Reloj guardado = relojRepository.save(newReloj);
        return fhirParser.encodeResourceToString(convertirAFhir(guardado));
    }
    // read all
    public String obtenerTodos() {
        List<Reloj> lista = relojRepository.findAll();
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Reloj r : lista) bundle.addEntry().setResource(convertirAFhir(r));
        return fhirParser.encodeResourceToString(bundle);
    }
    // read one
    public String obtenerPorId(Long id) {
        Optional<Reloj> relojOpt = relojRepository.findById(id);
        if (relojOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(relojOpt.get()));
        }
        return null;
    }

    // UPDATE 
    public String actualizarReloj(Long id, RelojDTO dto) {
        Optional<Reloj> relojOpt = relojRepository.findById(id);
        if (relojOpt.isPresent()) {
            Reloj r = relojOpt.get();
            r.setImei(dto.imei());
            r.setMacAddress(dto.macAddress());
            r.setEstadoReloj(dto.estadoReloj());
            r.setBateriaActual(dto.bateriaActual());
            
            Reloj guardado = relojRepository.save(r);
            return fhirParser.encodeResourceToString(convertirAFhir(guardado));
        }
        return null;
    }

    public boolean eliminar(Long id) {
        if (relojRepository.existsById(id)) {
            relojRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // TRADUCTOR FHIR: Reloj (Java) -> Device (FHIR)
    private Device convertirAFhir(Reloj miReloj) {
        Device fhirDevice = new Device();
        fhirDevice.setId(miReloj.getId().toString());

        // IMEI
        fhirDevice.addIdentifier()
            .setSystem("urn:gsma:imei")
            .setValue(miReloj.getImei());

        // MAC ADDRESS
        fhirDevice.addIdentifier()
            .setSystem("urn:ieee:mac")
            .setValue(miReloj.getMacAddress());

        // Etat de la batterie (on l'ajoute comme une note ou un nom de modèle)
        fhirDevice.setDeviceName(List.of(new Device.DeviceDeviceNameComponent()
            .setName("Batería: " + miReloj.getBateriaActual() + "%")
            .setType(Device.DeviceNameType.OTHER)));

        return fhirDevice;
    }
}