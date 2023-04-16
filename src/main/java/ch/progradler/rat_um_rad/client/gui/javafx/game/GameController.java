package ch.progradler.rat_um_rad.client.gui.javafx.game;

import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable, ServerResponseListener<ClientGameChange> {
    private GameService gameService;
    @FXML
    private ComboBox<Road> roadsToBuildList;
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
        this.gameService = new GameService();
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
        roadsToBuildList.setItems(this.gameModel.getRoadObservableList());
        roadsToBuildList.setConverter(new StringConverter<Road>() {
            @Override
            public String toString(Road object) {
                return object.getId();
            }

            @Override
            public Road fromString(String string) {
                return null;
            }
        });
        roadsToBuildList.setCellFactory(
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

    @FXML
    private void buildRoadAction(ActionEvent event) {
       //TODO:  this.gameService.buildRoad(this.roadsToBuildList.getValue());
    }
}
