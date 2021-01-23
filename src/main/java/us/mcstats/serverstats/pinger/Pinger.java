package us.mcstats.serverstats.pinger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import us.mcstats.serverstats.database.entities.Timeseries;
import us.mcstats.serverstats.database.repository.TimeseriesRepository;
import us.mcstats.serverstats.ping.MCPing;
import us.mcstats.serverstats.ping.MCPingResponse;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.Timestamp;

@Service
public class Pinger {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pinger.class);
    private final TimeseriesRepository timeseriesRepository;

    public Pinger(TimeseriesRepository timeseriesRepository) {
        this.timeseriesRepository = timeseriesRepository;
    }

    @PostConstruct
    public void init() throws IOException {
        MCPingResponse response = MCPing.getPing("play.ecc.eco");

        Timeseries timeseries = new Timeseries();
        Timeseries.CompositeKey key = new Timeseries.CompositeKey();
        key.setName("ecocitycraft");
        key.setTimestamp(new Timestamp(System.currentTimeMillis()));
        timeseries.setId(key);
        timeseries.setPlayersOnline(response.getPlayers().getOnline());

        timeseriesRepository.save(timeseries);
    }
}
