package com.projecte.radioisotopo.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // Llave secreta para firmar los tokens (mínimo 32 caracteres)
    private static final String SECRET_KEY = "mi_clave_secreta_super_segura_2026_radioisotopo_proyecto";

    // Extrae el email del usuario que está dentro del token
    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    // Verifica si el token es válido y pertenece al usuario
    public boolean esTokenValido(String token, UserDetails userDetails) {
        final String username = extraerUsername(token);
        return (username.equals(userDetails.getUsername())) && !esTokenExpirado(token);
    }

    // Métodos auxiliares de criptografía 
    
    private boolean esTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private Key getSignInKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String generarToken(UserDetails userDetails) {
        return generarToken(new java.util.HashMap<>(), userDetails);
    }

    // Generar el token con claims (datos extras) y el usuario
    public String generarToken(
            java.util.Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                // El token durará 24 horas
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}