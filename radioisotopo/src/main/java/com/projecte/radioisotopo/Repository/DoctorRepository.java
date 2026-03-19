package com.projecte.radioisotopo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
	Optional<Doctor> findByNombre(String nombre);
}
