package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Collection of whole information and data of a player.
 */
public class Player extends PlayerBase {
    private final List<WheelCard> wheelCards;
    private final DestinationCard longDestinationCard;
    private final List<DestinationCard> shortDestinationCards;

    //TODO List of roads built by player?

    public Player(String name, WheelColor color, int score, int wheelsRemaining, int playingOrder) {
        this(name, color, score, wheelsRemaining, playingOrder, new ArrayList<>(), null, new ArrayList<>());
    }

    public Player(String name,
                  WheelColor color,
                  int score,
                  int wheelsRemaining,
                  int playingOrder,
                  List<WheelCard> wheelCards,
                  DestinationCard longDestinationCard,
                  List<DestinationCard> shortDestinationCards) {
        super(name, color, score, wheelsRemaining, playingOrder);
        this.wheelCards = wheelCards;
        this.longDestinationCard = longDestinationCard;
        this.shortDestinationCards = shortDestinationCards;
    }

    public List<WheelCard> getWheelCards() {
        return wheelCards;
    }

    public DestinationCard getLongDestinationCard() {
        return longDestinationCard;
    }

    public List<DestinationCard> getShortDestinationCards() {
        return shortDestinationCards;
    }

    public void setColor(WheelColor color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Player player = (Player) o;
        return wheelCards.equals(player.wheelCards) && Objects.equals(longDestinationCard, player.longDestinationCard) && shortDestinationCards.equals(player.shortDestinationCards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), wheelCards, longDestinationCard, shortDestinationCards);
    }
}
