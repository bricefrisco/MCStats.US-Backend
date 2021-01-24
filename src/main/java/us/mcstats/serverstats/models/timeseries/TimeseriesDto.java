package us.mcstats.serverstats.models.timeseries;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeseriesDto {
    private Timestamp date;
    private int playersOnline;
}
