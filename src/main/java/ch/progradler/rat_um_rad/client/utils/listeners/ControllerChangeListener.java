package ch.progradler.rat_um_rad.client.utils.listeners;

import ch.progradler.rat_um_rad.shared.models.game.ClientGame;

public interface ControllerChangeListener<T> {
    void controllerChanged(String command);

    void gameCreated(ClientGame content); //TODO: generalize type

    void selectDestinationCards(ClientGame clientGame);

    void returnToGame(ClientGame clientGame);

    void showWinner(ClientGame game);
}
