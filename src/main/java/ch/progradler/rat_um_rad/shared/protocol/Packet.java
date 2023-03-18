package ch.progradler.rat_um_rad.shared.protocol;

import java.io.Serializable;

/**
 * Simple model class to hold data of a packet. This object is sent between server and client.
 */
public class Packet implements Serializable { //TODO: remove Serializable and write own serialization method
    private final Command command;
    private final Serializable content;
    private final ContentType contentType;

    public Packet(Command command, Serializable content, ContentType contentType) {
        this.command = command;
        this.content = content;
        this.contentType = contentType;
    }

    public Packet(String command, Serializable content, String contentType) {
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

    //TODO: test if there's problems with the usage of getMessage, when writing new methods.

    /**
     * // TODO: as class for de- and encoding should do this stuff
     * @return String encoded in our network protocol
     */
    public String encode() {
        return "";
    }
    public String decode(String str){ //decode the String from ServerInputListener
        return "";
    }
}

