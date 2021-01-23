package us.mcstats.serverstats.database.entities;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class Timeseries {

    @Id
    private CompositeKey id;

    @Field("o")
    private Integer playersOnline; // Players online

    @Getter
    @Setter
    @ToString
    public static class CompositeKey implements Serializable {
        @Field("n")
        private String name; // Name of server
        @Field("t")
        private Timestamp timestamp; // Timestamp
    }
}
