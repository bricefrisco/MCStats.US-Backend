package us.mcstats.serverstats.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import us.mcstats.serverstats.models.mongo.MongoResponse;
import us.mcstats.serverstats.models.mongo.PlayerData;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Service
public class TimeseriesController {
    // 2021-01-23T23:40:05.875+00:00
    // 2021-01-23T20:28:48.992-06:00
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesController.class);

    private static final SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
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

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void test() throws JsonProcessingException {
        timestampFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        long hours = 1000 * 60 * 60 * 3; // 60 seconds, 60 mins, twice, 1000ms

        String serverName = "EcoCityCraft";
        Timestamp twohoursago = new Timestamp(System.currentTimeMillis() - hours);
        Timestamp now = new Timestamp(System.currentTimeMillis());

        getTimeseries(serverName, now, twohoursago);
    }

    private void getTimeseries(String serverName, Timestamp lt, Timestamp gt) throws JsonProcessingException {
        long ms = lt.toInstant().toEpochMilli() - gt.toInstant().toEpochMilli();
        long interval = ms / 100;

        String query = String.format(TIMESERIES_QUERY, serverName, timestampFormatter.format(lt), timestampFormatter.format(gt), interval);

        LOGGER.info("Interval: " + interval);
        LOGGER.info("Query: " + query);

        Document doc = mongoTemplate.executeCommand(query);

        ObjectMapper mapper = new ObjectMapper();
        MongoResponse mongoResponse = mapper.readValue(doc.toJson(), MongoResponse.class);

        for (PlayerData playerData : mongoResponse.getCursor().getFirstBatch()) {
            LOGGER.info("Timestamp: " + playerData.getDateId().getDate());
            LOGGER.info("Number of players online: " + playerData.getPlayers());
        }
        LOGGER.info("Size: " + mongoResponse.getCursor().getFirstBatch().size());
    }


}
