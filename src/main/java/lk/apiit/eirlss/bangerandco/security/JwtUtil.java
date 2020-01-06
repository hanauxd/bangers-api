package lk.apiit.eirlss.bangerandco.security;

import io.jsonwebtoken.*;
import lk.apiit.eirlss.bangerandco.exceptions.JsonWebTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${app.token.secret}")
    private String SECRET_KEY;
    @Value("${app.token.expiry-time}")
    private long EXPIRY_TIME;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        } catch (SignatureException ex) {
            throw new JsonWebTokenException("Invalid JWT signature.");
        } catch (MalformedJwtException ex) {
            throw new JsonWebTokenException("Invalid JWT token.");
        } catch (ExpiredJwtException ex) {
            throw new JsonWebTokenException("Expired JWT token.");
        } catch (UnsupportedJwtException ex) {
            throw new JsonWebTokenException("Unsupported JWT token.");
        } catch (IllegalArgumentException ex) {
            throw new JsonWebTokenException("JWT claims string is empty.");
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRY_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
