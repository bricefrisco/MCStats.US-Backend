package us.mcstats.serverstats.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.mcstats.serverstats.database.entities.User;
import us.mcstats.serverstats.database.repository.UserRepository;
import us.mcstats.serverstats.models.users.LoginRequest;
import us.mcstats.serverstats.models.users.LoginResponse;
import us.mcstats.serverstats.models.users.RegistrationRequest;
import us.mcstats.serverstats.models.users.RegistrationResponse;
import javax.annotation.PostConstruct;

@RestController
public class UserController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private String jwtSecretKey;
    private String jwtIssuer;

    private Algorithm algorithm;

    private static final String INVALID_REGISTRATION = "Registration request is invalid.";
    private static final String INVALID_USERNAME_PASSWORD = "Invalid username or password.";

    public UserController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(jwtSecretKey);
    }

    @PostMapping("/register")
    public RegistrationResponse register(@RequestBody RegistrationRequest request) {
        if (!request.isValid()) throw new RuntimeException(INVALID_REGISTRATION);

        String email = request.getEmail().toLowerCase();
        userRepository.save(new User(email, bCryptPasswordEncoder.encode(request.getPassword())));

        return new RegistrationResponse(generateJWT(email));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        if (!request.isValid()) throw new RuntimeException(INVALID_USERNAME_PASSWORD);


        User user = userRepository.getUserByEIgnoreCase(request.getEmail());
        if (user == null) throw new RuntimeException(INVALID_USERNAME_PASSWORD);

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getP())) {
            throw new RuntimeException(INVALID_USERNAME_PASSWORD);
        }

        return new LoginResponse(generateJWT(request.getEmail()));
    }

    private String generateJWT(String email) {
        return JWT.create().withIssuer(jwtIssuer).withClaim("email", email).sign(algorithm);
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
