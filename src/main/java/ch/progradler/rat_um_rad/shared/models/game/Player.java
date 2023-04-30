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
    private DestinationCard longDestinationCard;
    private List<DestinationCard> shortDestinationCards;
    private List<DestinationCard> shortDestinationCardsToChooseFrom;

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
        this(name,
                color,
                score,
                wheelsRemaining,
                playingOrder,
                wheelCards,
                longDestinationCard,
                shortDestinationCards,
                new ArrayList<>());
    }

    public Player(String name, WheelColor color, int score, int wheelsRemaining, int playingOrder,
                  List<WheelCard> wheelCards,
                  DestinationCard longDestinationCard,
                  List<DestinationCard> shortDestinationCards,
                  List<DestinationCard> shortDestinationCardsToChooseFrom) {
        this(name, color, score, wheelsRemaining, playingOrder, wheelCards, longDestinationCard, shortDestinationCards, shortDestinationCardsToChooseFrom, null);
    }

    public Player(String name, WheelColor color, int score, int wheelsRemaining, int playingOrder,
                  List<WheelCard> wheelCards,
                  DestinationCard longDestinationCard,
                  List<DestinationCard> shortDestinationCards,
                  List<DestinationCard> shortDestinationCardsToChooseFrom,
                  PlayerEndResult endResult) {
        super(name, color, score, wheelsRemaining, playingOrder, endResult);
        this.wheelCards = wheelCards;
        this.longDestinationCard = longDestinationCard;
        this.shortDestinationCards = shortDestinationCards;
        this.shortDestinationCardsToChooseFrom = shortDestinationCardsToChooseFrom;
    }

    public List<WheelCard> getWheelCards() {
        return wheelCards;
    }

    public DestinationCard getLongDestinationCard() {
        return longDestinationCard;
    }

    public void setLongDestinationCard(DestinationCard longDestinationCard) {
        this.longDestinationCard = longDestinationCard;
    }

    public List<DestinationCard> getShortDestinationCards() {
        return shortDestinationCards;
    }

    public void addShortDestinationCards(List<DestinationCard> shortDestinationCards) {
        this.shortDestinationCards.addAll(shortDestinationCards);
    }

    public List<DestinationCard> getShortDestinationCardsToChooseFrom() {
        return shortDestinationCardsToChooseFrom;
    }

    public void setShortDestinationCardsToChooseFrom(List<DestinationCard> shortDestinationCardsToChooseFrom) {
        this.shortDestinationCardsToChooseFrom = shortDestinationCardsToChooseFrom;
    }

    public void setColor(WheelColor color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        if (!super.equals(o)) return false;
        Player player = (Player) o;
        return wheelCards.equals(player.wheelCards) && Objects.equals(longDestinationCard, player.longDestinationCard) && shortDestinationCards.equals(player.shortDestinationCards) && shortDestinationCardsToChooseFrom.equals(player.shortDestinationCardsToChooseFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), wheelCards, longDestinationCard, shortDestinationCards, shortDestinationCardsToChooseFrom);
    }
}
