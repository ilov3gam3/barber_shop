package org.example.barber_shop.Util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.Entity.File;
import org.example.barber_shop.Entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtil {
    private Key secretKey;

    @Value("${jwt_secret_key}")
    private String secret;
    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        secretKey = Keys.hmacShaKeyFor(decodedKey);
    }
    @Value("${access_token_expire_time}")
    private long expirationMs;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("dob", user.getDob() == null ? null : user.getDob().toString());
        claims.put("phone", user.getPhone());
        claims.put("avatar", user.getAvatar().getUrl());
        claims.put("role", user.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
    public User getUserFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        User user = new User();
        user.setId(Long.parseLong(claims.getSubject()));
        user.setName(claims.get("name", String.class));
        user.setEmail(claims.get("email", String.class));
        user.setPhone(claims.get("phone", String.class));
        user.setDob(java.sql.Date.valueOf(claims.get("dob", String.class)));
        user.setAvatar(new File(claims.get("avatar", String.class)));
        user.setRole(Role.valueOf(claims.get("role", String.class)));
        return user;
    }
    public Object getValueFromJwt(String token, String key) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get(key);
    }
}
