package us.mcstats.serverstats.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import us.mcstats.serverstats.database.entities.Server;
import us.mcstats.serverstats.database.repository.ServerRepository;
import us.mcstats.serverstats.database.repository.TimeseriesRepository;
import us.mcstats.serverstats.models.GenericResponse;
import us.mcstats.serverstats.models.servers.*;
import us.mcstats.serverstats.pinger.Pinger;
import us.mcstats.serverstats.services.JWTService;
import us.mcstats.serverstats.services.ServerService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins={"http://localhost:3000"})
public class ServerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    private final ServerService serverService;
    private final JWTService jwtService;
    private final ServerRepository serverRepository;
    private final TimeseriesRepository timeseriesRepository;
    private final Pinger pinger;

    public ServerController(ServerService serverService, JWTService jwtService, ServerRepository serverRepository, TimeseriesRepository timeseriesRepository, Pinger pinger) {
        this.serverService = serverService;
        this.jwtService = jwtService;
        this.serverRepository = serverRepository;
        this.timeseriesRepository = timeseriesRepository;
        this.pinger = pinger;
    }

    @GetMapping("/server")
    public Server getServer(@RequestParam String name) {
        LOGGER.info("GET /server - name:" + name);
        return serverRepository.findByNameIgnoreCase(name);
    }

    @GetMapping("/servers")
    public Page<Server> getServers(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int pageSize) {
        LOGGER.info("GET /servers - page:" + page + ", pageSize:" + pageSize);
        if (page < 0) throw new RuntimeException("Page must be greater than or equal to 0.");
        if (pageSize < 1) throw new RuntimeException("Page size must be greater than or equal to 1.");
        if (pageSize > 100) throw new RuntimeException("Page size must be less than or equal to 100.");

        Sort sort = Sort.by(("players")).descending();
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return serverRepository.findAll(pageable);
    }

    @GetMapping("/server-names")
    public List<String> getServerNames() {
        LOGGER.info("GET /server-names");
        List<Server> servers = serverRepository.findAll();
        return servers.stream().map(Server::getName).sorted().collect(Collectors.toList());
    }

    @PostMapping("/servers")
    public GenericResponse addServer(@RequestHeader("Authorization") String jwt, @RequestBody AddServerRequest request) {
        LOGGER.info("POST /servers - " + request);
        if (!jwtService.isAnAdmin(jwt)) throw new RuntimeException("Unauthorized.");
        if (!request.isValid()) throw new RuntimeException("Invalid server or address.");

        Server server = serverRepository.findByNameIgnoreCase(request.getName());
        if (server != null) throw new RuntimeException("Server with name '" + request.getName() + "' already exists!");

        server = new Server();
        server.setName(request.getName());
        server.setAddress(request.getAddress());

        serverRepository.save(server);
        pinger.addThread(server);
        return new GenericResponse("Successfully added server '" + request.getName() + "'");
    }

    @PutMapping("/servers")
    public GenericResponse modifyServer(@RequestHeader("Authorization") String jwt, @RequestBody ModifyServerRequest request) {
        LOGGER.info("PUT /servers - " + request);
        if (!jwtService.isAnAdmin(jwt)) throw new RuntimeException("Unauthorized.");
        if (!request.isValid()) throw new RuntimeException("Invalid server or address.");

        Server server = getServerIfExists(request.getName());
        server.setAddress(request.getAddress());

        serverRepository.save(server);
        pinger.updateThread(server);

        return new GenericResponse("Successfully modified server '" + request.getName() + "'");
    }

    @PutMapping("/servers/refresh")
    public GenericResponse refreshServer(@RequestHeader("Authorization") String jwt, @RequestBody RefreshServerRequest request) {
        LOGGER.info("PUT /servers/refresh - " + request);
        if (!jwtService.isAnAdmin(jwt)) throw new RuntimeException("Unauthorized.");

        Server server = getServerIfExists(request.getName());
        pinger.updateThread(server);

        return new GenericResponse("Successfully refreshed thread for server '" + request.getName() + "'");
    }

    @DeleteMapping("/servers")
    public GenericResponse deleteServer(@RequestHeader("Authorization") String jwt, @RequestBody DeleteServerRequest request) {
        LOGGER.info("DELETE /servers - " + request);
        if (!jwtService.isAnAdmin(jwt)) throw new RuntimeException("Unauthorized.");

        Server server = getServerIfExists(request.getName());
        serverRepository.delete(server);
        Long recordsRemoved = timeseriesRepository.deleteRecordsByServerName(request.getName());
        pinger.removeThread(server);

        return new GenericResponse("Successfully deleted server '" + request.getName() + "' and " + recordsRemoved + " records.");
    }

    @GetMapping("/stats")
    public StatsResponse getTotalPlayers() {
        LOGGER.info("GET /stats");
        return new StatsResponse(serverRepository.count(), serverService.fetchTotalPlayers());
    }


    private Server getServerIfExists(String name) {
        Server server = serverRepository.findByNameIgnoreCase(name);
        if (server == null) throw new RuntimeException("Server with name '" + name + "' was not found.");
        return server;
    }
}
