package us.mcstats.serverstats.database.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Server {
    @Id
    private String name;

    @Field(name = "a")
    private String address;

    @Field(name = "i")
    private String image;
}
