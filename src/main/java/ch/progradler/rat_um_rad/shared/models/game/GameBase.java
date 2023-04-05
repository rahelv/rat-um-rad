package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.client.models.ClientGame;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DecksOfGame;

import java.util.Date;
import java.util.Map;

/**
 * Since the game kept by the server {@link Game} is different from the game
 * kept by the client {@link ClientGame}, both extend this class.
 */
public class GameBase {
    private final String id;
    private GameStatus status;
    private final GameMap map;
    /**
     * Keys are ip-addresses
     */
    private Map<String, PlayerBase> players;
    private final Date createdAt;
    private String creatorPlayerIpAddress;
    private final int requiredPlayerCount;
    public GameBase(String id, GameStatus status, GameMap map, String creatorPlayerIpAddress, int requiredPlayerCount) {
        this.id = id;
        this.status = status;
        this.map = map;
        this.creatorPlayerIpAddress = creatorPlayerIpAddress;
        this.requiredPlayerCount = requiredPlayerCount;
        createdAt = new Date(); // now
    }

    public void addPlayer(String ipAddress) {
        //TODO: implement
    }

    public String getId() {
        return id;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public GameMap getMap() {
        return map;
    }

    public String getCreatorPlayerIpAddress() {
        return creatorPlayerIpAddress;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getRequiredPlayerCount() {
        return requiredPlayerCount;
    }
}