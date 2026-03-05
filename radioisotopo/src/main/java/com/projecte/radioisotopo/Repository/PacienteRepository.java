package com.projecte.radioisotopo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente,Long> {

}
