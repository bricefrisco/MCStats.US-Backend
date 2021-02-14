package us.mcstats.serverstats.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import us.mcstats.serverstats.database.entities.Server;
import us.mcstats.serverstats.database.repository.ServerRepository;
import us.mcstats.serverstats.database.repository.TimeseriesRepository;
import us.mcstats.serverstats.models.timeseries.TimeseriesDto;
import us.mcstats.serverstats.models.timeseries.TimeseriesServerDto;
import us.mcstats.serverstats.models.timeseries.TimeseriesServerPage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TimeseriesController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesController.class);
    private static final int MAX_RESULTS = 100;
    private final TimeseriesRepository timeseriesRepository;
    private final ServerRepository serverRepository;

    public TimeseriesController(TimeseriesRepository timeseriesRepository, ServerRepository serverRepository) {
        this.timeseriesRepository = timeseriesRepository;
        this.serverRepository = serverRepository;
    }

    @GetMapping("/timeseries/batch")
    public TimeseriesServerPage fetchTimeseriesBatch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssX") Date lt,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssX") Date gt
    ) {
        LOGGER.info("GET /timeseries/batch:" + page + ", pageSize:" + pageSize);
        if (page < 0) throw new RuntimeException("Page must be greater than or equal to 0.");
        if (pageSize < 1) throw new RuntimeException("Page size must be greater than or equal to 1.");
        if (pageSize > 100) throw new RuntimeException("Page size must be less than or equal to 100.");

        Sort sort = Sort.by(("players")).descending();
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<Server> servers = serverRepository.findAll(pageable);

        List<TimeseriesServerDto> results = new ArrayList<>();
        for (Server server : servers.getContent()) {
            TimeseriesServerDto dto = new TimeseriesServerDto();
            dto.setName(server.getName());
            dto.setAddress(server.getAddress());
            dto.setImage(server.getImage());
            dto.setDescription(server.getDescription());
            dto.setOnlinePlayers(server.getPlayers());
            dto.setPeakPlayers(server.getPeakPlayers());
            dto.setPeakPlayersTime(server.getPeakPlayersTime());
            dto.setPingSuccessful(server.getPingSuccessful());

            dto.setTimeseries(fetchTimeseries(server.getName(), lt, gt));

            results.add(dto);
        }

        return new TimeseriesServerPage(results, servers.getTotalPages());
    }

    @GetMapping("/timeseries")
    public List<TimeseriesDto> fetchTimeseries(@RequestParam String serverName,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX") Date lt,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX") Date gt) {

        // The difference in milliseconds between the dates
        long ms = lt.toInstant().toEpochMilli() - gt.toInstant().toEpochMilli();

        // Interval in milliseconds between the dates
        // Time-series interval calculated by difference in milliseconds divided by the max results we would like
        long interval = ms / MAX_RESULTS;

        int intervalInSeconds = (int) Math.ceil(interval / 1000.0);

        return timeseriesRepository.selectBetween(
                serverName,
                new Timestamp(lt.getTime()),
                new Timestamp(gt.getTime()),
                intervalInSeconds
        );
    }
}
