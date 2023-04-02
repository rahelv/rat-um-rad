package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCardDeck;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

import java.util.List;

/**
 * This is the implementation of {@link IGameService}. It's hidden for the clients since the instance
 * of {@link Game} will have an instance of the type {@link IGameService}.
 */
public class GameService implements IGameService {
    private final Game game;
    public GameService(Game game) {
        this.game = game;
    }
    @Override
    public void addPlayer(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void sendMessageTo(String ipAddressFrom, String ipAddressTo) {
        //TODO: implement
    }

    @Override
    public void sendMessageToAll(String ipAddressFrom) {
        //TODO: implement
    }

    @Override
    public void exitGame(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void selectShortDestinationCards(String ipAddress, DestinationCardDeck destinationCardDeck) {
        //TODO: implement
    }

    @Override
    public void buildRoad(String ipAddress, String roadId) {
        //TODO: implement
    }

    @Override
    public void buildGreyRoad(String ipAddress, String roadId, WheelColor color) {
        //TODO: implement
    }

    @Override
    public void takeWheelCardFromDeck(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void takeOpenWheelCard(String ipAdress, WheelCard wheelCard) {
        //TODO: implement
    }

    @Override
    public void takeDestinationCard(String ipAdress) {
        //TODO: implement
    }

    @Override
    public void handleConnectionLoss(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void wantToFinishGame(String ipAddress) {
        //TODO: implement
    }

    @Override
    public void dontWantToFinishGame(String ipAddress) {
        //TODO: implement
    }

    @Override
    public List<Game> getWaitingGames() {
        //TODO: implement
        return null;
    }

    @Override
    public List<Game> getFinishedGames() {
        //TODO: implement
        return null;
    }
}
