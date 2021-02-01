package us.mcstats.serverstats.models.servers;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StatsResponse {
    private Long totalServers;
    private Long totalPlayers;
}
