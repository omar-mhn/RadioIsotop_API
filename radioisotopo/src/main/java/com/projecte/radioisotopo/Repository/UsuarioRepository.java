package com.projecte.radioisotopo.Repository;

import com.projecte.radioisotopo.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Este método es fundamental para el proceso de Login.
    Optional<Usuario> findByEmail(String email);

    // Obtener todos los usuarios incluyendo los inactivos
    @Query("SELECT u FROM Usuario u")
    List<Usuario> findAllIncludingInactive();

    // Obtener solo los usuarios inactivos
    @Query("SELECT u FROM Usuario u WHERE u.activo = false")
    List<Usuario> findAllInactive();

    // Obtener un usuario por ID incluyendo inactivos
    @Query("SELECT u FROM Usuario u WHERE u.id = :id")
    Optional<Usuario> findByIdIncludingInactive(Long id);
}