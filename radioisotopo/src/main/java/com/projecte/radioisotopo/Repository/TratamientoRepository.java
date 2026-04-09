package com.projecte.radioisotopo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Tratamiento;
import com.projecte.radioisotopo.Model.EstadoTratamiento;

@Repository
public interface TratamientoRepository extends JpaRepository <Tratamiento,Long> {

    // Buscar por paciente
    List<Tratamiento> findByPacienteId(Long pacienteId);
    
    // Buscar por doctor
    List<Tratamiento> findByDoctorId(Long doctorId);
    
    // Buscar por estado
    List<Tratamiento> findByEstadoTratamiento(EstadoTratamiento estado);
    
    // Buscar activos de un paciente
    List<Tratamiento> findByPacienteIdAndEstadoTratamiento(Long pacienteId, EstadoTratamiento estado);

    // Obtener todos incluyendo inactivos
    @Query("SELECT t FROM Tratamiento t")
    List<Tratamiento> findAllIncludingInactive();

    // Obtener solo eliminados
    @Query("SELECT t FROM Tratamiento t WHERE t.activo = false")
    List<Tratamiento> findAllInactive();

    // Obtener por ID incluyendo inactivos
    @Query("SELECT t FROM Tratamiento t WHERE t.id = :id")
    Optional<Tratamiento> findByIdIncludingInactive(Long id);
}
