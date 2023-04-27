package ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap;

import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.io.IOException;

/**
 * Shows the current Game Map
 */
public class GameMapController extends GridPane {
    private GameService gameService;
    private GameMapModel gameMapModel;
    @FXML
    private ComboBox<Road> roadsToBuildListView;
    @FXML
    private ListView<String> roadsBuiltListView;
    @FXML
    private Label gameID;
    @FXML
    private Label status;
    @FXML
    private Label createdAt;
    @FXML
    private Label requiredPlayers;
    public GameMapController() {
        this.gameService = new GameService();
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
        this.gameMapModel.updateFields();

        setRoadObservableList();
        this.gameMapModel.setBuiltRoadObservableList();
        roadsBuiltListView.setItems(this.gameMapModel.getBuiltRoadsObservableList());
    }

    private void setRoadObservableList() {
        this.gameMapModel.setRoadsToBuildObservableList();
        roadsToBuildListView.setItems(this.gameMapModel.getRoadsToBuildObservableList());
        roadsToBuildListView.setConverter(new StringConverter<Road>() {
            @Override
            public String toString(Road object) {
                if(object == null) {
                    return "";
                }
                return object.getId();
            }

            @Override
            public Road fromString(String string) {
                return null;
            }
        });
        roadsToBuildListView.setCellFactory(
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

    public void updateGameMapModel(ClientGame clientGame) {
        this.gameMapModel.updateClientGame(clientGame);
    }

    public void updateGameMapModelWithMap(ClientGame clientGame) {
        this.gameMapModel.updateClientGameWithMap(clientGame);
        this.gameMapModel.updateFields();
    }

    @FXML
    private void buildRoadAction(ActionEvent event) {
        try {
            this.gameService.buildRoad(this.roadsToBuildListView.getValue().getId());
        } catch (IOException e) {
           e.printStackTrace(); //TODO: signal failure to user an let him build the road again
        }
    }

    @FXML
    private void requestWheelCards(ActionEvent event) {
        try {
            this.gameService.requestWheelCards();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void selectDestinationCards(ActionEvent event) {
        try {
          this.gameService.requestShortDestinationCards();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
