package com.projecte.radioisotopo.Service;

import com.projecte.radioisotopo.Model.Usuario;
import com.projecte.radioisotopo.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.upload.directorio}")
    private String directorioSubida;

    @Value("${app.upload.url-base}")
    private String urlBase;

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

    public Usuario actualizarFotoPerfil(Long idUsuario, MultipartFile archivo) throws IOException {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            return null;
        }

        // Crear el directorio si no existe
        Path dirPath = Paths.get(directorioSubida);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Eliminar la imagen anterior si existe
        if (usuario.getFotoPerfil() != null) {
            String nombreAnterior = Paths.get(usuario.getFotoPerfil()).getFileName().toString();
            Path rutaAnterior = dirPath.resolve(nombreAnterior);
            Files.deleteIfExists(rutaAnterior);
        }

        // Guardar el nuevo archivo con nombre único
        String extension = obtenerExtension(archivo.getOriginalFilename());
        String nombreArchivo = "usuario_" + idUsuario + "_" + UUID.randomUUID() + extension;
        Path rutaDestino = dirPath.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaDestino);

        // Guardar la URL en la base de datos
        usuario.setFotoPerfil(urlBase + "/" + nombreArchivo);
        return usuarioRepository.save(usuario);
    }

    public byte[] obtenerImagenFotoPerfil(Long idUsuario) throws IOException {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null || usuario.getFotoPerfil() == null) {
            return null;
        }

        String nombreArchivo = Paths.get(usuario.getFotoPerfil()).getFileName().toString();
        Path rutaImagen = Paths.get(directorioSubida).resolve(nombreArchivo);

        if (!Files.exists(rutaImagen)) {
            return null;
        }

        return Files.readAllBytes(rutaImagen);
    }

    public String obtenerTipoContenidoImagen(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null || usuario.getFotoPerfil() == null) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        String nombre = usuario.getFotoPerfil().toLowerCase();
        if (nombre.endsWith(".png")) return MediaType.IMAGE_PNG_VALUE;
        if (nombre.endsWith(".gif")) return MediaType.IMAGE_GIF_VALUE;
        return MediaType.IMAGE_JPEG_VALUE;
    }

    private String obtenerExtension(String nombreOriginal) {
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            return nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }
        return ".jpg";
    }
}
