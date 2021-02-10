package us.mcstats.serverstats.database.entities;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email")
    private String email; // User email

    @Column(name = "password")
    private String password; // User password (bCrypt)

    @Column(name = "role")
    private String role;

    @Column(name = "refresh_token")
    private String refreshToken;

}
