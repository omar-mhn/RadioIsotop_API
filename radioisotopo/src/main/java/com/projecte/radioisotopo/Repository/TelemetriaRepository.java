package com.projecte.radioisotopo.Repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Telemetria;

@Repository
public interface TelemetriaRepository extends JpaRepository<Telemetria,Long> {
    // Buscar telemetría vinculada a un tratamiento específico dentro de un rango de tiempo
    List<Telemetria> findByTratamientoIdAndFechaHoraBetween(Long tratamientoId, Timestamp inicio, Timestamp fin);
}
