package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.repositories.IHighscoreRepository;
import ch.progradler.rat_um_rad.shared.models.Highscore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HighscoreManagerTest {
    @Mock
    IHighscoreRepository mockHighscoreRepository;

    private HighscoreManager highscoreManager;

    private static List<Highscore> generateHighscores(String... usernames) {
        List<Highscore> highscores = new ArrayList<>();

        Random random = new Random();

        for (String username : usernames) {
            highscores.add(new Highscore(username, random.nextInt(100), new Date()));
        }
        return highscores;
    }

    @BeforeEach
    void setUp() {
        highscoreManager = new HighscoreManager(mockHighscoreRepository);
    }

    @Test
    void addsHighscoreIfCurrentListHasLessThan9Highscores() {
        List<Highscore> highscores = generateHighscores("u1", "u2", "u3", "u4", "u5", "u6", "u7", "u8", "u9");
        when(mockHighscoreRepository.getHighscores()).thenReturn(highscores);

        Date date = new Date();

        assertTrue(highscoreManager.attemptAddHighscore("u1", -10, date));
        List<Highscore> expectedUpdate = new ArrayList<>(highscores);
        expectedUpdate.add(new Highscore("u1", -10, date));
        verify(mockHighscoreRepository).updateHighscores(expectedUpdate);
    }

    @Test
    void doesNotAddHighscoreIfCurrentListFullAndIsNotNewHighscore() {
        Highscore highscore = new Highscore("userA", 100, new Date());
        List<Highscore> highscores = Collections.nCopies(10, highscore);
        when(mockHighscoreRepository.getHighscores()).thenReturn(highscores);

        assertFalse(highscoreManager.attemptAddHighscore("userB", 100, new Date()));
        verify(mockHighscoreRepository, never()).updateHighscores(anyList());
    }

    @Test
    void addsHighscoreIfNewHighscoreAndRemovesWorstScore() {
        Highscore goodOne = new Highscore("userA", 100, new Date());
        List<Highscore> highscores = new ArrayList<>(Collections.nCopies(9, goodOne));
        Highscore worst = new Highscore("userB", 50, new Date());
        highscores.add(worst);

        when(mockHighscoreRepository.getHighscores()).thenReturn(highscores);
        Date date = new Date();

        assertTrue(highscoreManager.attemptAddHighscore("userC", 51, date));

        List<Highscore> expectedUpdate = new ArrayList<>(highscores);
        expectedUpdate.remove(worst);
        expectedUpdate.add(new Highscore("userC", 51, date));
        verify(mockHighscoreRepository).updateHighscores(expectedUpdate);
    }
}