package us.mcstats.serverstats.database.entities;

import lombok.*;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@Table(name = "servers")
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "image")
    private String image;

    @Column(name="description")
    private String description;

    @Column(name = "online_players")
    private Integer players;

    @Column(name = "peak_players")
    private Integer peakPlayers;

    @Column(name = "peak_players_time")
    private Timestamp peakPlayersTime;

}
