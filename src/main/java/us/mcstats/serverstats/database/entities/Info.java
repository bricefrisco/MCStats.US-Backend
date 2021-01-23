package us.mcstats.serverstats.database.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@ToString
public class Info {

    @Id
    private String name; // Name of the server
    @Field("r")
    private Integer record; // Record of number of players online
    @Field("r2")
    private Integer dailyRecord; // Record of number of players online in a 24-hour period

}
