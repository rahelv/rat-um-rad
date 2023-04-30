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
    private int score;
    private int wheelsRemaining;
    /**
     * Determines when it is the players turn.
     * Is set to zero before the game begins and is reassigned when game begins.
     */
    private int playingOrder;

    private PlayerEndResult endResult;

    public PlayerBase(String name, WheelColor color, int score, int wheelsRemaining, int playingOrder) {
        this(name, color, score, wheelsRemaining, playingOrder, null);
    }

    public PlayerBase(String name, WheelColor color, int score, int wheelsRemaining, int playingOrder, PlayerEndResult endResult) {
        this.name = name;
        this.color = color;
        this.score = score;
        this.wheelsRemaining = wheelsRemaining;
        this.playingOrder = playingOrder;
        this.endResult = endResult;
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

    public void setScore(int score) {
        this.score = score;
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

    public void setWheelsRemaining(int wheelsRemaining) {
        this.wheelsRemaining = wheelsRemaining;
    }

    public PlayerEndResult getEndResult() {
        return endResult;
    }

    public void setEndResult(PlayerEndResult endResult) {
        this.endResult = endResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerBase)) return false;
        PlayerBase that = (PlayerBase) o;
        return score == that.score && wheelsRemaining == that.wheelsRemaining && playingOrder == that.playingOrder && name.equals(that.name) && color == that.color && Objects.equals(endResult, that.endResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, score, wheelsRemaining, playingOrder, endResult);
    }
}
