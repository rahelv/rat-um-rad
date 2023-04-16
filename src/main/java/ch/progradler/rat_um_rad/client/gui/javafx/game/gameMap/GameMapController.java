package ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap;

import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.io.IOException;

/**
 * Shows the current Game Map
 */
public class GameMapController extends GridPane {
    private GameMapModel gameMapModel;
    @FXML
    private ComboBox<Road> roadsToBuildList;
    @FXML
    private Label gameID;
    @FXML
    private Label status;
    @FXML
    private Label createdAt;
    @FXML
    private Label requiredPlayers;
    public GameMapController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/game/GameMapView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    public void initData(GameMapModel gameMapModel) {
        this.gameMapModel = gameMapModel;
        Platform.runLater(() -> {
            gameID.setText(this.gameMapModel.getGameID());
            status.setText(this.gameMapModel.getStatus().toString());
            createdAt.setText(this.gameMapModel.getCreatedAt().toString());
            requiredPlayers.setText(String.valueOf(this.gameMapModel.getRequiredPlayers()));
        });

        setRoadObservableList();
    }

    private void setRoadObservableList() {
        this.gameMapModel.setRoadObservableList();
        roadsToBuildList.setItems(this.gameMapModel.getRoadObservableList());
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

    public void udpateGameMapModel(ClientGame clientGame) {
        this.gameMapModel.updateClientGame(clientGame);
    }


    @FXML
    private void buildRoadAction(ActionEvent event) {
        //TODO:  this.gameService.buildRoad(this.roadsToBuildList.getValue());
    }

}
