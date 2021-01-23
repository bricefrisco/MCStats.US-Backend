package us.mcstats.serverstats.controllers;

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
import us.mcstats.serverstats.services.JWTService;

@RestController
public class UserController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTService jwtService;

    private static final String INVALID_REGISTRATION = "Registration request is invalid.";
    private static final String INVALID_USERNAME_PASSWORD = "Invalid username or password.";

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
        userRepository.save(new User(email, bCryptPasswordEncoder.encode(request.getPassword())));

        return new RegistrationResponse(jwtService.generateJWT(email));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        if (!request.isValid()) throw new RuntimeException(INVALID_USERNAME_PASSWORD);


        User user = userRepository.getUserByEIgnoreCase(request.getEmail());
        if (user == null) throw new RuntimeException(INVALID_USERNAME_PASSWORD);

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getP())) {
            throw new RuntimeException(INVALID_USERNAME_PASSWORD);
        }

        return new LoginResponse(jwtService.generateJWT(request.getEmail()));
    }
}
