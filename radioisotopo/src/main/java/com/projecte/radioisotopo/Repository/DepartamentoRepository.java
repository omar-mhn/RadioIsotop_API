package com.projecte.radioisotopo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento,Long>{

    // Obtener todos incluyendo inactivos
    @Query("SELECT d FROM Departamento d")
    List<Departamento> findAllIncludingInactive();

    // Obtener solo eliminados
    @Query("SELECT d FROM Departamento d WHERE d.activo = false")
    List<Departamento> findAllInactive();

    // Obtener por ID incluyendo inactivos
    @Query("SELECT d FROM Departamento d WHERE d.id = :id")
    Optional<Departamento> findByIdIncludingInactive(Long id);
}
