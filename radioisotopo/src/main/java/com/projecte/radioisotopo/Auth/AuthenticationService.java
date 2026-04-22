package com.projecte.radioisotopo.Auth;

import com.projecte.radioisotopo.Model.Usuario;
import com.projecte.radioisotopo.Model.Role;
import com.projecte.radioisotopo.Repository.UsuarioRepository;
import com.projecte.radioisotopo.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    //REGISTRO DE NUEVO USUARIO 
    public AuthenticationResponse register(RegisterRequest request) {
        //Creamos el objeto Usuario con la contraseña encriptada
        var user = Usuario.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.valueOf(request.role().toUpperCase()))
                .activo(true)
                .build();
        repository.save(user);
        
        //Generamos el token JWT para el nuevo usuario
        var jwtToken = jwtService.generarToken(user);
        return new AuthenticationResponse(jwtToken, user.getId());
    }

    //LOGIN DE USUARIO EXISTENTE 
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        //Spring Security verifica el email y password automáticamente Si fallan las credenciales, se lanza una excepción aquí mismo
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        
        // la autenticación fue exitosa. Buscamos al usuario.
        var user = repository.findByEmail(request.email())
                .orElseThrow();
        
        var jwtToken = jwtService.generarToken(user);
        
        return new AuthenticationResponse(jwtToken, user.getId());
    }
}
