package ch.progradler.rat_um_rad.server.repositories;

import ch.progradler.rat_um_rad.shared.models.Highscore;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IHighscoreRepository}
 */
public class HighscoreRepository implements IHighscoreRepository {

    // TODO: save to file and test!

    private List<Highscore> highscores = new ArrayList<>();

    @Override
    public void updateHighscores(List<Highscore> highscores) {
        this.highscores = highscores;
    }

    @Override
    public List<Highscore> getHighscores() {
        return highscores;
    }
}
