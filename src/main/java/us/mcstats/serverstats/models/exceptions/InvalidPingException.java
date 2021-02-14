package us.mcstats.serverstats.models.exceptions;

public class InvalidPingException extends RuntimeException {
    public InvalidPingException(String message) {
        super(message);
    }
}
