package ch.progradler.rat_um_rad.shared.models;

import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;

import java.util.Objects;

public class Activity {
    private final String username;
    private final ServerCommand command;

    public Activity(String username, ServerCommand command) {
        this.username = username;
        this.command = command;
    }

    public String getUsername() {
        return username;
    }

    public ServerCommand getCommand() {
        return command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activity)) return false;
        Activity activity = (Activity) o;
        return username.equals(activity.username) && command == activity.command;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, command);
    }
}
