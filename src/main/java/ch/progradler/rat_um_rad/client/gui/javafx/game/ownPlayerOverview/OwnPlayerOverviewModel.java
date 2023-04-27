package ch.progradler.rat_um_rad.client.gui.javafx.game.ownPlayerOverview;

import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OwnPlayerOverviewModel {
    //TODO: implement "Stapel"
    private Player ownPlayer;
    //TODO: LongDestinationCard, WheelCount (+ Color)
    private final ObservableList<DestinationCard> destinationCards = FXCollections.observableArrayList();
    private final ObservableList<WheelCard> wheelCards = FXCollections.observableArrayList();
    private IntegerProperty wheelCount = new SimpleIntegerProperty(this, "wheelCount");
    public void updateOwnPlayer(Player player) {
        this.ownPlayer = player;
        this.destinationCards.clear();
        this.destinationCards.addAll(player.getShortDestinationCards());
        this.wheelCards.clear();
        this.wheelCards.addAll(player.getWheelCards());
        this.wheelCount.set(player.getWheelsRemaining());
    }

    public ObservableList<DestinationCard> getDestinationCards() {
        return destinationCards;
    }

    public ObservableList<WheelCard> getWheelCards() {
        return wheelCards;
    }

    public Player getOwnPlayer() {
        return ownPlayer;
    }
    public WheelColor getWheelColor() {
        return ownPlayer.getColor();
    }

    public int getWheelCount() {
        return wheelCount.get();
    }

    public IntegerProperty wheelCountProperty() {
        return wheelCount;
    }
}
