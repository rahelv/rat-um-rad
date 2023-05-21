package ch.progradler.rat_um_rad.client.gui.javafx.game;

import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;

public class GameModel {
    private final ControllerChangeListener<?> listener;
    private ClientGame clientGame;

    public GameModel(ControllerChangeListener<?> listener, ClientGame clientGame) {
        this.clientGame = clientGame;
        this.listener = listener;
    }

    public ClientGame getClientGame() {
        return clientGame;
    }

    public void setClientGame(ClientGame clientGame) {
        this.clientGame = clientGame;
    }

    public ControllerChangeListener<?> getListener() {
        return listener;
    }
}
