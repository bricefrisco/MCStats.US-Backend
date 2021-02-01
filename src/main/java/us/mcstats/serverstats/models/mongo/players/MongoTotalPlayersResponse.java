package us.mcstats.serverstats.models.mongo.players;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MongoTotalPlayersResponse {
    private Cursor cursor;
}
