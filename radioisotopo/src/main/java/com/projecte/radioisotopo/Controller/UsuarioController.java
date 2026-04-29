package com.projecte.radioisotopo.Controller;

import com.projecte.radioisotopo.DTO.UsuarioFotoPerfilResponse;
import com.projecte.radioisotopo.Model.Usuario;
import com.projecte.radioisotopo.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PreAuthorize("hasRole('ADMIN') or #idUsuario == authentication.principal.id")
    @GetMapping("/usuarios/{idUsuario}/foto-perfil")
    public ResponseEntity<UsuarioFotoPerfilResponse> getFotoPerfilByUsuarioId(@PathVariable Long idUsuario) {
        Usuario usuario = usuarioService.obtenerPorId(idUsuario);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UsuarioFotoPerfilResponse(idUsuario, usuario.getFotoPerfil()));
    }

    @PreAuthorize("hasRole('ADMIN') or #idUsuario == authentication.principal.id")
    @GetMapping("/usuarios/{idUsuario}/foto-perfil/imagen")
    public ResponseEntity<byte[]> getImagenFotoPerfilByUsuarioId(@PathVariable Long idUsuario) throws IOException {
        byte[] imagen = usuarioService.obtenerImagenFotoPerfil(idUsuario);
        if (imagen == null) {
            return ResponseEntity.notFound().build();
        }
        String tipoContenido = usuarioService.obtenerTipoContenidoImagen(idUsuario);
        HttpHeaders cabeceras = new HttpHeaders();
        cabeceras.setContentType(MediaType.parseMediaType(tipoContenido));
        return ResponseEntity.ok().headers(cabeceras).body(imagen);
    }

    @PreAuthorize("hasRole('ADMIN') or #idUsuario == authentication.principal.id")
    @PatchMapping(value = "/usuarios/{idUsuario}/foto-perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioFotoPerfilResponse> actualizarFotoPerfilByUsuarioId(
            @PathVariable Long idUsuario,
            @RequestParam("imagen") MultipartFile archivo) throws IOException {
        Usuario usuarioActualizado = usuarioService.actualizarFotoPerfil(idUsuario, archivo);
        if (usuarioActualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UsuarioFotoPerfilResponse(idUsuario, usuarioActualizado.getFotoPerfil()));
    }
}
