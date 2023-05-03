package ch.progradler.rat_um_rad.server.repositories;

import ch.progradler.rat_um_rad.shared.database.Database;
import ch.progradler.rat_um_rad.shared.models.Highscore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HighscoreRepositoryTest {

    private final Highscore highscore1 = new Highscore("userA", 100, new Date(2023, Calendar.FEBRUARY, 4, 4, 0));
    private final Highscore highscore2 = new Highscore("userB", 56, new Date(2023, Calendar.MAY, 1, 3, 0));
    private final List<Highscore> highscores = Arrays.asList(highscore1, highscore2);
    @Mock
    Database<List<Highscore>> mockDatabase;
    private HighscoreRepository highscoreRepository;

    @BeforeEach
    void setUp() {
        highscoreRepository = new HighscoreRepository(mockDatabase);
    }

    @Test
    void initiallyReadFromDatabaseIsCalled() throws IOException {
        when(mockDatabase.read("highscores.txt")).thenReturn(highscores);
        List<Highscore> result = highscoreRepository.getHighscores();
        assertEquals(highscores, result);
    }

    @Test
    void doesNotReadFromDatabaseAfterListIsInitialized() throws IOException {
        when(mockDatabase.read("highscores.txt")).thenReturn(highscores);
        highscoreRepository.getHighscores();
        highscoreRepository.getHighscores();
        highscoreRepository.getHighscores();
        verify(mockDatabase).read("highscores.txt");
    }

    @Test
    void updateAndGetHighscores() {
        Highscore highscore1 = new Highscore("userA", 100, new Date(2023, Calendar.FEBRUARY, 4, 4, 0));
        Highscore highscore2 = new Highscore("userB", 56, new Date(2023, Calendar.MAY, 1, 3, 0));
        List<Highscore> highscores = Arrays.asList(highscore1, highscore2);
        highscoreRepository.updateHighscores(highscores);

        assertEquals(highscores, highscoreRepository.getHighscores());
    }

    @Test
    void updateSavesToDatabase() throws IOException {
        Highscore highscore1 = new Highscore("userA", 100, new Date(2023, Calendar.FEBRUARY, 4, 4, 0));
        Highscore highscore2 = new Highscore("userB", 56, new Date(2023, Calendar.MAY, 1, 3, 0));
        List<Highscore> highscores = Arrays.asList(highscore1, highscore2);
        highscoreRepository.updateHighscores(highscores);

        verify(mockDatabase).save(highscores, "highscores.txt");
    }
}