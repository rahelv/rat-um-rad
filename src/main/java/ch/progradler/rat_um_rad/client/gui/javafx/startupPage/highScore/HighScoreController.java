package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.highScore;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.Highscore;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class HighScoreController {
    Stage stage;
    @FXML
    private Button backToLobbyButton;
    @FXML
    private ListView highScoreListView;
    private IGameService gameService;
    private HighScoreModel highScoreModel;

    public HighScoreController() {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<List<Highscore>>() {
            @Override
            public void serverResponseReceived(List<Highscore> content) {
                highScoreReceived(content);
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.SEND_HIGHSCORES;
            }
        });

        this.gameService = new GameService();
    }

    public void initData(HighScoreModel model, Stage stage) {
        this.stage = stage;
        this.highScoreModel = model;

        try {
            this.gameService.requestHighscores();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void returnButtonAction(ActionEvent event) {
        Platform.runLater(() -> {
            highScoreModel.getListener().controllerChanged("showStartupPage"); //returns to main Page...
        });
    }

    public void highScoreReceived(List<Highscore> content) {
        this.highScoreModel.setHighScoreList(content);
        this.highScoreListView.setItems(this.highScoreModel.getHighScoreList());
        highScoreListView.setCellFactory(param -> new Cell());
    }

    static class Cell extends ListCell<Highscore>{
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();

        public Cell() {
            super();
            hbox.getChildren().addAll(nameLabel, pane);
            hbox.setHgrow(pane, Priority.ALWAYS);
        }

        protected void updateItem(Highscore item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty) {
                nameLabel.setText("user: " + item.getUsername() + " score: " + item.getScore());
                setGraphic(hbox);
            }
        }
    }
}
