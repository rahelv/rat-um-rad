package ch.progradler.rat_um_rad.shared.models;

import ch.progradler.rat_um_rad.shared.models.game.PlayerBase;
import ch.progradler.rat_um_rad.shared.models.game.PlayerColor;
import ch.progradler.rat_um_rad.shared.models.game.PlayerEndResult;

import java.util.Objects;

/**
 * Collection of data and information of a player, visible for other clients.
 */
public class VisiblePlayer extends PlayerBase {
    private final String ipAddress;
    private final int wheelCardsCount;
    private final int shortDestinationCardsCount;

    public VisiblePlayer(String name,
                         PlayerColor color,
                         int score,
                         int wheelsRemaining,
                         int playingOrder,
                         String ipAddress,
                         int wheelCardsCount,
                         int shortDestinationCardsCount) {
        this(name, color, score, wheelsRemaining, playingOrder, ipAddress, wheelCardsCount, shortDestinationCardsCount, null);
    }

    public VisiblePlayer(String name, PlayerColor color, int score, int wheelsRemaining, int playingOrder, String ipAddress, int wheelCardsCount, int shortDestinationCardsCount, PlayerEndResult endResult) {
        super(name, color, score, wheelsRemaining, playingOrder, endResult);
        this.ipAddress = ipAddress;
        this.wheelCardsCount = wheelCardsCount;
        this.shortDestinationCardsCount = shortDestinationCardsCount;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getWheelCardsCount() {
        return wheelCardsCount;
    }

    public int getShortDestinationCardsCount() {
        return shortDestinationCardsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VisiblePlayer that = (VisiblePlayer) o;
        return wheelCardsCount == that.wheelCardsCount && shortDestinationCardsCount == that.shortDestinationCardsCount && ipAddress.equals(that.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ipAddress, wheelCardsCount, shortDestinationCardsCount);
    }
}
