package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;

import java.util.Date;
import java.util.List;

public class GameBaseCoder implements Coder<GameBase> {
    private final Coder<GameMap> gameMapCoder;

    public GameBaseCoder(Coder<GameMap> gameMapCoder) {
        this.gameMapCoder = gameMapCoder;
    }

    @Override
    public String encode(GameBase game, int level) {
        return CoderHelper.encodeFields(level,
                game.getId(),
                game.getStatus().name(),
                gameMapCoder.encode(game.getMap(), level + 1),
                CoderHelper.encodeDate(game.getCreatedAt()),
                game.getCreatorPlayerIpAddress(),
                String.valueOf(game.getRequiredPlayerCount()),
                String.valueOf(game.getTurn()));
    }

    @Override
    public GameBase decode(String encoded, int level) {
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        String id = fields.get(0);
        GameStatus status = GameStatus.valueOf(fields.get(1));
        GameMap map = gameMapCoder.decode(fields.get(2), level + 1);
        Date createdAt = CoderHelper.decodeDate(fields.get(3));
        String creatorPlayerIp = fields.get(4);
        int requiredPlayerCount = Integer.parseInt(fields.get(5));
        int turn = Integer.parseInt(fields.get(6));
        return new GameBase(id, status, map, createdAt, creatorPlayerIp, requiredPlayerCount, turn);
    }
}