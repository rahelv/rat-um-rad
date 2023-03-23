package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Responsible for en-/decoding a {@link Packet}
 */
public class PacketCoder implements Coder<Packet> {

    private final Coder<ChatMessage> messageCoder;
    private final Coder<UsernameChange> usernameChangeCoder;

    static final String SEPARATOR = "-/-"; // unusual text that differs from separator used for content.

    public PacketCoder(Coder<ChatMessage> messageCoder, Coder<UsernameChange> usernameChangeCoder) {
        this.messageCoder = messageCoder;
        this.usernameChangeCoder = usernameChangeCoder;
    }

    /**
     * @return String encoded in our network protocol.
     * Will be in format "command|encodedContent|contentType"
     */
    @Override
    public String encode(Packet packet) {
        ContentType contentType = packet.getContentType();
        return packet.getCommand().name() + SEPARATOR
                + encodeContent(packet.getContent(), contentType)
                + SEPARATOR + contentType.name();
    }

    public Packet decode(String encoded) {
        String[] fields = encoded.split(SEPARATOR);
        if (fields.length < 3) {
            // should never happen
            throw new IllegalArgumentException("Cannot decode Packet String because it hat missing fields: " + encoded);
        }

        ContentType contentType = ContentType.valueOf(fields[2]);
        Command command = Command.valueOf(fields[0]);
        return new Packet(command, decodeContent(fields[1], contentType), contentType);
    }

    private <T> String encodeContent(T content, ContentType contentType) {
        switch (contentType) {
            case CHAT_MESSAGE -> {
                return messageCoder.encode((ChatMessage) content);
            }
            case STRING -> {
                return (String) content;
            }
            case USERNAME_CHANGE -> {
                return usernameChangeCoder.encode((UsernameChange) content);
            }
            case NONE -> {
                return "-";
            }
        }
        // should never happen
        // TODO: maybe throw exception?
        return null;
    }

    private Object decodeContent(String content, ContentType contentType) {
        switch (contentType) {
            case CHAT_MESSAGE -> {
                return messageCoder.decode(content);
            }
            case STRING -> {
                return content;
            }
            case USERNAME_CHANGE -> {
                return usernameChangeCoder.decode(content);
            }
            case NONE -> {
                return null;
            }
        }
        // should never happen
        // TODO: maybe throw exception?
        throw new IllegalArgumentException("Content type unknown: " + contentType);
    }
}
