package us.mcstats.serverstats.models.timeseries;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class TimeseriesRequest {
    private String serverName;
    private Timestamp lt;
    private Timestamp gt;
}
