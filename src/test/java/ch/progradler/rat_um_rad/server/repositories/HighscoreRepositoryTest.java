package ch.progradler.rat_um_rad.server.repositories;

import ch.progradler.rat_um_rad.shared.models.Highscore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HighscoreRepositoryTest {

    private HighscoreRepository highscoreRepository;

    @BeforeEach
    void setUp() {
        highscoreRepository = new HighscoreRepository();
    }

    @Test
    void updateAndGetHighscores() {
        assertTrue(highscoreRepository.getHighscores().isEmpty());

        Highscore highscore1 = new Highscore("userA", 100, new Date(2023, Calendar.FEBRUARY, 4, 4, 0));
        Highscore highscore2 = new Highscore("userB", 56, new Date(2023, Calendar.MAY, 1, 3, 0));
        List<Highscore> highscores = Arrays.asList(highscore1, highscore2);
        highscoreRepository.updateHighscores(highscores);

        assertEquals(highscores, highscoreRepository.getHighscores());
    }
}