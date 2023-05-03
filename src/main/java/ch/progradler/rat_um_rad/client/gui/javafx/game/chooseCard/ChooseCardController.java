package ch.progradler.rat_um_rad.client.gui.javafx.game.chooseCard;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChooseCardController {

    List<String> selectedRoadIdList = new ArrayList<>();
    private Stage stage;
    @FXML
    private Button chooseCardsButton;
    @FXML
    private ListView cardsListView;
    private ChooseCardModel chooseCardModel;
    private IGameService gameService;

    public ChooseCardController() {
        this.gameService = new GameService();
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<ClientGame>() {
            @Override
            public void serverResponseReceived(ClientGame content) {
                try {
                    destinationCardsSelectedReturnToGame(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } //TODO: what is received and react to errors

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.DESTINATION_CARDS_SELECTED;
            }
        });
    }

    /**
     * initializes the model which comes from the GUI class.
     *
     * @param chooseCardModel
     * @param window
     */
    public void initData(ChooseCardModel chooseCardModel, Stage window) {
        //TODO: multiple types of cards to chose from
        this.chooseCardModel = chooseCardModel;
        this.cardsListView.setItems(this.chooseCardModel.getDestinationCardList());
        this.cardsListView.setCellFactory(param -> new DestinationCardCell());
        this.stage = window;
    }

    /**
     * bound to chooseCardButton in View. sends request to server to create game through gameService.
     *
     * @param actionEvent
     */
    @FXML
    public void chooseCardsAction(ActionEvent actionEvent) {
        List<String> selectedCards = this.selectedRoadIdList;
        try {
            this.gameService.selectCards(selectedCards);
            this.selectedRoadIdList.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destinationCardsSelectedReturnToGame(ClientGame clientGame) throws IOException {
        Platform.runLater(() -> {
            chooseCardModel.getListener().returnToGame(clientGame);
        });
    }

    private class DestinationCardCell extends CheckBoxListCell<DestinationCard> {
        StringConverter<DestinationCard> stringConverter = new StringConverter<>() {
            @Override
            public String toString(DestinationCard destinationCard) {
                return destinationCard.getDestination1().getName() + " to " + destinationCard.getDestination2().getName();
            }

            @Override
            public DestinationCard fromString(String string) {
                // returns null because never used
                return null;
            }
        };

        Callback<DestinationCard, ObservableValue<Boolean>> callback = item -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            observable.addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    selectedRoadIdList.add(item.getCardID());
                } else {
                    selectedRoadIdList.remove(item.getCardID());
                }
            });
            return observable;
        };

        public DestinationCardCell() {
            super();
            setConverter(stringConverter);
            setSelectedStateCallback(callback);
        }
    }
}
