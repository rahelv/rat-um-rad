package ch.progradler.rat_um_rad.client.gui.javafx.game;

import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GameModel {
    private ClientGame clientGame;

    public GameModel(ClientGame clientGame) {
        this.clientGame = clientGame;
    }

    public void setClientGame(ClientGame clientGame) {
        this.clientGame = clientGame;
    }

    public ClientGame getClientGame() {
        return clientGame;
    }
}
