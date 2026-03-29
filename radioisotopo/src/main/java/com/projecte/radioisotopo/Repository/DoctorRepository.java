package com.projecte.radioisotopo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    Optional<Doctor> findByIdUsuario(Long idUsuario);

    // Obtener todos los doctores incluyendo inactivos
    @Query("SELECT d FROM Doctor d")
    List<Doctor> findAllIncludingInactive();

    // Obtener solo los doctores eliminados
    @Query("SELECT d FROM Doctor d WHERE d.activo = false")
    List<Doctor> findAllInactive();

    // Obtener doctor por ID incluyendo inactivos
    @Query("SELECT d FROM Doctor d WHERE d.id = :id")
    Optional<Doctor> findByIdIncludingInactive(Long id);
}
