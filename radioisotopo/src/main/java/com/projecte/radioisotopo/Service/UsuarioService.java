package com.projecte.radioisotopo.Service;

import com.projecte.radioisotopo.Model.Usuario;
import com.projecte.radioisotopo.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; // Para encriptar contraseñas
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectado para seguridad

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca al usuario para el proceso de autenticación (Login)
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));
    }


    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario guardar(Usuario usuario) {
        // ENCRIPTACIÓN: Antes de guardar, convertimos el password plano en un hash seguro
        String passwordEncriptado = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptado);
        
        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public boolean eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Obtener todos los usuarios incluyendo los eliminados (para ADMIN)
    public List<Usuario> obtenerTodosIncluyendoInactivos() {
        return usuarioRepository.findAllIncludingInactive();
    }

    // Obtener solo los usuarios eliminados (para ADMIN)
    public List<Usuario> obtenerEliminados() {
        return usuarioRepository.findAllInactive();
    }

    // Obtener usuario por ID incluyendo inactivos (para ADMIN)
    public Usuario obtenerPorIdIncluyendoInactivo(Long id) {
        return usuarioRepository.findByIdIncludingInactive(id).orElse(null);
    }

    public Usuario actualizarFotoPerfil(Long idUsuario, String fotoPerfil) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            return null;
        }
        usuario.setFotoPerfil(fotoPerfil);
        return usuarioRepository.save(usuario);
    }
}
