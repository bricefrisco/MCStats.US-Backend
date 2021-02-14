package us.mcstats.serverstats.models.timeseries;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@ToString
public class TimeseriesServerDto {
    private String name;
    private String address;
    private String image;
    private String description;
    private Integer onlinePlayers;
    private Integer peakPlayers;
    private Timestamp peakPlayersTime;
    private Boolean pingSuccessful;
    List<TimeseriesDto> timeseries;
}
