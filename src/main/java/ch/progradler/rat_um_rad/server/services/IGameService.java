package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

import java.util.List;

/**
 * This is the interface with actions the players can do and important services like exitGame().
 */
public interface IGameService {

    void createGame(String creatorIpAddress, int requiredPlayerCount);

    /**
     * Called when a new player connects to the game.
     * <p>
     * If number of players is equal to requiredPlayers, then:
     * - status must be changed to PREPARATION,
     * - all players must get a long destination card
     * - all players must be informed that the game has started
     * - all players must get a proposal of destination cards
     */
    void joinGame(String ipAddress, String gameIp);

    /**
     * This method is used in the game {@link GameStatus#PREPARATION} as well as in {@link GameStatus#STARTED}.
     */
    void selectShortDestinationCards(String ipAddress, List<String> selectedCards);

    void requestShortDestinationCards(String ipAddress);

    void buildRoad(String ipAddress, String roadId);

    void takeWheelCardFromDeck(String ipAddress);


    /**
     * TODO: explain what this method does.
     */
    void handleConnectionLoss(String ipAddress);

    /**
     * @param ipAddress of client who requested list of game instances that have not yet started.
     *                  (i.e. their {@link Game#getStatus()} is {@link GameStatus#WAITING_FOR_PLAYERS}
     */
    void getWaitingGames(String ipAddress);

    /**
     * @param ipAddress of client who requested list of game instances that are finished.
     *                  (i.e. their {@link Game#getStatus()} is {@link GameStatus#PREPARATION} and {@link GameStatus#STARTED}
     */
    void getStartedGames(String ipAddress);

    /**
     * @param ipAddress of client who requested list of game instances that are finished.
     *                  (i.e. their {@link Game#getStatus()} is {@link GameStatus#FINISHED}
     */
    void getFinishedGames(String ipAddress);

    void requestHighscores(String ipAddress);
}