package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;

import java.util.List;

/**
 * Collection of whole information and data of a player.
 */
public class Player {
    public static final int STARTING_WHEELS_PER_PLAYER = 35;
    private final String name;
    private int color;
    private int score;
    private final int wheelsRemaining;
    private List<WheelCard> wheelCards;
    private DestinationCard longDestinationCard;
    private List<DestinationCard> shortDestinationCards;

    //TODO List of roads build by player?
    public Player(String name) {
        this.name = name;
        wheelsRemaining = STARTING_WHEELS_PER_PLAYER;
    }

    public static int getStartingWheelsPerPlayer() {
        return STARTING_WHEELS_PER_PLAYER;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }

    public int getWheelsRemaining() {
        return wheelsRemaining;
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
}
