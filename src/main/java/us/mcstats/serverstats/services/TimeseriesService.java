package us.mcstats.serverstats.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import us.mcstats.serverstats.database.repository.TimeseriesRepository;
import us.mcstats.serverstats.models.mongo.timeseries.MongoResponse;
import us.mcstats.serverstats.models.mongo.timeseries.PlayerData;
import us.mcstats.serverstats.models.timeseries.TimeseriesDto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
public class TimeseriesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesService.class);
    private static final SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int MAX_RESULTS = 100;
    private final MongoTemplate mongoTemplate;
    private final TimeseriesRepository timeseriesRepository;

    public TimeseriesService(MongoTemplate mongoTemplate, TimeseriesRepository timeseriesRepository) {
        this.mongoTemplate = mongoTemplate;
        this.timeseriesRepository = timeseriesRepository;
        timestampFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static final String TIMESERIES_QUERY = "{ " +
            "  aggregate: 'timeseries', " +
            "  pipeline: [ " +
            "  { " +
            "    '$match': {  " +
            "      '_id.n': '%s', " +
            "      '_id.t': { " +
            "        '$lte': ISODate('%s'), " +
            "        '$gte': ISODate('%s') " +
            "      } " +
            "    } " +
            "  }, " +
            "  { " +
            "    '$group': { " +
            "      '_id': { " +
            "        '$toDate': { " +
            "          '$subtract': [ " +
            "              { '$toLong': '$_id.t' }, " +
            "              { '$mod': [ { '$toLong': '$_id.t'}, %d ] } " +
            "          ] " +
            "        }, " +
            "      }, " +
            "      'players': {'$first': '$o'} " +
            "    } " +
            "  }, " +
            "  { " +
            "    '$sort': { '_id': 1} " +
            "  } " +
            "  ], " +
            "  cursor: { } " +
            "}";

    public List<TimeseriesDto> fetchTimeSeries(String serverName, Timestamp lt, Timestamp gt) {
        // The difference in milliseconds between the dates
        long ms = lt.toInstant().toEpochMilli() - gt.toInstant().toEpochMilli();

        // Time-series interval calculated by difference in milliseconds divided by the max results we would like
        long interval = ms / MAX_RESULTS;

        LOGGER.info("Interval in seconds: " + interval / 1000);

        Document document = mongoTemplate.executeCommand(
                String.format(
                        TIMESERIES_QUERY,
                        serverName,
                        timestampFormatter.format(lt),
                        timestampFormatter.format(gt),
                        interval
                )
        );

        MongoResponse mongoResponse;

        try {
            mongoResponse = mapper.readValue(document.toJson(), MongoResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error occurred while parsing Mongo response", e);
        }

        List<TimeseriesDto> timeSeriesList = new ArrayList<>();
        for (PlayerData playerData : mongoResponse.getCursor().getFirstBatch()) {
            timeSeriesList.add(new TimeseriesDto(playerData.getDateId().getDate(), playerData.getPlayers()));
        }

        return timeSeriesList;
    }

    @Scheduled(fixedRate = 30000)
    public void purgeOldRecords() {
        LocalDateTime date = LocalDateTime.now().minusMonths(2);
        Long numRecordsDeleted = timeseriesRepository.deleteAllRecordsBefore(date);
        LOGGER.info("Purged " + numRecordsDeleted + " old records.");
    }
}
