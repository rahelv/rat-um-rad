package ch.progradler.rat_um_rad.client.gui.javafx.game;

import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    Stage stage;
    GameModel gameModel;
    @FXML
    private Label gameID;
    @FXML
    private Label status;
    @FXML
    private Label createdAt;
    @FXML
    private Label requiredPlayers;
    /**
     * 1. Warten auf Spieler in Lobby
     * 2. Game Startet
     * 3. --> Karten wählen
     * 4. --> Farbe zugewiesen, Radkarten bekommen etc.
     * 5. Karte anzeigen
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.gameModel = new GameModel();
    }

    public void initData(ClientGame clientGame, Stage stage) {
        this.stage = stage;
        this.gameModel.setClientGame(clientGame);

        gameID.setText(gameModel.getClientGame().getId());
        status.setText(gameModel.getClientGame().getStatus().toString());
        createdAt.setText(gameModel.getClientGame().getCreatedAt().toString());
        requiredPlayers.setText(String.valueOf(gameModel.getClientGame().getRequiredPlayerCount()));
    }
}
