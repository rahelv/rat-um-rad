package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper.*;

public class GameBaseCoder implements Coder<GameBase> {
    private final Coder<GameMap> gameMapCoder;
    private final Coder<Activity> activityCoder;

    public GameBaseCoder(Coder<GameMap> gameMapCoder, Coder<Activity> activityCoder) {
        this.gameMapCoder = gameMapCoder;
        this.activityCoder = activityCoder;
    }

    @Override
    public String encode(GameBase game, int level) {
        String activitiesEncoded = encodeList(activityCoder, game.getActivities(), level + 1);
        return encodeFields(level,
                game.getId(),
                game.getStatus().name(),
                gameMapCoder.encode(game.getMap(), level + 1),
                encodeDate(game.getCreatedAt()),
                game.getCreatorPlayerIpAddress(),
                String.valueOf(game.getRequiredPlayerCount()),
                String.valueOf(game.getTurn()),
                encodeStringMap(level + 1, game.getRoadsBuilt()),
                activitiesEncoded,
                encodeStringList(level + 1, game.getPlayerNames())
        );
    }

    @Override
    public GameBase decode(String encoded, int level) {
        List<String> fields = decodeFields(level, encoded);
        String id = fields.get(0);
        GameStatus status = GameStatus.valueOf(fields.get(1));
        GameMap map = gameMapCoder.decode(fields.get(2), level + 1);
        Date createdAt = decodeDate(fields.get(3));
        String creatorPlayerIp = fields.get(4);
        int requiredPlayerCount = Integer.parseInt(fields.get(5));
        int turn = Integer.parseInt(fields.get(6));
        Map<String, String> roadsBuilt = decodeStringMap(level + 1, fields.get(7));
        List<Activity> activities = decodeList(activityCoder, fields.get(8), level + 1);
        List<String> playerNames = decodeStringList(level + 1, fields.get(9));

        return new GameBase(id, status, map, createdAt, creatorPlayerIp, requiredPlayerCount, turn, roadsBuilt, activities, playerNames);
    }
}