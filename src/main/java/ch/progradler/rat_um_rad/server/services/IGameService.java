package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCardDeck;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

/**
 * This is the interface with actions the players can do and important services like exitGame().
 */
public interface IGameService {

    void createGame(String creatorIpAddress, int requiredPlayerCount);

    /**
     * Called when a new player connects to the game.
     */
    void addPlayer(String ipAddress);

    void sendMessageTo(String ipAddressFrom, String ipAddressTo);

    void sendMessageToAll(String ipAddressFrom);

    void exitGame(String ipAddress);

    /**
     * This method is used in the game {@link GameStatus#PREPARATION} as well as in {@link GameStatus#STARTED}.
     */
    void selectShortDestinationCards(String ipAddress, DestinationCardDeck destinationCardDeck);

    void buildRoad(String ipAddress, String roadId);

    /**
     * The user can choose the color of wheelCards he wants to use to build that road when the road
     * is grey.
     */
    void buildGreyRoad(String ipAddress, String roadId, WheelColor color);

    void takeWheelCardFromDeck(String ipAddress);

    void takeOpenWheelCard(String ipAddress, WheelCard wheelCard);

    void takeDestinationCard(String ipAddress);

    /**
     * TODO: explain what this method does.
     */
    void handleConnectionLoss(String ipAddress);

    void wantToFinishGame(String ipAddress);

    /**
     * In order to finish the game in a hypothetical dead end of the game, this method can be called.
     * If it's called from every player where no player called dontWantToFinishGame() anymore, the
     * game gets the state FINISHED.
     */
    void dontWantToFinishGame(String ipAddress);

    /**
     * @param ipAddress of client who requested list of game instances that have not yet started.
     * (i.e. their {@link Game#getStatus()} is {@link GameStatus#WAITING_FOR_PLAYERS}
     */
    void getWaitingGames(String ipAddress);

    /**
     * @param ipAddress of client who requested list of game instances that are finished.
     * (i.e. their {@link Game#getStatus()} is {@link GameStatus#PREPARATION} and {@link GameStatus#STARTED}
     */
    void getStartedGames(String ipAddress);

    /**
     * @param ipAddress of client who requested list of game instances that are finished.
     * (i.e. their {@link Game#getStatus()} is {@link GameStatus#FINISHED}
     */
    void getFinishedGames(String ipAddress);
}