package us.mcstats.serverstats.controllers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import us.mcstats.serverstats.models.timeseries.TimeseriesDto;
import us.mcstats.serverstats.services.TimeseriesService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins="*")
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
        LOGGER.info("GET /timeseries - serverName:" + serverName + ", lt:" + lt + ", gt:" + gt);

        List<TimeseriesDto> timeseriesList = new ArrayList<>();

        // TODO: Why are retries necessary in some cases? Bug work-around ahead.
        int attempt = 0;
        while (timeseriesList.size() == 0) {
            attempt += 1;
            if (attempt > 3) {
                break;
            }
            timeseriesList = timeseriesService.fetchTimeSeries(serverName, Timestamp.valueOf(lt), Timestamp.valueOf(gt));
        }

        return timeseriesList;
    }
}
