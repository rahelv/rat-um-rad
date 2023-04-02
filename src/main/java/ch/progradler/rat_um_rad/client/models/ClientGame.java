package ch.progradler.rat_um_rad.client.models;

import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Instance of the game kept in client. All data structures permitted to be visible by client are collected here.
 */
public class ClientGame extends GameBase {
    /**
     * Keys are ip-addresses
     */
    private Map<String, VisiblePlayer> players;
    private Player ownPlayer;

    public ClientGame(String id, GameStatus status, GameMap map, String creatorPlayerIpAddress, int requiredPlayerCount, Map<String, VisiblePlayer> players) {
        super(id, status, map, creatorPlayerIpAddress, requiredPlayerCount);
        this.players = players;
    }

    public Map<String, VisiblePlayer> getPlayers() {
        return players;
    }

    public Player getOwnPlayer() {
        return ownPlayer;
    }
}
