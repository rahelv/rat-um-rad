package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameBaseCoderTest {

    @Mock
    Coder<GameMap> gameMapCoder;

    private GameBaseCoder gameBaseCoder;

    @BeforeEach
    void setUp() {
        gameBaseCoder = new GameBaseCoder(gameMapCoder);
    }

    @Test
    void encode() {
        int level = 1;
        String id = "game1";
        GameStatus status = GameStatus.STARTED;
        GameMap map = GameMap.defaultMap();
        Date createdAt = new Date();
        String creatorPlayerIp = "player1";
        int requiredPlayerCount = 5;
        int turn = 13;
        GameBase game = new GameBase(id, status, map, createdAt, creatorPlayerIp, requiredPlayerCount, turn);

        String encoded = gameBaseCoder.encode(game, level);

        String expected = CoderHelper.encodeFields(level,
                id,
                status.name(),
                gameMapCoder.encode(map, level + 1),
                CoderHelper.encodeDate(createdAt),
                creatorPlayerIp,
                String.valueOf(requiredPlayerCount),
                String.valueOf(turn));
        assertEquals(expected, encoded);
    }

    @Test
    void decode() {
        int level = 1;

        String id = "game1";
        GameStatus status = GameStatus.STARTED;
        GameMap map = GameMap.defaultMap();
        Date createdAt = new Date(2022, Calendar.JUNE, 4, 9, 44, 50);
        String creatorPlayerIp = "player1";
        int requiredPlayerCount = 5;
        int turn = 13;

        String encodedMap = "encodedMap";

        when(gameMapCoder.decode(encodedMap, level + 1)).thenReturn(map);

        String encoded = CoderHelper.encodeFields(level,
                id,
                status.name(),
                encodedMap,
                CoderHelper.encodeDate(createdAt),
                creatorPlayerIp,
                String.valueOf(requiredPlayerCount),
                String.valueOf(turn));

        GameBase decoded = gameBaseCoder.decode(encoded, level);
        GameBase expected = new GameBase(id, status, map, createdAt, creatorPlayerIp, requiredPlayerCount, turn);
        assertEquals(expected, decoded);
    }
}