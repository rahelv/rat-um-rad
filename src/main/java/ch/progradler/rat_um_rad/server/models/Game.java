package ch.progradler.rat_um_rad.server.models;

import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DecksOfGame;

import java.util.*;

/**
 * Instance of the game kept in server. All data belonging to a game is collected here.
 */
public class Game extends GameBase {
    /**
     * Keys are ip-addresses
     */
    private final Map<String, Player> players;
    private final DecksOfGame decksOfGame;
    private final Map<String, Boolean> playersHaveChosenShortDestinationCards = new HashMap<String, Boolean>();

    public Game(String id,
                GameStatus status,
                GameMap map,
                Date createdAt,
                String creatorPlayerIpAddress,
                int requiredPlayerCount,
                Map<String, Player> players,
                int turn,
                Map<String, String> roadsBuilt,
                List<Activity> activities,
                DecksOfGame decksOfGame,
                List<String> playerNames) {
        super(id, status, map, createdAt, creatorPlayerIpAddress, requiredPlayerCount, turn, roadsBuilt, activities, playerNames);
        this.decksOfGame = decksOfGame;
        this.players = players;
    }

    public Game(String id, GameStatus status, GameMap map, String creatorPlayerIpAddress, int requiredPlayerCount, Map<String, Player> players, DecksOfGame decksOfGame) {
        super(id, status, map, creatorPlayerIpAddress, requiredPlayerCount);
        this.decksOfGame = decksOfGame;
        this.players = players;
    }

    /**
     * @param map must be the {@link GameMap#defaultMap()}
     */
    public Game(String id, GameStatus status, GameMap map, String creatorPlayerIpAddress, int requiredPlayerCount, Map<String, Player> players) {
        this(id, status, map, creatorPlayerIpAddress, requiredPlayerCount, players, DecksOfGame.startingDecks(map));
    }

    public Game(String id,
                GameStatus status,
                GameMap map,
                Date createdAt,
                String creatorPlayerIpAddress,
                int requiredPlayerCount,
                Map<String, Player> players,
                int turn,
                Map<String, String> roadsBuilt,
                List<Activity> activities,
                List<String> playerNames) {
        this(id,
                status,
                map,
                createdAt,
                creatorPlayerIpAddress,
                requiredPlayerCount,
                players,
                turn,
                roadsBuilt,
                activities,
                DecksOfGame.startingDecks(map),
                playerNames);
    }

    public DecksOfGame getDecksOfGame() {
        return decksOfGame;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public Map<String, Boolean> getPlayersHaveChosenShortDestinationCards() {
        return playersHaveChosenShortDestinationCards;
    }

    public boolean hasReachedRequiredPlayers() {
        return getRequiredPlayerCount() == players.size();
    }

    public Set<String> getPlayerIpAddresses() {
        return players.keySet();
    }
}
