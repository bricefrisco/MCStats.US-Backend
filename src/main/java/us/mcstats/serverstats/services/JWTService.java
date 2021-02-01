package us.mcstats.serverstats.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

@Service
public class JWTService {

    private Algorithm algorithm;
    private String jwtSecretKey;
    private String jwtIssuer;
    private long jwtExpiration;
    private long refreshTokenExpiration;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(jwtSecretKey);
    }

    public String validateJWTAndGetUsername(String jwt, boolean allowExpired) {
        if (jwt == null || jwt.isEmpty()) throw new RuntimeException("Missing authorization token.");
        if (!jwt.startsWith("Bearer ")) throw new RuntimeException("Token missing 'Bearer' prefix.");
        jwt = jwt.replace("Bearer ", "");
        if (allowExpired) {
            return JWT.require(algorithm).acceptExpiresAt(refreshTokenExpiration).build().verify(jwt).getSubject();
        }

        return JWT.require(algorithm).build().verify(jwt).getSubject();
    }

    public String validateJWTAndGetRole(String jwt) {
        if (jwt == null || jwt.isEmpty()) throw new RuntimeException("Missing authorization token.");
        if (!jwt.startsWith("Bearer ")) throw new RuntimeException("Token missing 'Bearer' prefix.");
        jwt = jwt.replace("Bearer ", "");

        return JWT.require(algorithm).build().verify(jwt).getClaim("role").asString();
    }

    public String generateJWT(String email, String role) {
        return JWT.create()
                .withIssuer(jwtIssuer)
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiration))
                .withSubject(email)
                .withClaim("role", role)
                .sign(algorithm);
    }

    public boolean isAnAdmin(String jwt) {
        String role = validateJWTAndGetRole(jwt);
        if (role == null) throw new RuntimeException("User has no role.");
        return role.equals("admin");
    }

    @Value("${jwt.secret-key}")
    public void setJwtSecretKey(String jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    @Value("${jwt.issuer}")
    public void setJwtIssuer(String jwtIssuer) {
        this.jwtIssuer = jwtIssuer;
    }

    @Value("${jwt.expiration}")
    public void setJwtExpiration(long jwtExpiration) {
        this.jwtExpiration = jwtExpiration;
    }

    @Value("${jwt.refresh-token-expiration}")
    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
}
