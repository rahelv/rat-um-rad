package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.*;
import ch.progradler.rat_um_rad.shared.models.game.*;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.protocol.coder.packet.ClientPacketCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.packet.PacketCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.packet.ServerPacketCoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PacketCoderTest {
    @Mock
    Coder<ChatMessage> messageCoderMock;
    @Mock
    Coder<UsernameChange> usernameChangeCoder;
    @Mock
    Coder<GameBase> gameBaseCoder;
    @Mock
    Coder<ClientGame> clientGameCoder;
    @Mock
    Coder<BuildRoadInfo> buildRoadInfoCoder;

    private PacketCoder<ClientCommand> clientPacketCoder;
    private PacketCoder<ServerCommand> serverPacketCoder;

    @BeforeEach
    public void initPacketCoder() {
        clientPacketCoder = new ClientPacketCoder(messageCoderMock, usernameChangeCoder, gameBaseCoder, clientGameCoder, buildRoadInfoCoder);
        serverPacketCoder = new ServerPacketCoder(messageCoderMock, usernameChangeCoder, gameBaseCoder, clientGameCoder, buildRoadInfoCoder);
    }

    @Test
    public void defaultCoderCanCodeClientGame() {
        // prepare
        int level = 1;

        Player player = new Player("own player", WheelColor.BLUE, 4, 5, 1,
                Arrays.asList(new WheelCard(40)), new DestinationCard(
                "longCard1", new City("city1", "City1", new Point(3, 4)),
                new City("city2", "City2", new Point(1, 4)),
                5), new ArrayList<>());
        List<Activity> activities = Collections.singletonList(new Activity("John", ServerCommand.GAME_JOINED));

        ClientGame clientGame = new ClientGame("game1", GameStatus.STARTED, GameMap.defaultMap(),
                new Date(2023, Calendar.MAY, 14, 4, 4, 4), "creator", 4, Arrays.asList(
                new VisiblePlayer("Player1", WheelColor.GREEN, 4, 20, 0, "ip", 5, 3)
        ), player, 30, new HashMap<>(), activities);


        ServerCommand command = ServerCommand.GAME_CREATED;
        ContentType contentType = ContentType.GAME;
        Packet.Server packet = new Packet.Server(command, clientGame, contentType);

        serverPacketCoder = PacketCoder.defaultServerPacketCoder();

        // execute
        String encoded = serverPacketCoder.encode(packet, level);
        Packet<ServerCommand> decoded = serverPacketCoder.decode(encoded, level);

        // assert
        assertEquals(packet, decoded);
    }

    @Test
    public void encodeReturnsStringWithDataInOrderAndSeparatedByCorrectSeparator() {
        // prepare
        int level = 2;
        ChatMessage content = new ChatMessage("userA", "Hi!");

        when(messageCoderMock.encode(content, level + 1)).thenReturn("username:userA,message:Hi!");


        ClientCommand command = ClientCommand.SEND_BROADCAST_CHAT;
        ContentType contentType = ContentType.CHAT_MESSAGE;
        Packet.Client packet = new Packet.Client(command, content, contentType);

        // execute
        String result = clientPacketCoder.encode(packet, level);

        // assert
        String expected = CoderHelper.encodeFields(level, command.name(),
                "{" + messageCoderMock.encode(content, level + 1) + "}",
                contentType.name());
        assertEquals(expected, result);
    }

    @Test
    public void encodeReturnsCorrectStringIfContentTypeIsNone() {
        int level = 0;
        ClientCommand command = ClientCommand.PONG;
        ContentType contentType = ContentType.NONE;

        Packet.Client packet = new Packet.Client(command, null, contentType);
        String result = clientPacketCoder.encode(packet, level);

        String expected = CoderHelper.encodeFields(level, command.name(), "null", contentType.name());
        assertEquals(expected, result);
    }

    @Test
    public void decodeReturnsPacketWithContentNullIfContentEmptyTypeIsNone() {
        int level = 0;
        ServerCommand command = ServerCommand.PING;
        ContentType contentType = ContentType.NONE;

        String packetEncoded = CoderHelper.encodeFields(level, command.name(), "null", contentType.name());

        // execute
        Packet<ServerCommand> result = serverPacketCoder.decode(packetEncoded, level);

        // assert
        Packet.Server expected = new Packet.Server(command, null, contentType);

        assertEquals(expected, result);
    }

    @Test
    public void decodeReturnsCorrectTypeWithCorrectData() {
        // prepare
        int level = 0;

        String messageEncoded = "username:userA,message:Hi!";
        ChatMessage expectedMessage = new ChatMessage("userA", "Hi!");
        when(messageCoderMock.decode(messageEncoded, level + 1)).thenReturn(expectedMessage);

        ClientCommand command = ClientCommand.SEND_BROADCAST_CHAT;
        ContentType contentType = ContentType.CHAT_MESSAGE;

        String packetEncoded = CoderHelper.encodeFields(level,
                command.name(),
                "{" + messageEncoded + "}",
                contentType.name());

        // execute
        Packet<ClientCommand> result = clientPacketCoder.decode(packetEncoded, level);

        // assert
        Packet.Client expected = new Packet.Client(command, expectedMessage, contentType);

        assertEquals(expected, result);
    }

    @Test
    public void encodeWorksForString() {
        // prepare
        int level = 0;

        String username = "userA";

        ClientCommand command = ClientCommand.REGISTER_USER;
        ContentType contentType = ContentType.STRING;

        Packet.Client packet = new Packet.Client(command, username, contentType);

        // execute
        String result = clientPacketCoder.encode(packet, level);

        // assert
        String expected = CoderHelper.encodeFields(level,
                command.name(),
                "{" + username + "}",
                contentType.name());

        assertEquals(expected, result);
    }

    @Test
    public void decodeWorksForString() {
        // prepare
        int level = 0;

        String username = "userA";

        ClientCommand command = ClientCommand.REGISTER_USER;
        ContentType contentType = ContentType.STRING;

        String packetEncoded = CoderHelper.encodeFields(level,
                command.name(),
                "{" + username + "}",
                contentType.name());

        // execute
        Packet<ClientCommand> result = clientPacketCoder.decode(packetEncoded, level);

        // assert
        Packet.Client expected = new Packet.Client(command, username, contentType);

        assertEquals(expected, result);
    }

    @Test
    public void encodeWorksForInt() {
        // prepare
        int level = 0;

        int content = 5;

        ClientCommand command = ClientCommand.CREATE_GAME;
        ContentType contentType = ContentType.INTEGER;


        Packet.Client packet = new Packet.Client(command, content, contentType);

        // execute
        String result = clientPacketCoder.encode(packet, level);

        // assert
        String expected = CoderHelper.encodeFields(level,
                command.name(),
                "{" + content + "}",
                contentType.name());

        assertEquals(expected, result);
    }

    @Test
    public void decodeWorksForInt() {
        // prepare
        int level = 0;

        int content = 5;

        ClientCommand command = ClientCommand.CREATE_GAME;
        ContentType contentType = ContentType.INTEGER;

        String packetEncoded = CoderHelper.encodeFields(level, command.name(),
                "{" + content + "}",
                contentType.name());

        // execute
        Packet<ClientCommand> result = clientPacketCoder.decode(packetEncoded, level);

        // assert
        Packet.Client expected = new Packet.Client(command, content, contentType);

        assertEquals(expected, result);
    }

    @Test
    public void encodeWorksForGameStatus() {
        // prepare
        int level = 0;

        GameStatus content = GameStatus.FINISHED;

        ClientCommand command = ClientCommand.REQUEST_GAMES;
        ContentType contentType = ContentType.GAME_STATUS;


        Packet.Client packet = new Packet.Client(command, content, contentType);

        // execute
        String result = clientPacketCoder.encode(packet, level);

        // assert
        String expected = CoderHelper.encodeFields(level,
                command.name(),
                "{" + content.name() + "}",
                contentType.name());

        assertEquals(expected, result);
    }

    @Test
    public void decodeWorksForGameStatus() {
        // prepare
        int level = 0;

        GameStatus content = GameStatus.FINISHED;

        ClientCommand command = ClientCommand.REQUEST_GAMES;
        ContentType contentType = ContentType.GAME_STATUS;

        String packetEncoded = CoderHelper.encodeFields(level, command.name(),
                "{" + content.name() + "}",
                contentType.name());

        // execute
        Packet<ClientCommand> result = clientPacketCoder.decode(packetEncoded, level);

        // assert
        Packet.Client expected = new Packet.Client(command, content, contentType);

        assertEquals(expected, result);
    }

    @Test
    public void encodeWorksForGameInfoList() {
        // prepare
        int level = 0;

        Date createdAt1 = new Date(2022, Calendar.JUNE, 4, 9, 44, 50);
        Date createdAt2 = new Date(2022, Calendar.JUNE, 5, 0, 0, 0);

        List<GameBase> content = Arrays.asList(
                new GameBase("game1", GameStatus.STARTED, GameMap.defaultMap(), createdAt1, "creator1", 5, 3, new HashMap<>(), new ArrayList<>()),
                new GameBase("game2", GameStatus.STARTED, GameMap.defaultMap(), createdAt2, "creator2", 4, 0, new HashMap<>(), new ArrayList<>())
        );

        ServerCommand command = ServerCommand.SEND_STARTED_GAMES;
        ContentType contentType = ContentType.GAME_INFO_LIST;


        Packet.Server packet = new Packet.Server(command, content, contentType);

        // execute
        String result = serverPacketCoder.encode(packet, level);

        String expectedContent = CoderHelper.encodeStringList(level + 1,
                content.stream().map((g) -> gameBaseCoder.encode(g, level + 2)).toList());
        // assert
        String expected = CoderHelper.encodeFields(level,
                command.name(),
                "{" + expectedContent + "}",
                contentType.name());

        assertEquals(expected, result);
    }

    @Test
    public void decodeWorksForGameInfoList() {
        // prepare
        int level = 0;

        Date createdAt1 = new Date(2022, Calendar.JUNE, 4, 9, 44, 50);
        Date createdAt2 = new Date(2022, Calendar.JUNE, 5, 0, 0, 0);

        GameBase game1 = new GameBase("game1", GameStatus.STARTED, GameMap.defaultMap(), createdAt1, "creator1", 5, 3, new HashMap<>(), new ArrayList<>());
        GameBase game2 = new GameBase("game2", GameStatus.STARTED, GameMap.defaultMap(), createdAt2, "creator2", 4, 0, new HashMap<>(), new ArrayList<>());


        ServerCommand command = ServerCommand.SEND_STARTED_GAMES;
        ContentType contentType = ContentType.GAME_INFO_LIST;

        String game1Encoded = "game1Encoded";
        String game2Encoded = "game2Encoded";

        String packetEncoded = CoderHelper.encodeFields(level, command.name(),
                "{" + CoderHelper.encodeStringList(level + 1, Arrays.asList(game1Encoded, game2Encoded)) + "}",
                contentType.name());

        when(gameBaseCoder.decode(game1Encoded, level + 2)).thenReturn(game1);
        when(gameBaseCoder.decode(game2Encoded, level + 2)).thenReturn(game2);

        // execute
        Packet<ServerCommand> result = serverPacketCoder.decode(packetEncoded, level);

        // assert
        Packet.Server expected = new Packet.Server(command, Arrays.asList(game1, game2), contentType);

        assertEquals(expected, result);
    }

    @Test
    void encodeWorksForListStringList() {

        // prepare
        List<String> content = new LinkedList<>();
        content.add("string1");
        content.add("string2");
        ServerCommand command = ServerCommand.SEND_ALL_CONNECTED_PLAYERS;
        ContentType contentType = ContentType.STRING_LIST;

        Packet.Server packet = new Packet.Server(command, content, contentType);
        int level = 0;

        //execute
        String result = serverPacketCoder.encode(packet, level);

        String expectedContent = CoderHelper.encodeStringList(level + 1, content);

        // assert
        String expected = CoderHelper.encodeFields(level,
                command.name(), "{" + expectedContent + "}",
                contentType.name());

        assertEquals(expected, result);
    }

    @Test
    void decodeWorksForStringList() {

        // prepare
        List<String> content = new LinkedList<>();
        content.add("string1");
        content.add("string2");
        ServerCommand command = ServerCommand.SEND_ALL_CONNECTED_PLAYERS;
        ContentType contentType = ContentType.STRING_LIST;

        int level = 0;

        String packetEncoded = CoderHelper.encodeFields(level, command.name(),
                "{" + CoderHelper.encodeStringList(level + 1, content) + "}",
                contentType.name());

        //execute
        Packet<ServerCommand> result = serverPacketCoder.decode(packetEncoded, level);

        //assert
        Packet.Server expected = new Packet.Server(command, content, contentType);
        assertEquals(expected, result);
    }

    @Test
    public void encodeWorksForBuildRoadInfo() {
        // prepare
        int level = 2;
        String roadId = "road1";
        WheelColor color = WheelColor.WHITE;
        BuildRoadInfo content = new BuildRoadInfo(roadId, color);

        String encoded = "encoded";
        when(buildRoadInfoCoder.encode(content, level + 1))
                .thenReturn(encoded);

        ClientCommand command = ClientCommand.BUILD_ROAD;
        ContentType contentType = ContentType.BUILD_ROAD_INFO;
        Packet.Client packet = new Packet.Client(command, content, contentType);

        // execute
        String result = clientPacketCoder.encode(packet, level);

        // assert
        String expected = CoderHelper.encodeFields(level, command.name(),
                "{" + encoded + "}",
                contentType.name());
        assertEquals(expected, result);
    }

    @Test
    public void decodeWorksForBuildRoadInfo() {
        // prepare
        int level = 2;
        String roadId = "road1";
        WheelColor color = WheelColor.WHITE;
        BuildRoadInfo buildRoadInfo = new BuildRoadInfo(roadId, color);

        String encodedContent = "encoded";
        when(buildRoadInfoCoder.decode(encodedContent, level + 1))
                .thenReturn(buildRoadInfo);

        ClientCommand command = ClientCommand.BUILD_ROAD;
        ContentType contentType = ContentType.BUILD_ROAD_INFO;
        Packet.Client packet = new Packet.Client(command, buildRoadInfo, contentType);


        String encoded = CoderHelper.encodeFields(level, command.name(),
                "{" + encodedContent + "}",
                contentType.name());

        // execute
        Packet<ClientCommand> result = clientPacketCoder.decode(encoded, level);

        // assert
        assertEquals(packet, result);
    }
}
