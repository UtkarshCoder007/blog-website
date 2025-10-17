package com.example.blog_website.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;

@Component
public class JwtUtil {
    private final String secret = "my-super-secret-key-which-should-be-very-long";
    private final Key secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    private final long expiration = 3600000;
    public String generateToken(String email, Collection<? extends GrantedAuthority> authorities){

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();

    }
    public String extractEmail(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<String> extractRoles(String token){
        return (List<String>) Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles");
    }
    public boolean validateToken(String token, String email){
        try{
            String tokenEmail = extractEmail(token);
            return tokenEmail.equals(email);
        }
        catch (Exception e){
            return false;
        }
    }
}
