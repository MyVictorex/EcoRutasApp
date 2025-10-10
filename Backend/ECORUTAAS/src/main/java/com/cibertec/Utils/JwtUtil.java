package com.cibertec.Utils;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import com.cibertec.models.Usuario;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "tuClaveSecretaMuySegura1234567890123456"; // mínimo 32 caracteres

    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generarToken(Usuario usuario) {
        return Jwts.builder().setSubject(usuario.getCorreo()).claim("roles", usuario.getRol().name()).setIssuedAt(new Date())
        		.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) 
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validarToken(String token, String correoUsuario) {
        final String correo = obtenerCorreo(token);
        return (correo.equals(correoUsuario) && !estaExpirado(token));
    }

    public String obtenerCorreo(String token) {
        Claims claims = getJwtParser().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    private boolean estaExpirado(String token) {
        Date expiration = getJwtParser().parseClaimsJws(token).getBody().getExpiration();
        return expiration.before(new Date());
    }

    private JwtParser getJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();
    }
}
