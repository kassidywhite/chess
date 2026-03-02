package model;
import java.util.UUID;

public record AuthData(String authToken, String username){
    public AuthData(String username){
        this(generateToken(), username);
    }
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public String getToken() {
        return authToken;
    }
}
