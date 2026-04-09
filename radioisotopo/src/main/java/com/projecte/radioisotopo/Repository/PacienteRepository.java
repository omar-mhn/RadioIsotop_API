package com.projecte.radioisotopo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente,Long> {
    Optional<Paciente> findByIdUsuario(Long idUsuario);

    // Obtener todos los pacientes incluyendo inactivos
    @Query("SELECT p FROM Paciente p")
    List<Paciente> findAllIncludingInactive();

    // Obtener solo los pacientes eliminados
    @Query("SELECT p FROM Paciente p WHERE p.activo = false")
    List<Paciente> findAllInactive();

    // Obtener paciente por ID incluyendo inactivos
    @Query("SELECT p FROM Paciente p WHERE p.id = :id")
    Optional<Paciente> findByIdIncludingInactive(Long id);
}
