package ch.progradler.rat_um_rad.client.gui.javafx.game.playerOverview;

import ch.progradler.rat_um_rad.client.gui.javafx.game.UiUtil;
import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;

/**
 * Controller for custom control PlayerOverview.fxml. Activities that happened in the game are displayed in a list.
 */
public class PlayerOverviewController extends VBox {
    private PlayerOverviewModel playerOverviewModel;
    @FXML
    private ListView<VisiblePlayer> playerOverviewListView;

    public PlayerOverviewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/game/PlayerOverview.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void initData(PlayerOverviewModel playerOverviewModel) {
        this.playerOverviewModel = playerOverviewModel;
        this.playerOverviewListView.setItems(this.playerOverviewModel.getVisiblePlayers());
        this.playerOverviewListView.setCellFactory(param -> new PlayerOverviewCell()); //TODO: find a better way to handle buttonAction from Cell
    }

    public void updatePlayerOverview(List<VisiblePlayer> players) {
        this.playerOverviewModel.updatePlayers(players);
    }
    static class PlayerOverviewCell extends ListCell<VisiblePlayer> {
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();
        Label wheelCardCountLabel = new Label();
        Label wheelsRemainingLabel = new Label();
        Label shortDestinationCardsCountLabel = new Label();
        Label playerScore = new Label();

        public PlayerOverviewCell() {
            super();
            hbox.getChildren().addAll(nameLabel, wheelsRemainingLabel, wheelCardCountLabel, shortDestinationCardsCountLabel, playerScore, pane);
            HBox.setHgrow(pane, Priority.ALWAYS);
        }

        protected void updateItem(VisiblePlayer item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty) {
                nameLabel.setText(item.getName());
                wheelCardCountLabel.setText(" WHEELC: " + item.getWheelCardsCount());
                wheelsRemainingLabel.setText(" RAD: " + item.getWheelsRemaining());
                shortDestinationCardsCountLabel.setText(" ZIELC: " + item.getShortDestinationCardsCount());
                playerScore.setText(" SCORE: " + item.getScore());
                setGraphic(hbox);
            }
        }
    }
}
