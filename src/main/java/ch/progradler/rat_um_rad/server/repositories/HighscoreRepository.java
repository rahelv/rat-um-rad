package ch.progradler.rat_um_rad.server.repositories;

import ch.progradler.rat_um_rad.shared.database.Database;
import ch.progradler.rat_um_rad.shared.models.Highscore;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IHighscoreRepository}
 * Saves highscores to database.
 */
public class HighscoreRepository implements IHighscoreRepository {
    private static final String DATABASE_KEY = "highscores.txt";

    private final Database<List<Highscore>> database;

    private List<Highscore> highscores;

    public HighscoreRepository(Database<List<Highscore>> database) {
        this.database = database;
    }

    @Override
    public void updateHighscores(List<Highscore> highscores) {
        this.highscores = highscores;
        try {
            database.save(highscores, DATABASE_KEY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Highscore> getHighscores() {
        if (highscores == null) {
            try {
                highscores = database.read(DATABASE_KEY);
            } catch (NoSuchFileException e) {
                highscores = new ArrayList<>();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return highscores;
    }
}
