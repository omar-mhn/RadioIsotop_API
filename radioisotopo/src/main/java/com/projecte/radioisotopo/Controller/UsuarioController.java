package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.Model.Usuario;
import com.projecte.radioisotopo.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener todos los usuarios activos
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    // Obtener un usuario por ID (solo activos)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    // Obtener TODOS los usuarios incluyendo eliminados (activo = false)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios/todos")
    public ResponseEntity<List<Usuario>> getAllUsuariosIncluyendoEliminados() {
        return ResponseEntity.ok(usuarioService.obtenerTodosIncluyendoInactivos());
    }

    // Obtener solo los usuarios eliminados (activo = false)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios/eliminados")
    public ResponseEntity<List<Usuario>> getUsuariosEliminados() {
        return ResponseEntity.ok(usuarioService.obtenerEliminados());
    }

    // Obtener un usuario por ID incluyendo eliminados
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios/todos/{id}")
    public ResponseEntity<Usuario> getUsuarioByIdIncluyendoEliminado(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorIdIncluyendoInactivo(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    // Eliminar un usuario (soft delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        if (usuarioService.eliminarUsuario(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
