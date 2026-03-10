package com.projecte.radioisotopo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecte.radioisotopo.Model.Familiar;

@Repository
public interface FamiliarRepository extends JpaRepository<Familiar,Long>{

}
