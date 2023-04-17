package ch.progradler.rat_um_rad.client.gui.javafx.game.chooseCard;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ArrayList;
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
    List<String> selectedRoadIdList = new ArrayList<>();

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
        this.cardsListView.setCellFactory(CheckBoxListCell.forListView(new Callback<DestinationCard, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(DestinationCard item) {
                CheckBoxListCell<DestinationCard> cell = new CheckBoxListCell<DestinationCard>() {
                    @Override
                    public void updateItem(DestinationCard item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(null);
                        setGraphic(null);
                        if (item != null && !empty) {
                            //TODO: text is never displayed correctly
                            setText(item.getDestination1().getName() + " to " + item.getDestination2().getName());
                        }
                    }
                };
                cell.setConverter(new StringConverter() {
                    @Override
                    public String toString(Object destinationCard) {
                        return ((DestinationCard)destinationCard).getDestination1().getName() + " " + ((DestinationCard)destinationCard).getDestination2().getName();
                    }
                    @Override
                    public Object fromString(String string) {
                        return string;
                    }
                });

                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) -> {
                    if (isNowSelected) {
                        selectedRoadIdList.add(item.getCardID());
                    } else {
                        selectedRoadIdList.remove(item.getCardID());
                    }
                });
                return observable;
            }
        }));
        this.stage = window;
    }

    /** bound to chooseCardButton in View. sends request to server to create game through gameService.
     * @param actionEvent
     */
    @FXML
    public void chooseCardsAction(ActionEvent actionEvent) {
        List<String> selectedCards = this.selectedRoadIdList;
        this.gameService.selectCards(selectedCards);
    }
}
