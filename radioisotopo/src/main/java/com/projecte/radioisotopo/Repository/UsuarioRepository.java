package com.projecte.radioisotopo.Repository;

import com.projecte.radioisotopo.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Este método es fundamental para el proceso de Login.
    Optional<Usuario> findByEmail(String email);
}