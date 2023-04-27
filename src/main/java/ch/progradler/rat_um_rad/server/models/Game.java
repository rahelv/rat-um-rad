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

    public Game(String id, GameStatus status, GameMap map, String creatorPlayerIpAddress, int requiredPlayerCount, Map<String, Player> players) {
        super(id, status, map, creatorPlayerIpAddress, requiredPlayerCount);
        this.decksOfGame = DecksOfGame.startingDecks(map);
        this.players = players;
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
                List<Activity> activities) {
        super(id, status, map, createdAt, creatorPlayerIpAddress, requiredPlayerCount, turn, roadsBuilt, activities);
        this.decksOfGame = DecksOfGame.startingDecks(map);
        this.players = players;
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
