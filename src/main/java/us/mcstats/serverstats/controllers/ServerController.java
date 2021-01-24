package us.mcstats.serverstats.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import us.mcstats.serverstats.database.entities.Server;
import us.mcstats.serverstats.database.repository.ServerRepository;
import us.mcstats.serverstats.models.servers.AddServerRequest;
import us.mcstats.serverstats.models.servers.DeleteServerRequest;
import us.mcstats.serverstats.models.servers.ModifyServerRequest;
import us.mcstats.serverstats.pinger.Pinger;
import us.mcstats.serverstats.services.JWTService;

@RestController
public class ServerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    private final JWTService jwtService;
    private final ServerRepository serverRepository;
    private final Pinger pinger;

    public ServerController(JWTService jwtService, ServerRepository serverRepository, Pinger pinger) {
        this.jwtService = jwtService;
        this.serverRepository = serverRepository;
        this.pinger = pinger;
    }

    @GetMapping("/servers")
    public Page<Server> getServers(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int pageSize) {
        if (page < 0) throw new RuntimeException("Page must be greater than or equal to 0.");
        if (pageSize < 1) throw new RuntimeException("Page size must be greater than or equal to 1.");
        if (pageSize > 100) throw new RuntimeException("Page size must be less than or equal to 100.");

        Pageable pageable = PageRequest.of(page, pageSize);
        return serverRepository.findAll(pageable);
    }

    @PostMapping("/servers")
    public ResponseEntity<String> addServer(@RequestHeader("Authorization") String jwt, @RequestBody AddServerRequest request) {
        if (!jwtService.isAnAdmin(jwt)) throw new RuntimeException("Unauthorized.");
        if (!request.isValid()) throw new RuntimeException("Invalid server or address.");

        Server server = serverRepository.findByNameIgnoreCase(request.getName());
        if (server != null) throw new RuntimeException("Server with name '" + request.getName() + "' already exists!");

        server = new Server(request.getName(), request.getAddress(), null);
        serverRepository.save(server);
        pinger.addThread(server);
        return new ResponseEntity<>("Successfully added server '" + request.getName() + "'", HttpStatus.CREATED);
    }

    @PutMapping("/servers")
    public ResponseEntity<String> modifyServer(@RequestHeader("Authorization") String jwt, @RequestBody ModifyServerRequest request) {
        if (!jwtService.isAnAdmin(jwt)) throw new RuntimeException("Unauthorized.");
        if (!request.isValid()) throw new RuntimeException("Invalid server or address.");

        Server server = getServerIfExists(request.getName());
        server.setAddress(request.getAddress());

        serverRepository.save(server);
        pinger.updateThread(server);

        return new ResponseEntity<>("Successfully modified server '" + request.getName() + "'", HttpStatus.OK);
    }

    @DeleteMapping("/servers")
    public ResponseEntity<String> deleteServer(@RequestHeader("Authorization") String jwt, @RequestBody DeleteServerRequest request) {
        if (!jwtService.isAnAdmin(jwt)) throw new RuntimeException("Unauthorized.");

        Server server = getServerIfExists(request.name);

        serverRepository.delete(server);
        pinger.removeThread(server);

        return new ResponseEntity<>("Successfully deleted server '" + request.getName() + "'", HttpStatus.OK);
    }


    private Server getServerIfExists(String name) {
        Server server = serverRepository.findByNameIgnoreCase(name);
        if (server == null) throw new RuntimeException("Server with name '" + name + "' was not found.");
        return server;
    }
}
