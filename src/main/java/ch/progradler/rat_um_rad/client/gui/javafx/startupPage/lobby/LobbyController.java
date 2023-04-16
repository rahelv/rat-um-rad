package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby;

import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.io.IOException;

public class LobbyController extends GridPane {
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
        openGamesListView.setCellFactory(param -> new Cell(this.gameService)); //TODO: find a better way to handle buttonAction from Cell
    }

    public void getOpenGamesFromServer() throws IOException {
        this.gameService.requestWaitingGames();
    }

    /**
     * Cell Class to set the Cells in the List View. Add two Buttons to each cell (players and join) and sets the id as text.)
     */
    static class Cell extends ListCell<GameBase> {
        private IGameService gameService;
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();
        Button listPlayersButton = new Button("players");
        Button enterGameButton = new Button("join");

        public Cell(IGameService gameService) {
            super();
            this.gameService = gameService;
            hbox.getChildren().addAll(nameLabel, pane, listPlayersButton, enterGameButton);
            hbox.setHgrow(pane, Priority.ALWAYS);
            enterGameButton.setOnAction(event -> {
                System.out.println("wanting to join game " + getItem().getId());
                try {
                    this.gameService.joinGame(getItem().getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            listPlayersButton.setOnAction(event -> {
                System.out.println("listing all players in this game");
            });
        }

        protected void updateItem(GameBase item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty) {
                nameLabel.setText(item.getId());
                setGraphic(hbox);
            }
        }
    }
}
