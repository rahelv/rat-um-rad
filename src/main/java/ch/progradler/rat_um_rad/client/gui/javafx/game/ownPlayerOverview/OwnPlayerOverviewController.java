package ch.progradler.rat_um_rad.client.gui.javafx.game.ownPlayerOverview;

import ch.progradler.rat_um_rad.client.gui.javafx.game.UiUtil;
import ch.progradler.rat_um_rad.shared.models.game.City;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * Controller for custom control OwnPlayerOverview.fxml. Activities that happened in the game are displayed in a list.
 */
public class OwnPlayerOverviewController extends VBox {
    private OwnPlayerOverviewModel ownPlayerOverviewModel;
    @FXML
    private ListView<DestinationCard> destinationCardListView;
    @FXML
    private ListView<WheelCard> wheelCardListView;
    @FXML
    private Label wheelCount;
    @FXML
    private Label playerScore;

    public OwnPlayerOverviewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/game/OwnPlayerOverview.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void initData(OwnPlayerOverviewModel ownPlayerOverviewModel) {
        this.ownPlayerOverviewModel = ownPlayerOverviewModel;
        this.destinationCardListView.setItems(this.ownPlayerOverviewModel.getDestinationCards());
        this.destinationCardListView.setCellFactory(param -> new DestinationCardCell());
        this.wheelCardListView.setItems(ownPlayerOverviewModel.getWheelCards());
        this.wheelCardListView.setCellFactory(param -> new WheelCardCell());
        this.wheelCount.textProperty().bind(Bindings.convert(this.ownPlayerOverviewModel.wheelCountProperty()));
        this.playerScore.textProperty().bind(Bindings.convert(this.ownPlayerOverviewModel.playerScoreProperty()));
    }

    public void updatePlayer(Player player) {
        this.ownPlayerOverviewModel.updateOwnPlayer(player);
    }

    static class DestinationCardCell extends ListCell<DestinationCard> {
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();

        public DestinationCardCell() {
            super();
            hbox.getChildren().addAll(nameLabel, pane);
            HBox.setHgrow(pane, Priority.ALWAYS);
        }

        protected void updateItem(DestinationCard card, boolean empty) {
            super.updateItem(card, empty);
            setText(null);
            setGraphic(null);
            if (card != null && !empty) {
                City from = card.getDestination1();
                City to = card.getDestination2();
                nameLabel.setText(from.getName() + "\n --> \n" + to.getName()
                        + "\n" + card.getPoints() + " points");
                setGraphic(hbox);
            }
        }
    }

    static class WheelCardCell extends ListCell<WheelCard> {
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();

        public WheelCardCell() {
            super();
            hbox.getChildren().addAll(nameLabel, pane);
            HBox.setHgrow(pane, Priority.ALWAYS);
        }

        protected void updateItem(WheelCard item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty) {
                hbox.setBackground(new Background(new BackgroundFill(Color.valueOf(item.getColor().toString()), CornerRadii.EMPTY, Insets.EMPTY)));
                nameLabel.setText(item.getColor().toString()); //TODO: check if color is correctly converted to string
                setGraphic(hbox);
            }
        }
    }
}
