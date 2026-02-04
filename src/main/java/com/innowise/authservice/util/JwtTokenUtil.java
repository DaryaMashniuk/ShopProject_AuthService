package com.innowise.authservice.util;

import com.innowise.authservice.model.UserInfoDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtTokenUtil {

  @Value("${JWT_SECRET}")
  private String secretKey;
  private static final long jwtExpiration = 1800000;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public String extractRole(String token) {
    return extractClaim(token, claims -> claims.get("role", String.class));
  }

  public Long extractId(String token) {
    return extractClaim(token, claims -> claims.get("id", Long.class));
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public boolean isExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isExpired(token));
  }

  public boolean validateToken(String token) {
    try {
      extractAllClaims(token);
      return !isExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  public String generateToken(UserInfoDetails userInfoDetails) {

    String role = userInfoDetails
            .getAuthorities()
            .stream()
            .findFirst()
            .get()
            .getAuthority();

    Map<String, Object> claims = Map.of(
            "id", userInfoDetails.getId(),
            "role", role
    );
    return Jwts.builder()
            .claims()
            .add(claims)
            .subject(userInfoDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis()+ jwtExpiration))
            .and()
            .signWith(getKey())
            .compact();
  }

  private SecretKey getKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
