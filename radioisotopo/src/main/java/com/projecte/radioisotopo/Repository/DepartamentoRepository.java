package com.projecte.radioisotopo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento,Long>{

}
