package us.mcstats.serverstats.models.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cursor {
    @JsonProperty("firstBatch")
    private List<PlayerData> firstBatch;
}
