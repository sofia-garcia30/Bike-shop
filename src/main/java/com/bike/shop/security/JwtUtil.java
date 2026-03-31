package com.bike.shop.security;

import com.bike.shop.entity.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Clave secreta — en producción moverla a application.properties
    private static final String SECRET =
            "tienda-bicicletas-secret-key-2025-muy-larga-para-seguridad";

    // 8 horas en milisegundos
    private static final long EXPIRATION_MS = 8 * 60 * 60 * 1000L;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ─── Generar token ───────────────────────────────────────────
    public String generarToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("nombre", usuario.getNombre())
                .claim("rol", usuario.getRol())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getKey())
                .compact();
    }

    // ─── Extraer email del token ─────────────────────────────────
    public String extraerEmail(String token) {
        return getClaims(token).getSubject();
    }

    // ─── Extraer rol del token ───────────────────────────────────
    public String extraerRol(String token) {
        return getClaims(token).get("rol", String.class);
    }

    // ─── Validar token ───────────────────────────────────────────
    public boolean esValido(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ─── Obtener claims ──────────────────────────────────────────
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}