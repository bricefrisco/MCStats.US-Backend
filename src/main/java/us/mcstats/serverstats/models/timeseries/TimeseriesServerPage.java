package us.mcstats.serverstats.models.timeseries;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TimeseriesServerPage {
    private List<TimeseriesServerDto> servers;
    private Integer totalPages;
}
