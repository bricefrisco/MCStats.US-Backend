package us.mcstats.serverstats.database.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "server_requests")
public class ServerRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "ip")
    private String ip;

    @Column(name = "approved")
    private Boolean approved;

    @Column(name = "email")
    private String email;
}
