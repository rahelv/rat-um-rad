package ch.progradler.rat_um_rad.shared.protocol.coder.packet;

import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.models.game.*;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.*;
import ch.progradler.rat_um_rad.shared.protocol.coder.cards_and_decks.DestinationCardCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.cards_and_decks.WheelCardCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.game.CityCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.game.GameMapCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.game.PointCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.game.RoadCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.player.PlayerCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.player.VisiblePlayerCoder;

import java.util.List;

/**
 * Responsible for en-/decoding the {@link Packet#getContent()}
 */
public class PacketContentCoder {

    private final Coder<ChatMessage> messageCoder;
    private final Coder<UsernameChange> usernameChangeCoder;
    private final Coder<GameBase> gameBaseCoder;
    private final Coder<ClientGame> clientGameCoder;
    private final Coder<BuildRoadInfo> buildRoadInfoCoder;

    public PacketContentCoder(Coder<ChatMessage> messageCoder,
                              Coder<UsernameChange> usernameChangeCoder,
                              Coder<GameBase> gameBaseCoder,
                              Coder<ClientGame> clientGameCoder,
                              Coder<BuildRoadInfo> buildRoadInfoCoder) {
        this.messageCoder = messageCoder;
        this.usernameChangeCoder = usernameChangeCoder;
        this.gameBaseCoder = gameBaseCoder;
        this.clientGameCoder = clientGameCoder;
        this.buildRoadInfoCoder = buildRoadInfoCoder;
    }

    public static PacketContentCoder defaultPacketContentCoder() {
        Coder<GameMap> gameMapCoder = new GameMapCoder(new CityCoder(new PointCoder()), new RoadCoder());
        Coder<Activity> activityCoder = new ActivityCoder();
        ClientGameCoder clientGameCoder = new ClientGameCoder(gameMapCoder,
                new VisiblePlayerCoder(),
                new PlayerCoder(new WheelCardCoder(),
                        new DestinationCardCoder(new CityCoder(new PointCoder()))), activityCoder);
        return new PacketContentCoder(new ChatMessageCoder(),
                new UsernameChangeCoder(),
                new GameBaseCoder(gameMapCoder, activityCoder),
                clientGameCoder,
                new BuildRoadInfoCoder());
    }


    /**
     * @return encodes content and wraps it with "{}" if not null
     */
    public <T> String encodeContent(T content, ContentType contentType, int level) {
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
                return clientGameCoder.encode((ClientGame) content, level);
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
            case BUILD_ROAD_INFO -> {
                return buildRoadInfoCoder.encode((BuildRoadInfo) content, level);
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
    public Object decodeContent(String content, ContentType contentType, int level) {
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
                return clientGameCoder.decode(contentUnwrapped, level);
            }
            case GAME_INFO_LIST -> {
                return decodeGameInfoList(contentUnwrapped, level);
            }
            case GAME_STATUS -> {
                return GameStatus.valueOf(contentUnwrapped);
            }
            case BUILD_ROAD_INFO -> {
                return buildRoadInfoCoder.decode(contentUnwrapped, level);
            }
        }
        // should never happen
        throw new IllegalArgumentException("Unrecognized contentType while decoding: " + contentType);
    }

    private List<GameBase> decodeGameInfoList(String content, int level) {
        List<String> asStrings = CoderHelper.decodeStringList(level, content);
        return asStrings.stream().map((encoded) -> gameBaseCoder.decode(encoded, level + 1)).toList();
    }
}
