package us.mcstats.serverstats.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import us.mcstats.serverstats.database.entities.Timeseries;
import us.mcstats.serverstats.database.repository.TimeseriesRepository;
import us.mcstats.serverstats.models.timeseries.TimeseriesDto;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TimeseriesController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesController.class);
    private static final String INTERVAL_STRING = "'%d seconds'";
    private static final int MAX_RESULTS = 100;
    private final TimeseriesRepository timeseriesRepository;

    public TimeseriesController(TimeseriesRepository timeseriesRepository) {
        this.timeseriesRepository = timeseriesRepository;
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

        LOGGER.info("Interval in milliseconds: " + interval);

        int intervalInSeconds = (int) Math.ceil(interval / 1000.0);

        LOGGER.info("Interval in seconds: " + intervalInSeconds);

        return timeseriesRepository.selectBetween(
                serverName,
                new Timestamp(lt.getTime()),
                new Timestamp(gt.getTime()),
                intervalInSeconds
        );
    }
}
