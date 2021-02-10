package us.mcstats.serverstats.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.mcstats.serverstats.database.entities.User;
import us.mcstats.serverstats.database.repository.UserRepository;
import us.mcstats.serverstats.models.users.*;
import us.mcstats.serverstats.services.JWTService;

import java.util.UUID;

@RestController
@CrossOrigin(origins="*")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static final String INVALID_REGISTRATION = "Registration request is invalid.";
    private static final String INVALID_USERNAME_PASSWORD = "Invalid username or password.";
    private static final String INVALID_REQUEST = "Invalid request.";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTService jwtService;

    public UserController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JWTService jwtService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public RegistrationResponse register(@RequestBody RegistrationRequest request) {
        LOGGER.info("POST /register - email:" + request.getEmail());
        if (!request.isValid()) throw new RuntimeException(INVALID_REGISTRATION);

        User user = userRepository.getUserByEmailIgnoreCase(request.getEmail());
        if (user != null) throw new RuntimeException("Email already exists.");

        String email = request.getEmail().toLowerCase();

        String jwt = jwtService.generateJWT(email, "user");
        String refreshToken = UUID.randomUUID().toString();

        user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new RegistrationResponse(jwt, refreshToken);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        LOGGER.info("POST /login - email:" + request.getEmail());

        if (!request.isValid()) throw new RuntimeException(INVALID_USERNAME_PASSWORD);
        
        User user = userRepository.getUserByEmailIgnoreCase(request.getEmail());
        if (user == null) throw new RuntimeException(INVALID_USERNAME_PASSWORD);

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException(INVALID_USERNAME_PASSWORD);
        }

        String jwt = jwtService.generateJWT(user.getEmail(), user.getRole());
        String refreshToken = UUID.randomUUID().toString();

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponse(jwt, refreshToken);
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        LOGGER.info("POST /refresh");
        if (!request.isValid()) throw new RuntimeException(INVALID_REQUEST);

        String email = jwtService.validateJWTAndGetUsername(request.getJwt(), true);
        if (email == null || email.isEmpty()) throw new RuntimeException(INVALID_REQUEST);

        User user = userRepository.getUserByEmailIgnoreCase(email);
        if (user == null) throw new RuntimeException(INVALID_REQUEST);

        if (!user.getRefreshToken().equals(request.getRefreshToken())) throw new RuntimeException("Invalid refresh token.");

        String jwt = jwtService.generateJWT(user.getEmail(), user.getRole());
        String refreshToken = UUID.randomUUID().toString();

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new RefreshTokenResponse(jwt, refreshToken);
    }
}
