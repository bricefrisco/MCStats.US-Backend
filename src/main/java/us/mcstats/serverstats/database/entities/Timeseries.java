package us.mcstats.serverstats.database.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@Table(name = "timeseries")
public class Timeseries {

    @EmbeddedId
    private CompositeId id;

    @Column(name="num_online_players")
    private Integer numPlayersOnline; // Players online

    @Getter
    @Setter
    @ToString
    @Embeddable
    public static class CompositeId implements Serializable {
        @Column(name = "server_name")
        private String serverName;

        @Column(name = "time")
        private Timestamp time;
    }

}
