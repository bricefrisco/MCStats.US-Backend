package us.mcstats.serverstats.models.servers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddServerRequest {
    private String name;
    private String address;

    public boolean isValid() {
        if (name == null || address == null) return false;
        if (name.isBlank() || address.isBlank()) return false;
        return address.contains(".");
    }
}
