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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
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
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX") Date lt,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX") Date gt) {
//        ZonedDateTime zlt = lt.atZone(ZoneId.of("UTC"));
//        ZonedDateTime zgt = gt.atZone(ZoneId.of("UTC"));
        LOGGER.info("GET /timeseries - serverName:" + serverName + ", lt:" + lt + ", gt:" + gt);
//        LOGGER.info("zlt: " + zlt + ", zgt: " + zgt);

        List<TimeseriesDto> timeseriesList = new ArrayList<>();

        // TODO: Why are retries necessary in some cases? Bug work-around ahead.
        int attempt = 0;
        while (timeseriesList.size() == 0) {
            attempt += 1;
            if (attempt > 3) {
                break;
            }
            timeseriesList = timeseriesService.fetchTimeSeries(serverName, new Timestamp(lt.getTime()), new Timestamp(gt.getTime()));
        }

        return timeseriesList;
    }
}
