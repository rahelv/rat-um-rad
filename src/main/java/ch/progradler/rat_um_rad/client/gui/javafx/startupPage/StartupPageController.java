package ch.progradler.rat_um_rad.client.gui.javafx.startupPage;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby.LobbyController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby.LobbyModel;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class StartupPageController {
    public ServerResponseListener<List<String>> allPlayersListener;
    Stage stage;
    UsernameChangeModel usernameChangeModel;
    private UserService userService;
    @FXML
    private LobbyController lobbyController;
    private StartupPageModel startupPageModel;
    @FXML
    private Button currentPlayersLabelButton;

    @FXML
    private Label welcomeLabel;

    public StartupPageController() {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<ClientGame>() {
            @Override
            public void serverResponseReceived(ClientGame content) {
                gameJoined(content);
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.GAME_JOINED;
            }
        });

        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<List<String>>() {
            @Override
            public void serverResponseReceived(List<String> content) {
                handleAllPlayersUpdate(content);
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.SEND_ALL_CONNECTED_PLAYERS;
            }
        });
        this.userService = new UserService();
    }

    @FXML
    private void changeUsernameButtonTriggered(ActionEvent event) {
        this.showUsernameChangeDialog();
    }

    public void initData(UsernameChangeModel usernameChangeModel, StartupPageModel startupPageModel, Stage stage, LobbyModel lobbyModel) {
        this.usernameChangeModel = usernameChangeModel;
        this.startupPageModel = startupPageModel;
        this.stage = stage;
        welcomeLabel.setText("Herzlich Willkommen " + usernameChangeModel.getCurrentUsername() + " !");

        this.lobbyController.initData(lobbyModel);
        try {
            this.userService.requestOnlinePlayers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showUsernameChangeDialog() {
        Platform.runLater(() -> {
            startupPageModel.getListener().controllerChanged("showUsernameChange");
        });
    }

    @FXML
    public void showAllGamesAction(ActionEvent event) {
        Platform.runLater(() -> {
            startupPageModel.getListener().controllerChanged("showAllGamesView");
        });
    }

    @FXML
    public void showHighScores(ActionEvent event) {
        Platform.runLater(() -> {
            startupPageModel.getListener().controllerChanged("showHighScores");
        });
    }

    @FXML
    public void createGameAction(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            startupPageModel.getListener().controllerChanged("createGame");
        });
    }

    public void gameJoined(ClientGame content) {
        Platform.runLater(() -> {
            startupPageModel.getListener().gameCreated(content);
        });
    }

    private void handleAllPlayersUpdate(List<String> players) {
        Platform.runLater(() -> {
            startupPageModel.addPlayersToList(players);
            this.currentPlayersLabelButton.setText("Currently Online Players: " + String.valueOf(startupPageModel.getOnlinePlayersCount()));
            this.currentPlayersLabelButton.setTooltip(new Tooltip(startupPageModel.getOnlinePlayersListAsString()));
        });
    }

}
