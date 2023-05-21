package ch.progradler.rat_um_rad.client.gui.javafx.game.chooseCard;

import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ChooseCardModel {
    private final ControllerChangeListener<?> listener;
    private DestinationCard longDestinationCard;

    private ObservableList<DestinationCard> destinationCardList;
    private ObservableList<DestinationCard> selectedDestinationCardList;

    public ChooseCardModel(ControllerChangeListener<?> listener) {
        this.listener = listener;
        this.destinationCardList = FXCollections.observableArrayList();
    }

    public ControllerChangeListener<?> getListener() {
        return listener;
    }

    public void updateDestinationCardList(List<DestinationCard> list) {
        this.destinationCardList.clear();
        this.destinationCardList.addAll(list);
    }

    public ObservableList<DestinationCard> getDestinationCardList() {
        return destinationCardList;
    }

    public void setLongDestinationCard(DestinationCard longDestinationCard) {
        this.longDestinationCard = longDestinationCard;
    }
}
