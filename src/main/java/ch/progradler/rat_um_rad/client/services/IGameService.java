package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import javafx.collections.ObservableList;

import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

import java.io.IOException;
import java.util.List;

/**
 * Allows client to send game-related requests to server.
 */
public interface IGameService {
    void createGame(int requiredPlayerCount) throws IOException;

    void joinGame(String gameId) throws IOException;

    void buildRoad(String roadId) throws IOException;

    void buildGreyRoad(String roadId, WheelColor color) throws IOException;

    void requestWaitingGames() throws IOException;

    void requestStartedGames() throws IOException;

    void requestFinishedGames() throws IOException;

    void selectCards(List<DestinationCard> selectedItems);
}
