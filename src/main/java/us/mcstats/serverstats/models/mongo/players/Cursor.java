package us.mcstats.serverstats.models.mongo.players;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cursor {
    @JsonProperty("firstBatch")
    private List<TotalPlayers> totalPlayersList;
}
