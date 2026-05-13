package com.projecte.radioisotopo.Service;

import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projecte.radioisotopo.Model.Reloj;
import com.projecte.radioisotopo.Model.Tratamiento;
import com.projecte.radioisotopo.Model.EstadoTratamiento;
import com.projecte.radioisotopo.DTO.RelojDTO;
import com.projecte.radioisotopo.Repository.RelojRepository;
import com.projecte.radioisotopo.Repository.TratamientoRepository;

import ca.uhn.fhir.parser.IParser;

@Service
public class RelojService {

    @Autowired
    private RelojRepository relojRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private IParser fhirParser;

    // Create
    public String crearReloj(RelojDTO dto) {
        Reloj newReloj = new Reloj();
        newReloj.setImei(dto.imei());
        newReloj.setMacAddress(dto.macAddress());
        newReloj.setIdAndroid(dto.idAndroid());
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
            r.setIdAndroid(dto.idAndroid());
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

    // Obtener relojes disponibles
    public String obtenerDisponibles() {
        List<Reloj> lista = relojRepository.findByEstadoReloj(
            com.projecte.radioisotopo.Model.EstadoReloj.DISPONIBLE);
        return crearBundle(lista);
    }

    // Obtener relojes con batería baja
    public String obtenerBateriaBaja(Integer nivelMinimo) {
        List<Reloj> lista = relojRepository.findByBateriaActualLessThan(nivelMinimo);
        return crearBundle(lista);
    }

    // Obtener todos incluyendo eliminados (para ADMIN)
    public String obtenerTodosIncluyendoEliminados() {
        List<Reloj> lista = relojRepository.findAllIncludingInactive();
        return crearBundle(lista);
    }

    // Obtener solo eliminados (para ADMIN)
    public String obtenerEliminados() {
        List<Reloj> lista = relojRepository.findAllInactive();
        return crearBundle(lista);
    }

    // Obtener por ID incluyendo eliminados (para ADMIN)
    public String obtenerPorIdIncluyendoEliminado(Long id) {
        Optional<Reloj> relojOpt = relojRepository.findByIdIncludingInactive(id);
        if (relojOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(relojOpt.get()));
        }
        return null;
    }

    // Obtener el reloj vinculado al tratamiento activo de un paciente
    public String obtenerPorPaciente(Long pacienteId) {
        List<Tratamiento> activos = tratamientoRepository
            .findByPacienteIdAndEstadoTratamiento(pacienteId, EstadoTratamiento.ACTIVO);
        Tratamiento tratamiento = null;
        if (!activos.isEmpty()) {
            tratamiento = activos.get(activos.size() - 1); // el más reciente
        } else {
            // Si no hay activo, usar el último tratamiento disponible
            List<Tratamiento> todos = tratamientoRepository.findByPacienteId(pacienteId);
            if (!todos.isEmpty()) {
                tratamiento = todos.get(todos.size() - 1);
            }
        }

        if (tratamiento == null || tratamiento.getReloj() == null) return null;
        return fhirParser.encodeResourceToString(convertirAFhir(tratamiento.getReloj()));
    }

    // Helper para crear Bundle
    private String crearBundle(List<Reloj> lista) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Reloj r : lista) bundle.addEntry().setResource(convertirAFhir(r));
        return fhirParser.encodeResourceToString(bundle);
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

        // Android ID (si existe)
        if (miReloj.getIdAndroid() != null) {
            fhirDevice.addIdentifier()
                .setSystem("urn:android:id")
                .setValue(miReloj.getIdAndroid());
        }

        // Etat de la batterie (on l'ajoute comme une note ou un nom de modèle)
        fhirDevice.setDeviceName(List.of(new Device.DeviceDeviceNameComponent()
            .setName("Batería: " + miReloj.getBateriaActual() + "%")
            .setType(Device.DeviceNameType.OTHER)));

        return fhirDevice;
    }
}
