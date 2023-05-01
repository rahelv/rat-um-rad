package ch.progradler.rat_um_rad.server.repositories;

import ch.progradler.rat_um_rad.shared.models.Highscore;

import java.util.List;

/**
 * Interface which allows interacting with the game high scores and stores them.
 */
public interface IHighscoreRepository {
    void updateHighscores(List<Highscore> highscores);

    List<Highscore> getHighscores();
}
