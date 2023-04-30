package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.repositories.IHighscoreRepository;
import ch.progradler.rat_um_rad.shared.models.Highscore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Handles highscore logic.
 */
public class HighscoreManager {
    private final IHighscoreRepository highscoreRepository;

    public HighscoreManager(IHighscoreRepository highscoreRepository) {
        this.highscoreRepository = highscoreRepository;
    }

    /**
     * If is top 10 highscore, it is created and added to list.
     *
     * @return whether or a new highscore was added
     */
    public boolean attemptAddHighscore(String username, int score, Date date) {
        List<Highscore> highscores = new ArrayList<>(highscoreRepository.getHighscores());
        if (highscores.size() < 10) {
            highscores.add(new Highscore(username, score, date));
            highscoreRepository.updateHighscores(highscores);
            return true;
        }

        highscores.sort(Comparator.comparingInt(Highscore::getScore));
        Highscore worst = highscores.get(0);
        if (score > worst.getScore()) {
            highscores.remove(0);
            highscores.add(new Highscore(username, score, date));
            highscoreRepository.updateHighscores(highscores);
            return true;
        }
        return false;
    }


}
