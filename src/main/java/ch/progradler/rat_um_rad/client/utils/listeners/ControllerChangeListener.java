package ch.progradler.rat_um_rad.client.utils.listeners;

import ch.progradler.rat_um_rad.client.gui.javafx.game.GameModel;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;

import java.util.List;
import java.util.Optional;

public interface ControllerChangeListener<T> {
    void controllerChanged(String command);
    void gameCreated(ClientGame content); //TODO: generalize type

    void selectDestinationCards(ClientGame clientGame);

    void returnToGame(ClientGame clientGame);

    void showWinner(ClientGame game);
}
