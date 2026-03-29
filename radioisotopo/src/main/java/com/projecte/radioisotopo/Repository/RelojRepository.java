package com.projecte.radioisotopo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Reloj;
import com.projecte.radioisotopo.Model.EstadoReloj;

@Repository
public interface RelojRepository extends JpaRepository<Reloj,Long>{

    // Buscar por estado
    List<Reloj> findByEstadoReloj(EstadoReloj estado);
    
    // Buscar relojes con batería baja
    List<Reloj> findByBateriaActualLessThan(Integer nivel);

    // Obtener todos incluyendo inactivos
    @Query("SELECT r FROM Reloj r")
    List<Reloj> findAllIncludingInactive();

    // Obtener solo eliminados
    @Query("SELECT r FROM Reloj r WHERE r.activo = false")
    List<Reloj> findAllInactive();

    // Obtener por ID incluyendo inactivos
    @Query("SELECT r FROM Reloj r WHERE r.id = :id")
    Optional<Reloj> findByIdIncludingInactive(Long id);
}
