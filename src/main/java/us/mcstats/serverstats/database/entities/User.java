package us.mcstats.serverstats.database.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String e; // User email

    @Indexed
    private String p; // User password (bCrypt)

    @Field("ro")
    private String role;

    @Field("r")
    private String refreshToken;
}
