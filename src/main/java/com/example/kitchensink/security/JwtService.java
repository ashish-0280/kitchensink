package com.example.kitchensink.security;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.BlacklistToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private final BlacklistToken blacklistToken;

    public JwtService(BlacklistToken blacklistToken) {
        this.blacklistToken = blacklistToken;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Member member) {

        return Jwts.builder()
                .setSubject(member.getEmail())
                .claim("role", "ROLE_" + member.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        if(expiration.before(new Date())){
            blacklistToken.removeToken(token);
        }
        return expiration.before(new Date());
    }

    public boolean isTokenValid(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }

    public String getTokenFromCookie(HttpServletRequest request){
        if(request.getCookies() == null){
            return null;
        }

        for(Cookie cookie: request.getCookies()){
            if(cookie.getName().equals("token")){
                return cookie.getValue();
            }
        }
        return null;
    }

    //TODO Do not pass around the token and still extract the information, Hint: Use the class that you use to set the token in the first place
}