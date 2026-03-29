package com.projecte.radioisotopo.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Alerta;
import com.projecte.radioisotopo.Model.Tipo;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    
    // Buscar alertas por tratamiento
    List<Alerta> findByTratamientoId(Long tratamientoId);
    
    // Buscar alertas por tipo
    List<Alerta> findByTipo(Tipo tipo);
    
    // Buscar alertas por paciente (a través del tratamiento)
    List<Alerta> findByTratamientoPacienteId(Long pacienteId);
    
    // Buscar alertas por rango de fechas
    List<Alerta> findByFechaHoraBetween(Timestamp inicio, Timestamp fin);
    
    // Obtener todas las alertas incluyendo inactivas
    @Query("SELECT a FROM Alerta a")
    List<Alerta> findAllIncludingInactive();
    
    // Obtener solo las alertas eliminadas
    @Query("SELECT a FROM Alerta a WHERE a.activo = false")
    List<Alerta> findAllInactive();
    
    // Obtener alerta por ID incluyendo inactivas
    @Query("SELECT a FROM Alerta a WHERE a.id = :id")
    Optional<Alerta> findByIdIncludingInactive(Long id);
}
