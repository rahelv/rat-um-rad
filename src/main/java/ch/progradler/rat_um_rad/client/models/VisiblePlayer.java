package ch.progradler.rat_um_rad.client.models;

import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;

import java.util.List;

/**
 * Collection of data and information of a player, visible for other clients.
 */
public class VisiblePlayer {
    public static final int STARTING_WHEELS_PER_PLAYER = 35;
    private final String name;
    private int color;
    private int score;
    private final int wheelsRemaining;

    //TODO List of roads build by player?
    public VisiblePlayer(String name) {
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
}
