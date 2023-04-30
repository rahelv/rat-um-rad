package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;

import java.util.List;
import java.util.Objects;

/**
 * Holds data of the achievements of a player the end of a game
 */
public class PlayerEndResult {
    private final List<DestinationCard> achievedShorts;
    private final List<DestinationCard> notAchievedShorts;
    private final boolean achievedLong;
    // TODO: private final int longest connection

    public PlayerEndResult(List<DestinationCard> achievedShorts, List<DestinationCard> notAchievedShorts, boolean achievedLong) {
        this.achievedShorts = achievedShorts;
        this.notAchievedShorts = notAchievedShorts;
        this.achievedLong = achievedLong;
    }

    public List<DestinationCard> getAchievedShorts() {
        return achievedShorts;
    }

    public List<DestinationCard> getNotAchievedShorts() {
        return notAchievedShorts;
    }

    public boolean hasAchievedLong() {
        return achievedLong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerEndResult)) return false;
        PlayerEndResult that = (PlayerEndResult) o;
        return achievedLong == that.achievedLong && achievedShorts.equals(that.achievedShorts) && notAchievedShorts.equals(that.notAchievedShorts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(achievedShorts, notAchievedShorts, achievedLong);
    }
}
