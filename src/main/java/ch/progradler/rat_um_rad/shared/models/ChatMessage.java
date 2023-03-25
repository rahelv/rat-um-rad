package ch.progradler.rat_um_rad.shared.models;

import java.util.Objects;

/**
 * Simple model class to hold data about a chat message.
 */
public class ChatMessage {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return username.equals(that.username) && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, message);
    }
}
