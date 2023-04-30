package ch.progradler.rat_um_rad.shared.models;

import java.util.Date;
import java.util.Objects;

/**
 * Holds information on a high score.
 */
public class Highscore {
    private final String username;
    private final int score;
    private final Date date;

    public Highscore(String username, int score, Date date) {
        this.username = username;
        this.score = score;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Highscore)) return false;
        Highscore highscore = (Highscore) o;
        return score == highscore.score && username.equals(highscore.username) && date.equals(highscore.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, score, date);
    }
}
