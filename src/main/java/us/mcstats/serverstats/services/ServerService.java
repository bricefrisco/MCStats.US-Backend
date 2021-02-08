package us.mcstats.serverstats.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import us.mcstats.serverstats.models.mongo.players.MongoTotalPlayersResponse;

@Service
public class ServerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final MongoTemplate mongoTemplate;

    private static final String SUM_PLAYERS_QUERY = "{ " +
            "  aggregate: 'server', " +
            "  pipeline: [{ " +
            "    '$group': { " +
            "      '_id': '', " +
            "      'totalPlayers': { '$sum': '$p' } " +
            "    } " +
            "  }, " +
            "  { " +
            "    '$project': { " +
            "      '_id': 0, " +
            "      'totalPlayers': '$totalPlayers' " +
            "    }, " +
            "  } " +
            "  ], " +
            "  cursor: { } " +
            "}";

    public ServerService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public long fetchTotalPlayers() {
        Document document = mongoTemplate.executeCommand(SUM_PLAYERS_QUERY);
        try {
            MongoTotalPlayersResponse response = MAPPER.readValue(document.toJson(), MongoTotalPlayersResponse.class);
            return response.getCursor().getTotalPlayersList().get(0).getTotalPlayers();
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing JSON document", e);
            return 0;
        }
    }
}
