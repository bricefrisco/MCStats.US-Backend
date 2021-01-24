package us.mcstats.serverstats.models.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {
    private String jwt;
    private String refreshToken;

    public boolean isValid() {
        if (jwt == null || jwt.isEmpty()) return false;
        return refreshToken != null && !refreshToken.isEmpty();
    }
}
