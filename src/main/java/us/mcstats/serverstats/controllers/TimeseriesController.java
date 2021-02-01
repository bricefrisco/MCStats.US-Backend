package us.mcstats.serverstats.controllers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import us.mcstats.serverstats.database.entities.Timeseries;
import us.mcstats.serverstats.models.timeseries.TimeseriesDto;
import us.mcstats.serverstats.services.TimeseriesService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins={"http://localhost:3000"})
public class TimeseriesController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesController.class);

    private final TimeseriesService timeseriesService;

    public TimeseriesController(TimeseriesService timeseriesService) {
        this.timeseriesService = timeseriesService;
    }

    @GetMapping("/timeseries")
    public List<TimeseriesDto> fetchTimeseries(@RequestParam String serverName,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lt,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime gt) {
        return timeseriesService.fetchTimeSeries(serverName, Timestamp.valueOf(lt), Timestamp.valueOf(gt));
    }
}
