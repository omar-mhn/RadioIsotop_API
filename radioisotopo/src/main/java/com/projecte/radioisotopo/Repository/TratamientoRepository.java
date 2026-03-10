package com.projecte.radioisotopo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Tratamiento;

@Repository
public interface TratamientoRepository extends JpaRepository <Tratamiento,Long> {

}
