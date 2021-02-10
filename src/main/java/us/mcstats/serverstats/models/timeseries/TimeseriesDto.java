package us.mcstats.serverstats.models.timeseries;

import java.sql.Timestamp;

public interface TimeseriesDto {
    Timestamp getT();
    Integer getO();
}
