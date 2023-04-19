package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Instance of the game kept in client. All data structures permitted to be visible by client are collected here.
 */
public class ClientGame extends GameBase {
    private final List<VisiblePlayer> otherPlayers;
    private final Player ownPlayer;

    public ClientGame(String id,
                      GameStatus status,
                      GameMap map,
                      Date createdAt,
                      String creatorPlayerIpAddress,
                      int requiredPlayerCount,
                      List<VisiblePlayer> otherPlayers,
                      Player ownPlayer,
                      int turn,
                      Map<String, String> roadsBuilt,
                      List<Activity> activities) {
        super(id, status, map, createdAt, creatorPlayerIpAddress, requiredPlayerCount, turn, roadsBuilt, activities);
        this.otherPlayers = otherPlayers;
        this.ownPlayer = ownPlayer;
    }

    public List<VisiblePlayer> getOtherPlayers() {
        return otherPlayers;
    }

    public Player getOwnPlayer() {
        return ownPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClientGame that = (ClientGame) o;
        return otherPlayers.equals(that.otherPlayers) && ownPlayer.equals(that.ownPlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), otherPlayers, ownPlayer);
    }
}
