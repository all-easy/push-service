package ru.all_easy.push.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import ru.all_easy.push.user.repository.UserEntity;
import ru.all_easy.push.web.security.model.User;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {

    private SecretKey jwtSecret;

    @PostConstruct
    void init() {
        jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateToken(UserEntity user) {
        Date date = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(user.getUid())
                .claim("fromUsername", user.getUsername())
                .setExpiration(date)
                .signWith(jwtSecret)
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts
                    .parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception ex) {
            throw new JwtException(ex.getMessage(), ex);
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();
    }

    public User getUser(String token) {
        Claims claims = getClaims(token);
        return new User(
                getUsernameFromToken(claims),
                getUIdFromToken(claims));
    }

    private String getUsernameFromToken(Claims claims) {
        return claims.get("fromUsername", String.class);
    }

    private String getUIdFromToken(Claims claims) {
        return claims.getSubject();
    }
}
