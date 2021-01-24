package us.mcstats.serverstats.controllers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.mcstats.serverstats.database.entities.User;
import us.mcstats.serverstats.database.repository.UserRepository;
import us.mcstats.serverstats.models.users.*;
import us.mcstats.serverstats.services.JWTService;

import java.util.UUID;

@RestController
public class UserController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTService jwtService;

    private static final String INVALID_REGISTRATION = "Registration request is invalid.";
    private static final String INVALID_USERNAME_PASSWORD = "Invalid username or password.";
    private static final String INVALID_REQUEST = "Invalid request.";

    public UserController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JWTService jwtService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public RegistrationResponse register(@RequestBody RegistrationRequest request) {
        if (!request.isValid()) throw new RuntimeException(INVALID_REGISTRATION);

        User user = userRepository.getUserByEIgnoreCase(request.getEmail());
        if (user != null) throw new RuntimeException("Email already exists.");

        String email = request.getEmail().toLowerCase();

        String jwt = jwtService.generateJWT(email);
        String refreshToken = UUID.randomUUID().toString();

        user = new User();
        user.setE(email);
        user.setP(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new RegistrationResponse(jwt, refreshToken);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        if (!request.isValid()) throw new RuntimeException(INVALID_USERNAME_PASSWORD);


        User user = userRepository.getUserByEIgnoreCase(request.getEmail());
        if (user == null) throw new RuntimeException(INVALID_USERNAME_PASSWORD);

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getP())) {
            throw new RuntimeException(INVALID_USERNAME_PASSWORD);
        }

        String jwt = jwtService.generateJWT(user.getE());
        String refreshToken = UUID.randomUUID().toString();

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponse(jwt, refreshToken);
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        if (!request.isValid()) throw new RuntimeException(INVALID_REQUEST);

        String email = jwtService.validateJWTAndGetUsername(request.getJwt(), true);
        if (email == null || email.isEmpty()) throw new RuntimeException(INVALID_REQUEST);

        User user = userRepository.getUserByEIgnoreCase(email);
        if (user == null) throw new RuntimeException(INVALID_REQUEST);

        if (!user.getRefreshToken().equals(request.getRefreshToken())) throw new RuntimeException("Invalid refresh token.");

        String jwt = jwtService.generateJWT(user.getE());
        String refreshToken = UUID.randomUUID().toString();

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new RefreshTokenResponse(jwt, refreshToken);
    }
}
