package com.MooneyB;

//import java.util.Date;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;

//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.JwtParser;
//import io.jsonwebtoken.security.Keys;
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;

//@Component
public class JwtUtil {
//	
//    @Value("${jwt.secret}")
//    private String secretKey;
//
//    @Value("${jwt.expiration}")
//    private long expiration;
//
//    private SecretKey getSigningKey() {
//        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//    
//    public String generateToken(String username) {
//        return Jwts.builder()
//            .setSubject(username)
//            .setIssuedAt(new Date())
//            .setExpiration(new Date(System.currentTimeMillis() + expiration))
//            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//            .compact();
//    }
//
//    public Claims validateToken(String token) throws JwtException {
//        JwtParser parser = Jwts.parserBuilder()
//            .setSigningKey(getSigningKey())
//            .build();
//
//        return parser.parseClaimsJws(token).getBody();
//    }
}
