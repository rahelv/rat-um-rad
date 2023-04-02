package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCardDeck;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

import java.util.List;

/**
 * This is the interface with actions the players can do and important services like exitGame().
 */
public interface IGameService {
    /**
     * Called when a new player connects to the game.
     */
    public void addPlayer(String ipAddress);
    public void sendMessageTo(String ipAddressFrom, String ipAddressTo);
    public void sendMessageToAll(String ipAddressFrom);
    public void exitGame(String ipAddress);

    /**
     * This method is used in the game state PREPARATION as well as in STARTED.
     */
    public void selectShortDestinationCards(String ipAddress, DestinationCardDeck destinationCardDeck);
    public void buildRoad(String ipAddress, String roadId);

    /**
     * The user can choose the color of wheelCards he wants to use to build that road when the road
     * is grey.
     */
    public void buildGreyRoad(String ipAddress, String roadId, WheelColor color);
    public void takeWheelCardFromDeck(String ipAddress);
    public void takeOpenWheelCard(String ipAdress, WheelCard wheelCard);
    public void takeDestinationCard(String ipAdress);

    /**
     * TODO: explain what this method does.
     */
    public void handleConnectionLoss(String ipAddress);
    public void wantToFinishGame(String ipAddress);

    /**
     * In order to finish the game in a hypothetical dead end of the game, this method can be called.
     * If it's called from every player where no player called dontWantToFinishGame() anymore, the
     * game gets the state FINISHED.
     */
    public void dontWantToFinishGame(String ipAddress);

    /**
     * @return List of game instances that have not yet started
     * (i.e. their {@link Game#getStatus()} is {@link GameStatus#WAITING_FOR_PLAYERS}
     */
    List<Game> getWaitingGames();

    /**
     * @return List of game instances that are finished.
     * (i.e. their {@link Game#getStatus()} is {@link GameStatus#FINISHED}
     */
    List<Game> getFinishedGames();
}