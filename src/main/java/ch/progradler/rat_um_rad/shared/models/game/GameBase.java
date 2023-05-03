package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.Activity;

import java.util.*;

/**
 * Since the game kept by the server {@link Game} is different from the game
 * kept by the client {@link ClientGame}, both extend this class.
 */
public class GameBase {
    private final String id;
    private final GameMap map;
    private final Date createdAt;
    private final String creatorPlayerIpAddress;
    private final int requiredPlayerCount;
    private final List<Activity> activities;
    /**
     * Saves information on roads which have been built by which player.
     * Keys are road ids and values are player ip addresses.
     */
    private final Map<String, String> roadsBuilt;
    private GameStatus status;
    private int turn;

    public GameBase(String id, GameStatus status, GameMap map, String creatorPlayerIpAddress, int requiredPlayerCount) {
        this(id, status, map, new Date() /* now */, creatorPlayerIpAddress, requiredPlayerCount, 0, new HashMap<>(), new ArrayList<>());
    }

    public GameBase(String id,
                    GameStatus status,
                    GameMap map,
                    Date createdAt,
                    String creatorPlayerIpAddress,
                    int requiredPlayerCount,
                    int turn,
                    Map<String, String> roadsBuilt,
                    List<Activity> activities) {
        this.id = id;
        this.status = status;
        this.map = map;
        this.createdAt = createdAt;
        this.creatorPlayerIpAddress = creatorPlayerIpAddress;
        this.requiredPlayerCount = requiredPlayerCount;
        this.turn = turn;
        this.activities = activities;
        this.roadsBuilt = roadsBuilt;
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

    public int getTurn() {
        return turn;
    }

    public void nextTurn() {
        turn++;
    }

    public Map<String, String> getRoadsBuilt() {
        return roadsBuilt;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameBase)) return false;
        GameBase gameBase = (GameBase) o;
        return requiredPlayerCount == gameBase.requiredPlayerCount && turn == gameBase.turn && id.equals(gameBase.id) && status == gameBase.status && map.equals(gameBase.map) && createdAt.equals(gameBase.createdAt) && creatorPlayerIpAddress.equals(gameBase.creatorPlayerIpAddress) && activities.equals(gameBase.activities) && roadsBuilt.equals(gameBase.roadsBuilt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, map, createdAt, creatorPlayerIpAddress, requiredPlayerCount, turn, activities, roadsBuilt);
    }
}