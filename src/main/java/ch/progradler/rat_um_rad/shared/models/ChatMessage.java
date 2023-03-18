package ch.progradler.rat_um_rad.shared.models;

import java.io.Serializable;

/**
 * Simple model class to hold data about a chat message.
 */
public class ChatMessage implements Serializable {
    final String username;
    final String message;

    public ChatMessage(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
