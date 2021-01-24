package us.mcstats.serverstats.pinger;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.mcstats.serverstats.database.entities.Server;
import us.mcstats.serverstats.database.entities.Timeseries;
import us.mcstats.serverstats.database.repository.ServerRepository;
import us.mcstats.serverstats.database.repository.TimeseriesRepository;
import us.mcstats.serverstats.ping.MCPing;
import us.mcstats.serverstats.ping.MCPingResponse;
import java.sql.Timestamp;
import java.util.Random;

public class PingerThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(PingerThread.class);

    private final ServerRepository serverRepository;
    private final TimeseriesRepository repository;
    private final String serverName;
    private final String serverAddress;

    public PingerThread(String serverName, String serverAddress, TimeseriesRepository timeseriesRepository, ServerRepository serverRepository) {
        LOGGER.info("Thread started for server " + serverName + " - " + serverAddress);
        this.serverName = serverName;
        this.serverAddress = serverAddress;
        this.repository = timeseriesRepository;
        this.serverRepository = serverRepository;
    }

    @SneakyThrows
    public void run() {
        int rand = new Random().nextInt(60) + 1;
        LOGGER.info("Sleeping initially for " + rand + " seconds");
        Thread.sleep(rand * 1000);

        boolean updatedImage = false;

        while (true) {
            try {
                MCPingResponse response = MCPing.getPing(serverAddress);
                Timeseries timeseries = new Timeseries();
                Timeseries.CompositeKey key = new Timeseries.CompositeKey();
                key.setName(serverName);
                key.setTimestamp(new Timestamp(System.currentTimeMillis()));
                timeseries.setId(key);
                timeseries.setPlayersOnline(response.getPlayers().getOnline());

                LOGGER.info("Pinged server " + serverName + " (players online: " + response.getPlayers().getOnline() + ") saving timeseries: " + timeseries.toString());

                if (!updatedImage) {
                    updatedImage = true;

                    LOGGER.info("Updating image for server '" + serverName + "'");

                    Server server = serverRepository.findByNameIgnoreCase(serverName);
                    if (server == null) continue;
                    server.setImage(response.getFavicon());

                    serverRepository.save(server);
                }

                repository.save(timeseries);
            } catch (Exception e) {
                LOGGER.info("Failed to ping " + serverAddress + " - " + e.getMessage());
            } finally {
                Thread.sleep(60 * 1000);
            }
        }
    }
}
