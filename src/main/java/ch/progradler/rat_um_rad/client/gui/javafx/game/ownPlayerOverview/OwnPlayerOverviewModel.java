package ch.progradler.rat_um_rad.client.gui.javafx.game.ownPlayerOverview;

import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.PlayerColor;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OwnPlayerOverviewModel {
    //TODO: implement "Stapel"
    private Player ownPlayer;
    //TODO: LongDestinationCard, WheelCount (+ Color)
    private final ObservableList<DestinationCard> destinationCards = FXCollections.observableArrayList();
    private final ObservableList<WheelCard> wheelCards = FXCollections.observableArrayList();
    private IntegerProperty wheelCount = new SimpleIntegerProperty(this, "wheelCount");
    private IntegerProperty playerScore = new SimpleIntegerProperty(this, "playerScore");

    public void updateOwnPlayer(Player player) {
        this.ownPlayer = player;
        this.destinationCards.clear();
        this.destinationCards.addAll(player.getShortDestinationCards());
        this.wheelCards.clear();
        List<WheelCard> wheelCardList = new ArrayList<>(player.getWheelCards());
        wheelCardList.sort(Comparator.comparing(WheelCard::getColor));
        this.wheelCards.addAll(wheelCardList);
        this.wheelCount.set(player.getWheelsRemaining());
        this.playerScore.set(player.getScore());
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

    public PlayerColor getWheelColor() {
        return ownPlayer.getColor();
    }

    public int getWheelCount() {
        return wheelCount.get();
    }

    public IntegerProperty wheelCountProperty() {
        return wheelCount;
    }
    public IntegerProperty playerScoreProperty() {return playerScore;}
}
