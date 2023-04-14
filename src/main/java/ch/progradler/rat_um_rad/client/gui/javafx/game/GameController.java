package ch.progradler.rat_um_rad.client.gui.javafx.game;

import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview.ShowAllGamesController;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML
    private ListView<Road> roadsListView;
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
    }

    /** initializes the game model and binds data to view.
     * @param gameModel
     * @param stage
     */
    public void initData(GameModel gameModel, Stage stage) {
        this.stage = stage;
        this.gameModel = gameModel;

        gameID.setText(gameModel.getClientGame().getId());
        status.setText(gameModel.getClientGame().getStatus().toString());
        createdAt.setText(gameModel.getClientGame().getCreatedAt().toString());
        requiredPlayers.setText(String.valueOf(gameModel.getClientGame().getRequiredPlayerCount()));

        this.gameModel.setRoadObservableList(this.gameModel.getClientGame().getMap().getRoads());
        roadsListView.setItems(this.gameModel.getRoadObservableList());
        roadsListView.setCellFactory(
                listview -> new ListCell<Road>() {
                    @Override
                    public void updateItem(Road road, boolean empty) {
                        super.updateItem(road, empty);
                        textProperty().unbind();
                        if(road != null)
                            //TODO: textProperty().bind();
                            textProperty().setValue(road.getId());
                        else
                            setText(null);
                    }
                }
        );
    }
}
