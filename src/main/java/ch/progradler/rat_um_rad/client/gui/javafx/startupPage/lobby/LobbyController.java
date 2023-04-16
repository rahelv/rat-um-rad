package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.createGame.CreateGameController;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LobbyController implements Initializable, ServerResponseListener<List<GameBase>> {
    public Button showAllGamesButton;
    public Button leaveLobbyButton;
    public Button createGameButton;
    public Button joinButton;
    public ListView<GameBase> openGamesListView;
    public TextArea currentPlayersTextArea;
    public TextField gameIdTextField;

    private IGameService gameService;
    private LobbyModel lobbyModel;
    private ServerResponseListener<List<String>> allPlayerListener;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(this);
        this.gameService = new GameService();
        this.lobbyModel = new LobbyModel();
       /**try {
            this.gameService.requestWaitingGames();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        this.openGamesListView.setItems(this.lobbyModel.getGameInfoList());
        openGamesListView.setCellFactory(param -> new Cell());
        //each item of listView should have 2 buttons:list players and join game

        allPlayerListener = this::handleAllPlayersUpdate;
        currentPlayersTextArea.textProperty().bindBidirectional(lobbyModel.allOnlinePlayersProperty());
    }


    public void getOpenGamesFromServer() throws IOException{
        this.gameService.requestWaitingGames();
    }

    @FXML
    public void showAllGamesAction(ActionEvent event){
        System.out.println("show all games");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/showAllGamesView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Rat um Rad - show all games");
            Scene scene = new Scene(root, 700, 600);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.out.println("load failed");
            e.printStackTrace();
        }
    }

    @FXML
    public void leaveLobbyAction(ActionEvent actionEvent) {
        System.out.println("leave lobby");
    }

    @FXML
    public void createGameAction(ActionEvent actionEvent) {
        //TODO: create Game dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/createGameView.fxml"));

        try {
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("Rat um Rad - create game");
            Scene scene = new Scene(loader.load(), 640, 480);
            stage.setScene(scene);
            stage.show();

            CreateGameController controller = loader.getController();
            //controller.initData(stage);
        } catch (Exception e) {
            System.out.println("load failed");
            e.printStackTrace();
        }
    }

    @FXML
    public void joinGameAction(ActionEvent actionEvent) {
        //TODO: get gameId (bind to textfield wo id eingegeben wurde)
        System.out.println("joined game ");
        //TODO: Anfrage an Server um Game zu joinen
    }

    @Override
    public void serverResponseReceived(List<GameBase> content, ContentType contentType) {
        this.lobbyModel.updateGameList(content);
    }

    static class Cell extends ListCell<GameBase> {
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();
        Button listPlayersButton = new Button("players");
        Button enterGameButton = new Button("join");
        public Cell() {
            super();
            hbox.getChildren().addAll(nameLabel,pane,listPlayersButton, enterGameButton);
            hbox.setHgrow(pane, Priority.ALWAYS);
            enterGameButton.setOnAction(event -> {
                System.out.println("wanting to join game " + getItem().getId());
                //TODO: send anfrage to server: OutputPacketGatewaySingleton.;
            });
            listPlayersButton.setOnAction(event -> {
                System.out.println("listing all players in this game");
            });
        }
        protected void updateItem(GameBase item, boolean empty){
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if(item != null && !empty){
                nameLabel.setText(item.getId());
                setGraphic(hbox);
            }
        }
    }
    public interface AllOnlinePlayersListener extends ServerResponseListener<List<String>>{

    }
    private void handleAllPlayersUpdate(List<String> content, ContentType contentType) {
        Platform.runLater(() -> {
            lobbyModel.allOnlinePlayersProperty = new SimpleStringProperty("All online players : "+content.size());
            System.out.println("here should be invoked");
        });
    }
}
