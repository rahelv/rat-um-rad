package ch.progradler.rat_um_rad.server.repositories;

import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class GameRepositoryTest {
    private static final String GAME_ID_1 = "game1";
    private GameRepository gameRepository;
    private Game game1;

    @BeforeEach
    public void initRepository() throws IGameRepository.DuplicateIdException {
        game1 = new Game(GAME_ID_1,
                GameStatus.WAITING_FOR_PLAYERS,
                GameMap.defaultMap(),
                "creator",
                4,
                new HashMap<>());
        gameRepository = new GameRepository();
        gameRepository.addGame(game1);
    }

    @Test
    void isEmptyAfterConstruction() {
        gameRepository = new GameRepository();
        assertEquals(0, gameRepository.getGamesCount());
    }

    @Test
    void hasCorrectCountAfterAddingAndUpdating() throws IGameRepository.DuplicateIdException {
        assertEquals(1, gameRepository.getGamesCount());
        gameRepository.addGame(new Game("newGame",
                GameStatus.FINISHED,
                GameMap.defaultMap(),
                "creator",
                3,
                new HashMap<>()));
        assertEquals(2, gameRepository.getGamesCount());
        gameRepository.getGame("client3"); // does nothing
        assertEquals(2, gameRepository.getGamesCount());
        gameRepository.updateGame(new Game(GAME_ID_1,
                GameStatus.FINISHED,
                GameMap.defaultMap(),
                "creator",
                4,
                new HashMap<>()));  // does nothing
        assertEquals(2, gameRepository.getGamesCount());
    }

    @Test
    void addAndGetGame() throws IGameRepository.DuplicateIdException {
        String gameId = "gameABC";
        Game newGame = new Game(gameId,
                GameStatus.WAITING_FOR_PLAYERS,
                GameMap.defaultMap(),
                "creatorABC",
                4,
                new HashMap<>());
        assertNull(gameRepository.getGame(gameId));
        gameRepository.addGame(newGame);
        assertEquals(newGame, gameRepository.getGame(gameId));
    }

    @Test
    void updateGame() {
        Game updatedGame1 = new Game(GAME_ID_1,
                GameStatus.STARTED, // new status
                GameMap.defaultMap(),
                "creator",
                4,
                new HashMap<>());
        assertEquals(game1, gameRepository.getGame(GAME_ID_1));
        gameRepository.updateGame(updatedGame1);
        assertEquals(updatedGame1, gameRepository.getGame(GAME_ID_1));
    }

    @Test
    void getAll() throws IGameRepository.DuplicateIdException {
        Game game2 = new Game("game2",
                GameStatus.WAITING_FOR_PLAYERS,
                GameMap.defaultMap(),
                "creatorABC",
                4,
                new HashMap<>());
        gameRepository.addGame(game2);
        assertArrayEquals(new Game[]{game1, game2}, gameRepository.getAllGames().toArray());
    }

    @Test
    void createThrowsErrorIfGameWithSameIdAlreadyExists() {
        assertThrows(IGameRepository.DuplicateIdException.class, () -> {
            Game newGame = new Game(GAME_ID_1,
                    GameStatus.WAITING_FOR_PLAYERS,
                    GameMap.defaultMap(),
                    "creatorA",
                    3,
                    new HashMap<>());
            gameRepository.addGame(newGame);
        });
    }
}