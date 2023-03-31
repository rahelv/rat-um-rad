package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DecksOfGame;

import java.util.Date;
import java.util.Map;

/**
 * Instance of the game. All data structures belonging to a game are collected here.
 */
public class Game {
    private final String id;
    private GameStatus status;
    private final GameMap map;
    /**
     * Keys are ip-addresses
     */
    private Map<String, Player> players;
    private final Date createdAt;
    private String creatorPlayerIpAddress;
    private DecksOfGame decksOfGame;

    public Game(String id, GameStatus status, GameMap map, String creatorPlayerIpAddress) {
        this.id = id;
        this.status = status;
        this.map = map;
        this.creatorPlayerIpAddress = creatorPlayerIpAddress;
        this.decksOfGame = DecksOfGame.startingDecks();
        createdAt = new Date(); // now
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

    public Player getCreator() {
        return players.get(creatorPlayerIpAddress);
    }

    public DecksOfGame getDecksOfGame() {
        return decksOfGame;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}