package ch.progradler.rat_um_rad.client.gui.javafx.game;

import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GameModel {
    private final ControllerChangeListener<?> listener;
    private ClientGame clientGame;

    public GameModel(ControllerChangeListener<?> listener, ClientGame clientGame) {
        this.clientGame = clientGame;
        this.listener = listener;
    }

    public void setClientGame(ClientGame clientGame) {
        this.clientGame = clientGame;
    }

    public ClientGame getClientGame() {
        return clientGame;
    }

    public ControllerChangeListener<?> getListener() {
        return listener;
    }
}
