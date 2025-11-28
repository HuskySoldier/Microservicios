package cl.gymtastic.loginservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // En producción, esto debería estar en application.properties
    // Clave secreta segura (min 256 bits para HS256)
    private static final String SECRET = "GymtasticSecretKeySuperSeguraParaElExamen2024";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    
    // Duración del token: 24 horas
    private static final long EXPIRATION_TIME = 86400000; 

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // Guardamos el rol en el token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }
}