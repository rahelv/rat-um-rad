package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.util.List;

/**
 * Responsible for en-/decoding a {@link Packet}
 */
public class PacketCoder implements Coder<Packet> {

    private final Coder<ChatMessage> messageCoder;
    private final Coder<UsernameChange> usernameChangeCoder;
    private final Coder<GameBase> gameBaseCoder;

    public PacketCoder(Coder<ChatMessage> messageCoder, Coder<UsernameChange> usernameChangeCoder, Coder<GameBase> gameBaseCoder) {
        this.messageCoder = messageCoder;
        this.usernameChangeCoder = usernameChangeCoder;
        this.gameBaseCoder = gameBaseCoder;
    }

    /**
     * @param packet
     * @param level
     * @return String encoded in our network protocol.
     * Will be in format "command-/-{encodedContent}-/-contentType"
     */
    @Override
    public String encode(Packet packet, int level) {
        ContentType contentType = packet.getContentType();
        return CoderHelper.encodeFields(level, packet.getCommand().name(),
                encodeContent(packet.getContent(), contentType, level + 1),
                contentType.name());
    }

    @Override
    public Packet decode(String encoded, int level) {
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        if (fields.size() < 3) {
            // should never happen
            throw new IllegalArgumentException("Cannot decode Packet String because it hat missing fields: " + encoded);
        }

        ContentType contentType = ContentType.valueOf(fields.get(2));
        Command command = Command.valueOf(fields.get(0));
        return new Packet(command, decodeContent(fields.get(1), contentType, level + 1), contentType);
    }

    /**
     * @return encodes content and wraps it with "{}" if not null
     */
    private <T> String encodeContent(T content, ContentType contentType, int level) {
        boolean isContentNull = content == null;
        String encodedNoWrap = encodeContentNoWrap(content, contentType, level);
        if (isContentNull) return encodedNoWrap;
        return "{" + encodedNoWrap + "}";
    }

    /**
     * @return encoded content with no "{}" wrap
     */
    private <T> String encodeContentNoWrap(T content, ContentType contentType, int level) {
        switch (contentType) {
            case CHAT_MESSAGE -> {
                return messageCoder.encode((ChatMessage) content, level);
            }
            case STRING -> {
                return (String) content;
            }
            case INTEGER -> {
                return content.toString();
            }
            case USERNAME_CHANGE -> {
                return usernameChangeCoder.encode((UsernameChange) content, level);
            }
            case GAME -> {
                // TODO: implement. User {@link ClientGameCoder}
            }
            case GAME_INFO_LIST -> {
                return encodeGameInfoList((List<GameBase>) content, level);
            }
            case GAME_STATUS -> {
                return ((GameStatus) content).name();
            }
            case NONE -> {
                return "null";
            }
            case STRING_LIST -> {
                return CoderHelper.encodeStringList(level, (List<String>) content);
            }
        }
        // should never happen
        throw new IllegalArgumentException("Unrecognized contentType while encoding: " + contentType);
    }

    private String encodeGameInfoList(List<GameBase> games, int level) {
        List<String> encodedGames = games.stream().map((g) -> gameBaseCoder.encode(g, level + 1)).toList();
        return CoderHelper.encodeStringList(level, encodedGames);
    }

    /**
     * @param content
     * @return content without "{}" around it
     */
    private String unwrapContent(String content) {
        return content.substring(1, content.length() - 1);
    }

    /**
     * @return decoded content by first removing "{}" wrap if not null
     */
    private Object decodeContent(String content, ContentType contentType, int level) {
        if (contentType == ContentType.NONE) return null;
        String contentUnwrapped = unwrapContent(content);
        switch (contentType) {
            case CHAT_MESSAGE -> {
                return messageCoder.decode(contentUnwrapped, level);
            }
            case STRING -> {
                return contentUnwrapped;
            }
            case INTEGER -> {
                return Integer.parseInt(contentUnwrapped);
            }
            case USERNAME_CHANGE -> {
                return usernameChangeCoder.decode(contentUnwrapped, level);
            }
            case STRING_LIST -> {
                return CoderHelper.decodeStringList(level, contentUnwrapped);
            }

            case GAME -> {
            }
            case GAME_INFO_LIST -> {
                return decodeGameInfoList(contentUnwrapped, level);
            }
            case GAME_STATUS -> {
                return GameStatus.valueOf(contentUnwrapped);
            }
        }
        // should never happen
        throw new IllegalArgumentException("Unrecognized contentType while decoding: " + contentType);
    }

    private List<GameBase> decodeGameInfoList(String content, int level) {
        List<String> asStrings = CoderHelper.decodeStringList(level, content);
        return asStrings.stream().map((encoded) -> gameBaseCoder.decode(encoded, level+1)).toList();
    }
}
