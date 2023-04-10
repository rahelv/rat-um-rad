package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    private PacketCoder packetCoder;

    @BeforeEach
    public void initPacketCoder() {
        packetCoder = new PacketCoder(messageCoderMock, usernameChangeCoder, gameBaseCoder);
    }

    @Test
    public void encodeReturnsStringWithDataInOrderAndSeparatedByCorrectSeparator() {
        // prepare
        int level = 2;
        ChatMessage content = new ChatMessage("userA", "Hi!");

        when(messageCoderMock.encode(content, level + 1)).thenReturn("username:userA,message:Hi!");


        Command command = Command.SEND_BROADCAST_CHAT;
        ContentType contentType = ContentType.CHAT_MESSAGE;
        Packet packet = new Packet(command, content, contentType);

        // execute
        String result = packetCoder.encode(packet, level);

        // assert
        String expected = CoderHelper.encodeFields(level, command.name(),
                "{" + messageCoderMock.encode(content, level + 1) + "}",
                contentType.name());
        assertEquals(expected, result);
    }

    @Test
    public void encodeReturnsCorrectStringIfContentTypeIsNone() {
        int level = 0;
        Command command = Command.PONG;
        ContentType contentType = ContentType.NONE;

        Packet packet = new Packet(command, null, contentType);
        String result = packetCoder.encode(packet, level);

        String expected = CoderHelper.encodeFields(level, command.name(), "null", contentType.name());
        assertEquals(expected, result);
    }

    @Test
    public void decodeReturnsPacketWithContentNullIfContentEmptyTypeIsNone() {
        int level = 0;
        Command command = Command.PING;
        ContentType contentType = ContentType.NONE;

        String packetEncoded = CoderHelper.encodeFields(level, command.name(), "null", contentType.name());

        // execute
        Packet result = packetCoder.decode(packetEncoded, level);

        // assert
        Packet expected = new Packet(command, null, contentType);

        assertEquals(expected,result);
    }

    @Test
    public void decodeReturnsCorrectTypeWithCorrectData() {
        // prepare
        int level = 0;

        String messageEncoded = "username:userA,message:Hi!";
        ChatMessage expectedMessage = new ChatMessage("userA", "Hi!");
        when(messageCoderMock.decode(messageEncoded, level + 1)).thenReturn(expectedMessage);

        Command command = Command.SEND_BROADCAST_CHAT;
        ContentType contentType = ContentType.CHAT_MESSAGE;

        String packetEncoded = CoderHelper.encodeFields(level,
                command.name(),
                "{" + messageEncoded + "}",
                contentType.name());

        // execute
        Packet result = packetCoder.decode(packetEncoded, level);

        // assert
        Packet expected = new Packet(command, expectedMessage, contentType);

        assertEquals(expected, result);
    }

    @Test
    public void encodeWorksForString() {
        // prepare
        int level = 0;

        String username = "userA";

        Command command = Command.NEW_USER;
        ContentType contentType = ContentType.STRING;

        Packet packet = new Packet(command, username, contentType);

        // execute
        String result = packetCoder.encode(packet, level);

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

        Command command = Command.NEW_USER;
        ContentType contentType = ContentType.STRING;

        String packetEncoded = CoderHelper.encodeFields(level,
                command.name(),
                "{" + username + "}",
                contentType.name());

        // execute
        Packet result = packetCoder.decode(packetEncoded, level);

        // assert
        Packet expected = new Packet(command, username, contentType);

        assertEquals(expected, result);
    }

    @Test
    public void encodeWorksForInt() {
        // prepare
        int level = 0;

        int content = 5;

        Command command = Command.CREATE_GAME;
        ContentType contentType = ContentType.INTEGER;


        Packet packet = new Packet(command, content, contentType);

        // execute
        String result = packetCoder.encode(packet, level);

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

        Command command = Command.CREATE_GAME;
        ContentType contentType = ContentType.INTEGER;

        String packetEncoded = CoderHelper.encodeFields(level, command.name(),
                "{" + content + "}",
                contentType.name());

        // execute
        Packet result = packetCoder.decode(packetEncoded, level);

        // assert
        Packet expected = new Packet(command, content, contentType);

        assertEquals(expected, result);
    }

    @Test
    public void encodeWorksForGameStatus() {
        // prepare
        int level = 0;

        GameStatus content = GameStatus.FINISHED;

        Command command = Command.REQUEST_GAMES;
        ContentType contentType = ContentType.GAME_STATUS;


        Packet packet = new Packet(command, content, contentType);

        // execute
        String result = packetCoder.encode(packet, level);

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

        Command command = Command.REQUEST_GAMES;
        ContentType contentType = ContentType.GAME_STATUS;

        String packetEncoded = CoderHelper.encodeFields(level, command.name(),
                "{" + content.name() + "}",
                contentType.name());

        // execute
        Packet result = packetCoder.decode(packetEncoded, level);

        // assert
        Packet expected = new Packet(command, content, contentType);

        assertEquals(expected, result);
    }

    @Test
    public void encodeWorksForGameInfoList() {
        // prepare
        int level = 0;

        Date createdAt1 = new Date(2022, Calendar.JUNE, 4, 9, 44, 50);
        Date createdAt2 = new Date(2022, Calendar.JUNE, 5, 0, 0, 0);

        List<GameBase> content = Arrays.asList(
                new GameBase("game1", GameStatus.STARTED, GameMap.defaultMap(), createdAt1, "creator1", 5, 3),
                new GameBase("game2", GameStatus.STARTED, GameMap.defaultMap(), createdAt2, "creator2", 4, 0)
        );

        Command command = Command.SEND_GAMES;
        ContentType contentType = ContentType.GAME_INFO_LIST;


        Packet packet = new Packet(command, content, contentType);

        // execute
        String result = packetCoder.encode(packet, level);

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

        GameBase game1 = new GameBase("game1", GameStatus.STARTED, GameMap.defaultMap(), createdAt1, "creator1", 5, 3);
        GameBase game2 = new GameBase("game2", GameStatus.STARTED, GameMap.defaultMap(), createdAt2, "creator2", 4, 0);


        Command command = Command.SEND_GAMES;
        ContentType contentType = ContentType.GAME_INFO_LIST;

        String game1Encoded = "game1Encoded";
        String game2Encoded = "game2Encoded";

        String packetEncoded = CoderHelper.encodeFields(level, command.name(),
                "{" + CoderHelper.encodeStringList(level + 1, Arrays.asList(game1Encoded, game2Encoded)) + "}",
                contentType.name());

        when(gameBaseCoder.decode(game1Encoded, level + 2)).thenReturn(game1);
        when(gameBaseCoder.decode(game2Encoded, level + 2)).thenReturn(game2);
        
        // execute
        Packet result = packetCoder.decode(packetEncoded, level);

        // assert
        Packet expected = new Packet(command, Arrays.asList(game1, game2), contentType);

        assertEquals(expected, result);
    }
}
