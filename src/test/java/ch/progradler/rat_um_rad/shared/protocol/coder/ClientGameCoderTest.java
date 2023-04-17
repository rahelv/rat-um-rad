package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientGameCoderTest {

    @Mock
    Coder<GameMap> gameMapCoderMock;
    @Mock
    Coder<VisiblePlayer> visiblePlayerCoderMock;
    @Mock
    Coder<Player> playerCoderMock;

    private ClientGameCoder clientGameCoder;

    @BeforeEach
    void setUp() {
        clientGameCoder = new ClientGameCoder(gameMapCoderMock, visiblePlayerCoderMock, playerCoderMock);
    }

    @Test
    void encodeWorks() {
        int level = 1;

        String gameId = "gameA";
        GameStatus status = GameStatus.STARTED;
        GameMap map = GameMap.defaultMap();
        Date createdAt = new Date(2023, Calendar.JUNE, 3);
        String creatorIp = "clientCreator";
        int requiredPlayerCount = 5;
        int turn = 20;

        Player ownPlayer = new Player("player A", WheelColor.RED, 100, 10, 2);
        VisiblePlayer otherPlayer = new VisiblePlayer("player B", WheelColor.BLUE,
                50, 15, 1, "clientB", 15, 3);
        List<VisiblePlayer> otherPlayers = Collections.singletonList(otherPlayer);
        Map<String, String> roadsBuilt = Map.of("road1", "playerA", "road4", "playerB");

        ClientGame game = new ClientGame(gameId,
                status,
                map,
                createdAt,
                creatorIp,
                requiredPlayerCount,
                otherPlayers,
                ownPlayer,
                turn,
                roadsBuilt);

        when(gameMapCoderMock.encode(map, level + 1)).thenReturn("encoded/map");
        String encodedVisiblePlayer = "encoded/visible/player";
        when(visiblePlayerCoderMock.encode(otherPlayer, level + 2)).thenReturn(encodedVisiblePlayer);
        String encodedPlayer = "encoded/player";
                when(playerCoderMock.encode(ownPlayer, level + 1)).thenReturn(encodedPlayer);

        String encoded = clientGameCoder.encode(game, level);

        String expected = CoderHelper.encodeFields(level,
                gameId,
                status.name(),
                gameMapCoderMock.encode(map, level + 1),
                CoderHelper.encodeDate(createdAt),
                creatorIp,
                String.valueOf(requiredPlayerCount),
                CoderHelper.encodeStringList(level + 1, Collections.singletonList(encodedVisiblePlayer)),
                encodedPlayer,
                String.valueOf(turn),
                CoderHelper.encodeStringMap(level + 1, roadsBuilt));
        assertEquals(expected, encoded);
    }

    @Test
    void decodeWorks() {
        int level = 1;

        String gameId = "gameA";
        GameStatus status = GameStatus.STARTED;
        GameMap map = GameMap.defaultMap();
        Date createdAt = new Date(2023, Calendar.JUNE, 3);
        String creatorIp = "clientCreator";
        int requiredPlayerCount = 5;
        int turn = 20;

        Player ownPlayer = new Player("player A", WheelColor.RED, 100, 10, 2);
        VisiblePlayer otherPlayer = new VisiblePlayer("player B", WheelColor.BLUE,
                50, 15, 1, "clientB", 15, 3);
        List<VisiblePlayer> otherPlayers = Collections.singletonList(otherPlayer);
        Map<String, String> roadsBuilt = Map.of("road1", "playerA", "road4", "playerB");

        String encodedMap = "encoded/map";

        when(gameMapCoderMock.decode(encodedMap, level + 1)).thenReturn(map);
        String encodedVisiblePlayer = "encoded/visible/player";
        when(visiblePlayerCoderMock.decode(encodedVisiblePlayer, level + 2)).thenReturn(otherPlayer);
        String encodedPlayer = "encoded/player";
        when(playerCoderMock.decode(encodedPlayer, level + 1)).thenReturn(ownPlayer);

        String encoded = CoderHelper.encodeFields(level,
                gameId,
                status.name(),
                encodedMap,
                CoderHelper.encodeDate(createdAt),
                creatorIp,
                String.valueOf(requiredPlayerCount),
                CoderHelper.encodeStringList(level + 1, Collections.singletonList(encodedVisiblePlayer)),
                encodedPlayer,
                String.valueOf(turn),
                CoderHelper.encodeStringMap(level + 1, roadsBuilt));


        ClientGame decoded = clientGameCoder.decode(encoded, level);

        ClientGame expected = new ClientGame(gameId,
                status,
                map,
                createdAt,
                creatorIp,
                requiredPlayerCount,
                otherPlayers,
                ownPlayer,
                turn,
                roadsBuilt);
        assertEquals(expected, decoded);
    }
}