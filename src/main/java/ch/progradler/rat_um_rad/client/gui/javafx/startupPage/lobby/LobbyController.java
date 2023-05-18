package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby;

import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LobbyController extends VBox {
    @FXML
    private ListView<GameBase> openGamesListView;
    @FXML
    private TextField gameIdTextField;
    private IGameService gameService;
    private LobbyModel lobbyModel;

    public LobbyController() {
        this.gameService = new GameService();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/LobbyControl.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void initData(LobbyModel lobbyModel) {
        this.lobbyModel = lobbyModel;

        try {
            getOpenGamesFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.openGamesListView.setItems(this.lobbyModel.getGameInfoList());
        openGamesListView.setCellFactory(param -> new Cell()); //TODO: find a better way to handle buttonAction from Cell
    }

    public void getOpenGamesFromServer() throws IOException {
        this.gameService.requestWaitingGames();
    }

    /**
     * Cell Class to set the Cells in the List View. Add two Buttons to each cell (players and join) and sets the id as text.)
     */
    class Cell extends ListCell<GameBase> {
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();
        Label playerNamesLabel = new Label();
        Button enterGameButton = new Button("join");

        public Cell() {
            super();
            hbox.getChildren().addAll(nameLabel, pane, playerNamesLabel, enterGameButton);
            HBox.setHgrow(pane, Priority.ALWAYS);
            enterGameButton.setOnAction(event -> {
                try {
                    gameService.joinGame(getItem().getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        protected void updateItem(GameBase item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty) {
                nameLabel.setText(item.getId());
                playerNamesLabel.setText(String.join(", ", item.getPlayerNames()));
                setGraphic(hbox);
            }
        }
    }
}
