package com.example.hpsus.clientauthdocsvision.Model;

public class AuthMessage {

    private long SessionToken;
    private long JwtToken;
    private String Message;

    public AuthMessage() {
    }

    public AuthMessage(long sessionToken, long jwtToken, String message) {
        SessionToken = sessionToken;
        JwtToken = jwtToken;
        Message = message;
    }

    public long getSessionToken() {
        return SessionToken;
    }

    public void setSessionToken(long sessionToken) {
        SessionToken = sessionToken;
    }

    public long getJwtToken() {
        return JwtToken;
    }

    public void setJwtToken(long jwtToken) {
        JwtToken = jwtToken;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
