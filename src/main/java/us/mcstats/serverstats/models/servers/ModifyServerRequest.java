package us.mcstats.serverstats.models.servers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyServerRequest {
    private String name;
    private String address;

    public boolean isValid() {
        return name != null && address != null && !name.isBlank() && !address.isBlank();
    }
}
