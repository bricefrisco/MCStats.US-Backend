package us.mcstats.serverstats.models.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    private String email;
    private String password;

    public boolean isValid() {
        if (email == null || email.isBlank()) return false;
        if (email.length() < 5) return false;
        if (!email.contains("@")) return false;
        return password != null && !password.isBlank() && password.length() > 8;
    }
}
