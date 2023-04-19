package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameBaseCoderTest {

    @Mock
    Coder<GameMap> gameMapCoder;
    @Mock
    Coder<Activity> activityCoder;

    private GameBaseCoder gameBaseCoder;

    @BeforeEach
    void setUp() {
        gameBaseCoder = new GameBaseCoder(gameMapCoder, activityCoder);
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
        Map<String, String> roadsBuilt = Map.of("road1", "playerA", "road4", "playerB");
        Activity activity1 = new Activity("hans", ServerCommand.GAME_JOINED);

        GameBase game = new GameBase(id, status, map, createdAt, creatorPlayerIp, requiredPlayerCount, turn, roadsBuilt, Collections.singletonList(activity1));

        String encodedActivity1 = "encodedActivity1";
        when(activityCoder.encode(activity1, level + 2)).thenReturn(encodedActivity1);

        String encoded = gameBaseCoder.encode(game, level);

        String expected = CoderHelper.encodeFields(level,
                id,
                status.name(),
                gameMapCoder.encode(map, level + 1),
                CoderHelper.encodeDate(createdAt),
                creatorPlayerIp,
                String.valueOf(requiredPlayerCount),
                String.valueOf(turn),
                CoderHelper.encodeStringMap(level + 1, roadsBuilt),
                CoderHelper.encodeStringList(level + 1, Collections.singletonList(encodedActivity1)));
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
        Map<String, String> roadsBuilt = Map.of("road1", "playerA", "road4", "playerB");
        Activity activity1 = new Activity("hans", ServerCommand.GAME_JOINED);
        List<Activity> activities = Collections.singletonList(activity1);

        String encodedMap = "encodedMap";
        String encodedActivity1 = "encodedActivity1";

        when(gameMapCoder.decode(encodedMap, level + 1)).thenReturn(map);
        when(activityCoder.decode(encodedActivity1, level + 2)).thenReturn(activity1);

        String encoded = CoderHelper.encodeFields(level,
                id,
                status.name(),
                encodedMap,
                CoderHelper.encodeDate(createdAt),
                creatorPlayerIp,
                String.valueOf(requiredPlayerCount),
                String.valueOf(turn),
                CoderHelper.encodeStringMap(level + 1, roadsBuilt),
                CoderHelper.encodeStringList(level + 1, Collections.singletonList(encodedActivity1)));

        GameBase decoded = gameBaseCoder.decode(encoded, level);
        GameBase expected = new GameBase(id, status, map, createdAt, creatorPlayerIp, requiredPlayerCount, turn, roadsBuilt, activities);
        assertEquals(expected, decoded);
    }
}