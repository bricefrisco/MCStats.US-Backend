package us.mcstats.serverstats.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class JWTService {

    private Algorithm algorithm;
    private String jwtSecretKey;
    private String jwtIssuer;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(jwtSecretKey);
    }

    public String validateJWTAndGetUsername(String jwt) {
        if (jwt == null || jwt.isEmpty()) throw new RuntimeException("Missing authorization token.");
        if (!jwt.startsWith("Bearer ")) throw new RuntimeException("Token missing 'Bearer' prefix.");
        return JWT.require(algorithm).build().verify(jwt).getSubject();
    }

    public String generateJWT(String email) {
        return JWT.create().withIssuer(jwtIssuer).withSubject(email).sign(algorithm);
    }

    @Value("${jwt.secret-key}")
    public void setJwtSecretKey(String jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    @Value("${jwt.issuer}")
    public void setJwtIssuer(String jwtIssuer) {
        this.jwtIssuer = jwtIssuer;
    }
}
