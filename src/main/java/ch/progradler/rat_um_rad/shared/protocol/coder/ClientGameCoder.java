package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper.*;

/**
 * Can en- and decode a {@link ClientGame}
 */
public class ClientGameCoder implements Coder<ClientGame> {

    private final Coder<GameMap> gameMapCoder;
    private final Coder<VisiblePlayer> visiblePlayerCoder;
    private final Coder<Player> playerCoder;
    private final Coder<Activity> activityCoder;

    public ClientGameCoder(Coder<GameMap> gameMapCoder, Coder<VisiblePlayer> visiblePlayerCoder, Coder<Player> playerCoder, Coder<Activity> activityCoder) {
        this.gameMapCoder = gameMapCoder;
        this.visiblePlayerCoder = visiblePlayerCoder;
        this.playerCoder = playerCoder;
        this.activityCoder = activityCoder;
    }

    @Override
    public String encode(ClientGame clientGame, int level) {
        String ownPlayerEncoded = playerCoder.encode(clientGame.getOwnPlayer(), level + 1);
        String otherPlayersEncoded = encodeList(visiblePlayerCoder, clientGame.getOtherPlayers(), level + 1);
        String activitiesEncoded = encodeList(activityCoder, clientGame.getActivities(), level + 1);

        return encodeFields(level,
                clientGame.getId(),
                clientGame.getStatus().name(),
                gameMapCoder.encode(clientGame.getMap(), level + 1),
                encodeDate(clientGame.getCreatedAt()),
                clientGame.getCreatorPlayerIpAddress(),
                String.valueOf(clientGame.getRequiredPlayerCount()),
                otherPlayersEncoded,
                ownPlayerEncoded,
                String.valueOf(clientGame.getTurn()),
                encodeStringMap(level + 1, clientGame.getRoadsBuilt()),
                activitiesEncoded,
                encodeStringList(level + 1, clientGame.getPlayerNames())
        );
    }

    @Override
    public ClientGame decode(String encoded, int level) {
        if (encoded.equals("null")) {
            return null;
        }
        List<String> fields = decodeFields(level, encoded);
        String gameId = fields.get(0);
        GameStatus status = GameStatus.valueOf(fields.get(1));
        GameMap map = gameMapCoder.decode(fields.get(2), level + 1);
        Date createdAt = decodeDate(fields.get(3));
        String creatorIpAddress = fields.get(4);
        int requiredPlayerCount = Integer.parseInt(fields.get(5));
        List<VisiblePlayer> otherPlayers = decodeList(visiblePlayerCoder, fields.get(6), level + 1);
        Player ownPlayer = playerCoder.decode(fields.get(7), level + 1);
        int turn = Integer.parseInt(fields.get(8));
        Map<String, String> roadsBuilt = decodeStringMap(level + 1, fields.get(9));
        List<Activity> activities = decodeList(activityCoder, fields.get(10), level + 1);
        List<String> playerNames = decodeStringList(level + 1, fields.get(11));

        return new ClientGame(gameId, status, map, createdAt, creatorIpAddress,
                requiredPlayerCount, otherPlayers, ownPlayer, turn, roadsBuilt, activities, playerNames);
    }
}
