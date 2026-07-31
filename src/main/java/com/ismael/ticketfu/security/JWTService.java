package com.ismael.ticketfu.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    //agarra la secreKey de applications.prop...
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;


    // Constructor con manejo interno de excepciones (try-catch)

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }



    /**
     * Crear el token con el username(email)
     * @param username el email del Usuario.
     * @return UN token correspndiente al email
     */
    public String generarToken(String username) {
        Map<String, Object> mapa = new HashMap<>();


        return Jwts.builder()
                .claims()
                .add(mapa)
                .subject(username) // el dueño dle token(email)
                .issuedAt(new Date(System.currentTimeMillis())) //fecha de creacion que es la actual
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60)) // caducacion 1 hora.
                .and()
                .signWith(getKey())
                .compact();

    }


    public String extractUsername(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * Desencripta y lee el token
     * @param token token del email
     * @return el contenido del tokemn
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getKey()) // verifica con la clave
                .build()
                .parseSignedClaims(token) //  ve si fue alterado o ya no sirve
                .getPayload(); // regresa el contenido del token
    }

    /**
     * Valida el token con todos sus datos
     * @param token el token del email
     * @param userDetails la entidad
     * @return
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }



}