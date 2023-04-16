package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Can en- and decode a {@link ClientGame}
 */
public class ClientGameCoder implements Coder<ClientGame> {

    private final Coder<GameMap> gameMapCoder;
    private final Coder<VisiblePlayer> visiblePlayerCoder;
    private final Coder<Player> playerCoder;

    public ClientGameCoder(Coder<GameMap> gameMapCoder, Coder<VisiblePlayer> visiblePlayerCoder, Coder<Player> playerCoder) {
        this.gameMapCoder = gameMapCoder;
        this.visiblePlayerCoder = visiblePlayerCoder;
        this.playerCoder = playerCoder;
    }

    @Override
    public String encode(ClientGame clientGame, int level) {
        String ownPlayerEncoded = playerCoder.encode(clientGame.getOwnPlayer(), level + 1);
        List<String> otherPlayersEncodedList = clientGame.getOtherPlayers().stream()
                .map((p) -> visiblePlayerCoder.encode(p, level + 2))
                .toList();
        String otherPlayersEncoded = CoderHelper.encodeStringList(level + 1, otherPlayersEncodedList);
        return CoderHelper.encodeFields(level,
                clientGame.getId(),
                clientGame.getStatus().name(),
                gameMapCoder.encode(clientGame.getMap(), level + 1),
                CoderHelper.encodeDate(clientGame.getCreatedAt()),
                clientGame.getCreatorPlayerIpAddress(),
                String.valueOf(clientGame.getRequiredPlayerCount()),
                otherPlayersEncoded,
                ownPlayerEncoded,
                String.valueOf(clientGame.getTurn()),
                CoderHelper.encodeStringMap(level + 1, clientGame.getRoadsBuilt())
        );
    }

    @Override
    public ClientGame decode(String encoded, int level) {
        if(encoded.equals("") || encoded.equals("null")) {
            return null;
        }
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        String gameId = fields.get(0);
        GameStatus status = GameStatus.valueOf(fields.get(1));
        GameMap map = gameMapCoder.decode(fields.get(2), level + 1);
        Date createdAt = CoderHelper.decodeDate(fields.get(3));
        String creatorIpAddress = fields.get(4);
        int requiredPlayerCount = Integer.parseInt(fields.get(5));
        List<String> otherPlayersStrings = CoderHelper.decodeStringList(level + 1, fields.get(6));
        List<VisiblePlayer> otherPlayers = otherPlayersStrings.stream()
                .map((s) -> visiblePlayerCoder.decode(s, level + 2)).toList();
        Player ownPlayer = playerCoder.decode(fields.get(7), level + 1);
        int turn = Integer.parseInt(fields.get(8));
        Map<String, String> roadsBuilt = CoderHelper.decodeStringMap(level + 1, fields.get(9));

        return new ClientGame(gameId, status, map, createdAt, creatorIpAddress,
                requiredPlayerCount, otherPlayers, ownPlayer, turn, roadsBuilt);
    }
}
