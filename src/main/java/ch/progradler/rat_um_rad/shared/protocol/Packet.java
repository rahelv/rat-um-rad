package ch.progradler.rat_um_rad.shared.protocol;

import java.util.Objects;

/**
 * Simple model class to hold data of a packet. This object is sent between server and client.
 */
public class Packet {
    private final Command command;
    private final Object content;
    private final ContentType contentType;

    public Packet(Command command, Object content, ContentType contentType) {
        this.command = command;
        this.content = content;
        this.contentType = contentType;
    }

    public Packet(String command, Object content, String contentType) {
        this(Command.valueOf(command), content, ContentType.valueOf(contentType));
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
        if (o == null || getClass() != o.getClass()) return false;
        Packet packet = (Packet) o;
        return command == packet.command && Objects.equals(content, packet.content) && contentType == packet.contentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, content, contentType);
    }
}

