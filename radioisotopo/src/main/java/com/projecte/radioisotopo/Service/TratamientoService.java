package com.projecte.radioisotopo.Service;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projecte.radioisotopo.Model.Tratamiento;
import com.projecte.radioisotopo.Model.Paciente;
import com.projecte.radioisotopo.Model.Reloj;
import com.projecte.radioisotopo.Model.Doctor;
import com.projecte.radioisotopo.DTO.TratamientoDTO;
import com.projecte.radioisotopo.Repository.TratamientoRepository;
import com.projecte.radioisotopo.Repository.PacienteRepository;
import com.projecte.radioisotopo.Repository.RelojRepository;
import com.projecte.radioisotopo.Repository.DoctorRepository;


import ca.uhn.fhir.parser.IParser;

@Service
public class TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private RelojRepository relojRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private IParser fhirParser;

    // Crear un nuevo tratamiento (CarePlan)
    public String crearTratamiento(TratamientoDTO dto) {
        Tratamiento t = new Tratamiento();
        t.setTipoIsotopo(dto.tipoIsotopo());
        t.setDosisInicial(dto.dosisInicial());
        t.setFechaAdministracion(dto.fechaAdministracion());
        t.setFechaFinalEstimada(dto.fechaFinalEstimada());
        t.setEstadoTratamiento(dto.estadoTratamiento());
        
        if (dto.pacienteId() != null) {
            t.setPaciente(pacienteRepository.findById(dto.pacienteId()).orElse(null));
        }
        if (dto.relojId() != null) {
            t.setReloj(relojRepository.findById(dto.relojId()).orElse(null));
        }
        if (dto.doctorId() != null) {
            t.setDoctor(doctorRepository.findById(dto.doctorId()).orElse(null));
        }

        Tratamiento guardado = tratamientoRepository.save(t);
        return fhirParser.encodeResourceToString(convertirAFhir(guardado));
    }

    // Obtener todos 
    public String obtenerTodos() {
        List<Tratamiento> lista = tratamientoRepository.findAll();
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Tratamiento t : lista) {
            bundle.addEntry().setResource(convertirAFhir(t));
        }
        return fhirParser.encodeResourceToString(bundle);
    }

    // Obtener un tratamiento específico por ID
    public String obtenerPorId(Long id) {
        Optional<Tratamiento> tOpt = tratamientoRepository.findById(id);
        if (tOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(tOpt.get()));
        }
        return null;
    }

    // Actualizar el tratamiento
    public String actualizarTratamiento(Long id, TratamientoDTO dto) {
        Optional<Tratamiento> tOpt = tratamientoRepository.findById(id);
        if (tOpt.isPresent()) {
            Tratamiento t = tOpt.get();
            t.setTipoIsotopo(dto.tipoIsotopo());
            t.setDosisInicial(dto.dosisInicial());
            t.setFechaAdministracion(dto.fechaAdministracion());
            t.setFechaFinalEstimada(dto.fechaFinalEstimada());
            t.setEstadoTratamiento(dto.estadoTratamiento());
            
            if (dto.pacienteId() != null) {
                t.setPaciente(pacienteRepository.findById(dto.pacienteId()).orElse(null));
            } else {
                t.setPaciente(null);
            }
            
            if (dto.relojId() != null) {
                t.setReloj(relojRepository.findById(dto.relojId()).orElse(null));
            } else {
                t.setReloj(null);
            }
            
            if (dto.doctorId() != null) {
                t.setDoctor(doctorRepository.findById(dto.doctorId()).orElse(null));
            } else {
                t.setDoctor(null);
            }
            
            return fhirParser.encodeResourceToString(convertirAFhir(tratamientoRepository.save(t)));
        }
        return null;
    }

    // Eliminar registro
    public boolean eliminar(Long id) {
        if (tratamientoRepository.existsById(id)) {
            tratamientoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Obtener por paciente
    public String obtenerPorPaciente(Long pacienteId) {
        List<Tratamiento> lista = tratamientoRepository.findByPacienteId(pacienteId);
        return crearBundle(lista);
    }

    // Obtener por doctor
    public String obtenerPorDoctor(Long doctorId) {
        List<Tratamiento> lista = tratamientoRepository.findByDoctorId(doctorId);
        return crearBundle(lista);
    }

    // Obtener tratamientos activos
    public String obtenerActivos() {
        List<Tratamiento> lista = tratamientoRepository.findByEstadoTratamiento(
            com.projecte.radioisotopo.Model.EstadoTratamiento.ACTIVO);
        return crearBundle(lista);
    }

    // Obtener todos incluyendo eliminados (para ADMIN)
    public String obtenerTodosIncluyendoEliminados() {
        List<Tratamiento> lista = tratamientoRepository.findAllIncludingInactive();
        return crearBundle(lista);
    }

    // Obtener solo eliminados (para ADMIN)
    public String obtenerEliminados() {
        List<Tratamiento> lista = tratamientoRepository.findAllInactive();
        return crearBundle(lista);
    }

    // Obtener por ID incluyendo eliminados (para ADMIN)
    public String obtenerPorIdIncluyendoEliminado(Long id) {
        Optional<Tratamiento> tOpt = tratamientoRepository.findByIdIncludingInactive(id);
        if (tOpt.isPresent()) {
            return fhirParser.encodeResourceToString(convertirAFhir(tOpt.get()));
        }
        return null;
    }

    // Helper para crear Bundle
    private String crearBundle(List<Tratamiento> lista) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        for (Tratamiento t : lista) {
            bundle.addEntry().setResource(convertirAFhir(t));
        }
        return fhirParser.encodeResourceToString(bundle);
    }

    // TRADUCTOR FHIR: Tratamiento (Java) -> CarePlan (FHIR)
    private CarePlan convertirAFhir(Tratamiento t) {
        CarePlan cp = new CarePlan();
        cp.setId(t.getId().toString());

        // Estado y Propósito (Mapeo dinámico)
        if (t.getEstadoTratamiento() != null) {
            switch (t.getEstadoTratamiento()) {
                case ACTIVO:
                    cp.setStatus(CarePlan.CarePlanStatus.ACTIVE);
                    break;
                case FINALIZADO:
                    cp.setStatus(CarePlan.CarePlanStatus.COMPLETED);
                    break;
                case CANCELADO:
                    cp.setStatus(CarePlan.CarePlanStatus.REVOKED);
                    break;
            }
        } else {
            cp.setStatus(CarePlan.CarePlanStatus.UNKNOWN);
        }
        cp.setIntent(CarePlan.CarePlanIntent.PLAN);
        cp.setTitle("Tratamiento de Radioisótopo: " + t.getTipoIsotopo());

        // Referencia al PACIENTE (Subject)
        if (t.getPaciente() != null) {
            Reference refPac = new Reference("Patient/" + t.getPaciente().getId());
            refPac.setDisplay(t.getPaciente().getNombre() + " " + t.getPaciente().getApellido());
            cp.setSubject(refPac);
        }

        //  Referencia al DOCTOR (Author/Responsable)
        if (t.getDoctor() != null) {
            Reference refDoc = new Reference("Practitioner/" + t.getDoctor().getId());
            refDoc.setDisplay("Dr. " + t.getDoctor().getApellido());
            cp.setAuthor(refDoc);
        }

        // Referencia al RELOJ (SupportingInfo/Device)
        if (t.getReloj() != null) {
            Reference refReloj = new Reference("Device/" + t.getReloj().getId());
            refReloj.setDisplay("Reloj IoT - IMEI: " + t.getReloj().getImei());
            cp.addSupportingInfo(refReloj);
        }

        // Período de tiempo (Administración -> Fin estimado)
        Period periodo = new Period();
        periodo.setStart(t.getFechaAdministracion());
        if (t.getFechaFinalEstimada() != null) {
            periodo.setEnd(t.getFechaFinalEstimada());
        }
        cp.setPeriod(periodo);

        //  Detalles técnicos del Isótopo (Annotation)
        Annotation nota = new Annotation();
        nota.setText("Dosis inicial administrada: " + t.getDosisInicial() + " MBq");
        cp.addNote(nota);

        return cp;
    }
}