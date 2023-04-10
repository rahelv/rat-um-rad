package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview;

import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.net.URL;
import java.util.ResourceBundle;

public class ShowAllGamesController implements Initializable {
    public Button backToLobbyButton;
    public ListView openGamesListView;
    public ListView onGoingListView;
    public ListView finishedGamesListView;

    private IGameService gameService;
    private ShowAllGamesModel showAllGamesModel;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.gameService = new GameService();
        this.showAllGamesModel = new ShowAllGamesModel();

        this.openGamesListView.setItems(this.showAllGamesModel.getOpenGameList());
        openGamesListView.setCellFactory(param -> new ShowAllGamesController.OpenGameCell());

        this.onGoingListView.setItems(this.showAllGamesModel.getOngoingGameList());
        onGoingListView.setCellFactory(param -> new ShowAllGamesController.Cell());

        this.finishedGamesListView.setItems(this.showAllGamesModel.getFinishedGameList());
        finishedGamesListView.setCellFactory(param -> new ShowAllGamesController.Cell());
    }
    static class OpenGameCell extends ListCell<GameBase> {
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();
        Button listPlayersButton = new Button("players");
        Button enterGameButton = new Button("join");
        public OpenGameCell() {
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
    static class Cell extends ListCell<GameBase>{
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();
        Button listPlayersButton = new Button("players");
        public Cell() {
            super();
            hbox.getChildren().addAll(nameLabel,pane,listPlayersButton);
            hbox.setHgrow(pane, Priority.ALWAYS);
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
}
