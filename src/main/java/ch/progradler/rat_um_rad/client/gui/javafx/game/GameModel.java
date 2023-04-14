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
    private ObservableList<Road> roadObservableList;
    /**
     * 1. Warten auf Spieler in Lobby
     * 2. Game Startet
     * 3. --> Karten wählen
     * 4. --> Farbe zugewiesen, Radkarten bekommen etc.
     * 5. Karte anzeigen
     * */

    public GameModel(ClientGame clientGame) {
        this.clientGame = clientGame;
        this.roadObservableList = FXCollections.observableArrayList();
    }

    public void setClientGame(ClientGame clientGame) {
        this.clientGame = clientGame;
    }
    public ObservableList<Road> getRoadObservableList() {
        return this.roadObservableList;
    }

    public void setRoadObservableList(List<Road> roads) {
        this.roadObservableList = FXCollections.observableArrayList(roads);
    }

    public ClientGame getClientGame() {
        return clientGame;
    }
}
