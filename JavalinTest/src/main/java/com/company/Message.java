package com.company;

public class Message {
    private String username;
    private String message;
    Message(String username, String message) {
        this.username = username;
        this.message = message;
    }
    void setMessage(String message) {
            this.message = message;
    }
    void setUsername(String username) {
        this.username = username;
    }
    String getUsername() {
        return username;
    }
    String getMessage() {
        return message;
    }

}
