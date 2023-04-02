package ch.progradler.rat_um_rad.server.models;

import ch.progradler.rat_um_rad.server.services.GameService;
import ch.progradler.rat_um_rad.server.services.IGameService;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DecksOfGame;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;

import java.util.Map;

/**
 * Instance of the game kept in server. All data belonging to a game are collected here.
 */
public class Game extends GameBase {
    /**
     * Keys are ip-addresses
     */
    private Map<String, Player> players;
    private DecksOfGame decksOfGame;
    private final IGameService gameService = new GameService(this);

    public Game(String id, GameStatus status, GameMap map, String creatorPlayerIpAddress, int requiredPlayerCount, Map<String, Player> players) {
        super(id, status, map, creatorPlayerIpAddress, requiredPlayerCount);
        this.decksOfGame = DecksOfGame.startingDecks();
        this.players = players;
    }

    public DecksOfGame getDecksOfGame() {
        return decksOfGame;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }
}