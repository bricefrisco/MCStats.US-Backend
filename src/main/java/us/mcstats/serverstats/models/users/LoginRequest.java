package us.mcstats.serverstats.models.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String email;
    private String password;

    public boolean isValid() {
        if (email == null || email.isEmpty()) return false;
        if (!email.contains("@")) return false;
        if (email.length() < 5) return false;
        return password != null && password.length() > 8;
    }
}
