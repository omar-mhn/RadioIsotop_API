package com.projecte.radioisotopo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Familiar;

@Repository
public interface FamiliarRepository extends JpaRepository<Familiar,Long>{
    Optional<Familiar> findByIdUsuario(Long idUsuario);
    
    @Query("SELECT f.pacientes FROM Familiar f WHERE f.idUsuario = :idUsuario")
    List<com.projecte.radioisotopo.Model.Paciente> findPacientesByIdUsuario(@Param("idUsuario") Long idUsuario);

    // Obtener todos los familiares incluyendo inactivos
    @Query("SELECT f FROM Familiar f")
    List<Familiar> findAllIncludingInactive();

    // Obtener solo los familiares eliminados
    @Query("SELECT f FROM Familiar f WHERE f.activo = false")
    List<Familiar> findAllInactive();

    // Obtener familiar por ID incluyendo inactivos
    @Query("SELECT f FROM Familiar f WHERE f.id = :id")
    Optional<Familiar> findByIdIncludingInactive(Long id);
}
