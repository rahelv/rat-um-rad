package ch.progradler.rat_um_rad.server.repositories;

import ch.progradler.rat_um_rad.shared.database.Database;
import ch.progradler.rat_um_rad.shared.models.Highscore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IHighscoreRepository}
 * Saves highscores to database.
 */
public class HighscoreRepository implements IHighscoreRepository {
    private static final Logger LOGGER = LogManager.getLogger();

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
            LOGGER.warn("Failed to save highscores to database", e);
        }
    }

    @Override
    public List<Highscore> getHighscores() {
        if (highscores == null) {
            try {
                highscores = database.read(DATABASE_KEY);
            } catch (NoSuchFileException e) {
                highscores = new ArrayList<>();
            } catch (IOException e) {
                highscores = new ArrayList<>();
                LOGGER.warn("Failed to read highscores from database", e);
            }
        }
        return highscores;
    }
}
