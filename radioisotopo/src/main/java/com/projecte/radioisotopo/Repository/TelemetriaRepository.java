package com.projecte.radioisotopo.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Telemetria;

@Repository
public interface TelemetriaRepository extends JpaRepository<Telemetria,Long> {
    // Buscar telemetría vinculada a un tratamiento específico dentro de un rango de tiempo
    List<Telemetria> findByTratamientoIdAndFechaHoraBetween(Long tratamientoId, Timestamp inicio, Timestamp fin);
    
    // Buscar telemetría por paciente (a través del tratamiento)
    List<Telemetria> findByTratamientoPacienteId(Long pacienteId);
    
    // Buscar telemetría por tratamiento
    List<Telemetria> findByTratamientoId(Long tratamientoId);
    
    // Soft-delete queries (ADMIN)
    @Query(value = "SELECT * FROM telemetria", nativeQuery = true)
    List<Telemetria> findAllIncludingInactive();
    
    @Query(value = "SELECT * FROM telemetria WHERE activo = false", nativeQuery = true)
    List<Telemetria> findAllInactive();
    
    @Query(value = "SELECT * FROM telemetria WHERE id = :id", nativeQuery = true)
    Optional<Telemetria> findByIdIncludingInactive(@Param("id") Long id);
}
