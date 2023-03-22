package ch.progradler.rat_um_rad.shared.protocol;

import java.io.Serializable;

/**
 * Simple model class to hold data of a packet. This object is sent between server and client.
 */
public class Packet implements Serializable { //TODO: remove Serializable and write own serialization method
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
}

