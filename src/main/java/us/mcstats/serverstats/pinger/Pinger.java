package us.mcstats.serverstats.pinger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import us.mcstats.serverstats.database.entities.Server;
import us.mcstats.serverstats.database.repository.ServerRepository;
import us.mcstats.serverstats.database.repository.TimeseriesRepository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

@Service
public class Pinger {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pinger.class);

    private final ServerRepository serverRepository;
    private final TimeseriesRepository timeseriesRepository;
    private final HashMap<String, Thread> threads = new HashMap<>();

    public Pinger(ServerRepository serverRepository, TimeseriesRepository timeseriesRepository) {
        this.serverRepository = serverRepository;
        this.timeseriesRepository = timeseriesRepository;
    }

    @PostConstruct
    public void init() {
        List<Server> servers = serverRepository.findAll();
        for (Server server : servers) {
            Thread thread = new PingerThread(server, timeseriesRepository, serverRepository);
            threads.put(server.getName(), thread);
            thread.start();
        }

        LOGGER.info("Started pinger thread for each server, total: " + servers.size());
    }

    public void addThread(Server server) {
        Thread thread = threads.get(server.getName());

        if (thread != null) {
            LOGGER.warn("Thread for server " + server + " already exists.");
            return;
        }

        thread = new PingerThread(server, timeseriesRepository, serverRepository);
        threads.put(server.getName(), thread);
        thread.start();

        LOGGER.info("Added thread for server '" + server.getName() + "'");
    }

    public void removeThread(Server server) {
        Thread thread = threads.get(server.getName());
        if (thread == null) {
            LOGGER.warn("Thread for server '" + server.getName() + "' was not found.");
            return;
        }

        thread.interrupt();
        threads.remove(server.getName());

        LOGGER.info("Removed thread for '" + server.getName() + "'");
    }

    public void updateThread(Server server) {
        Thread thread = threads.get(server.getName());

        if (thread != null) {
            removeThread(server);
        }

        addThread(server);

        LOGGER.info("Updated thread for server '" + server.getName() + "'");
    }
}
