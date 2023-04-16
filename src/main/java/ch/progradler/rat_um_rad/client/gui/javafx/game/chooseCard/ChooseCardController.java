package ch.progradler.rat_um_rad.client.gui.javafx.game.chooseCard;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChooseCardController implements Initializable {
    private Stage stage;
    @FXML
    private Button chooseCardsButton;
    @FXML
    private ListView cardsListView;
    private ChooseCardModel chooseCardModel;
    private IGameService gameService;

    /** initializes the controller.
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.gameService = new GameService();
    }

    /** initializes the model which comes from the GUI class.
     * @param chooseCardModel
     * @param window
     */
    public void initData(ChooseCardModel chooseCardModel, Stage window) {
        //TODO: multiple types of cards to chose from
        this.chooseCardModel = chooseCardModel;
        this.cardsListView.setItems(this.chooseCardModel.getDestinationCardList());
        this.cardsListView.setCellFactory(param -> new CardCell());
        this.stage = window;
    }

    /** bound to chooseCardButton in View. sends request to server to create game through gameService.
     * @param actionEvent
     */
    @FXML
    public void chooseCardsAction(ActionEvent actionEvent) {
        List<DestinationCard> selectedCards = cardsListView.getSelectionModel().getSelectedItems().stream().toList();
        this.gameService.selectCards(selectedCards);
    }

    /**
     * Cell Class to set the Cells in the List View. Add two Buttons to each cell (players and join) and sets the id as text.)
     */
    static class CardCell extends ListCell<DestinationCard> {
        Pane pane = new Pane();
        HBox hbox = new HBox();
        Label nameLabel = new Label();
        CheckBox cardSelectedCheckBox = new CheckBox();

        public CardCell() {
            super();
            hbox.getChildren().addAll(nameLabel, pane, cardSelectedCheckBox);
            hbox.setHgrow(pane, Priority.ALWAYS);
            cardSelectedCheckBox.setOnAction(event -> {
                //TODO:
            });
        }

        protected void updateItem(DestinationCard item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty) {
                nameLabel.setText(item.getDestination1() + " to " + item.getDestination2());
                setGraphic(hbox);
            }
        }
    }
}
