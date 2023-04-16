package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

import java.util.Objects;

/**
 * Since the players kept by the server and the own player kept by each client {@link Player} is different from the
 * other players kept by the client {@link VisiblePlayer}, both extend this class.
 */
public class PlayerBase {
    private final String name;
    protected WheelColor color;
    private final int score;
    private final int wheelsRemaining;
    /**
     * Determines when it is the players turn.
     * Is set to zero before the game begins and is reassigned when game begins.
     */
    private int playingOrder;

    //TODO List of roads build by player?

    public PlayerBase(String name, WheelColor color, int score, int wheelsRemaining, int playingOrder) {
        this.name = name;
        this.color = color;
        this.score = score;
        this.wheelsRemaining = wheelsRemaining;
        this.playingOrder = playingOrder;
    }

    public String getName() {
        return name;
    }

    public WheelColor getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }

    public int getWheelsRemaining() {
        return wheelsRemaining;
    }

    public int getPlayingOrder() {
        return playingOrder;
    }

    public void setPlayingOrder(int playingOrder) {
        this.playingOrder = playingOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerBase)) return false;
        PlayerBase that = (PlayerBase) o;
        return score == that.score && wheelsRemaining == that.wheelsRemaining && playingOrder == that.playingOrder && name.equals(that.name) && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, score, wheelsRemaining, playingOrder);
    }
}
