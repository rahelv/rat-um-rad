package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LobbyController implements Initializable, ServerResponseListener<List<GameBase>> {
    public Button joinButton;
    public ListView<GameBase> openGamesListView;
    public TextField gameIdTextField;
    private IGameService gameService;
    private LobbyModel lobbyModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(this);
        this.gameService = new GameService();
        this.lobbyModel = new LobbyModel();
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

    @FXML
    public void joinGame(ActionEvent actionEvent) {
       //TODO: get TextInput and send Request to Service (to join game)
    }

    /** Updates the GameList when a Server Response is received. (Listens to ServerResponseHandler)
     * @param content
     * @param contentType
     */
    @Override
    public void serverResponseReceived(List<GameBase> content, ContentType contentType) {
            this.lobbyModel.updateGameList(content);
            this.openGamesListView.setItems(this.lobbyModel.getGameInfoList());
            openGamesListView.setCellFactory(param -> new Cell(this.gameService));
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
