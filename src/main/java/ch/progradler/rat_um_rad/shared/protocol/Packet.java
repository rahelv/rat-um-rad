package ch.progradler.rat_um_rad.shared.protocol;

import java.util.Objects;


/**
 * Simple model class to hold data of a packet. This object is sent between server and client.
 *
 * @param <Command> must be either {@link ClientCommand} or {@link ServerCommand}.
 */
public class Packet<Command> {
    private final Command command;
    private final Object content;
    private final ContentType contentType;

    private Packet(Command command, Object content, ContentType contentType) {
        this.command = command;
        this.content = content;
        this.contentType = contentType;
    }

    public Command getCommand() {
        return command;
    }

    public Object getContent() {
        return content;
    }

    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Packet)) return false;
        Packet<?> packet = (Packet<?>) o;
        return command.equals(packet.command) && Objects.equals(content, packet.content) && contentType == packet.contentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, content, contentType);
    }

    /**
     * Class that extends {@link Packet<ClientCommand>} for easy construction.
     */
    public static class Client extends Packet<ClientCommand> {
        public Client(ClientCommand clientCommand, Object content, ContentType contentType) {
            super(clientCommand, content, contentType);
        }
    }

    /**
     * Class that extends {@link Packet<ServerCommand>} for easy construction.
     */
    public static class Server extends Packet<ServerCommand> {
        public Server(ServerCommand serverCommand, Object content, ContentType contentType) {
            super(serverCommand, content, contentType);
        }
    }
}

