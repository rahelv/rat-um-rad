package ch.progradler.rat_um_rad.server.repositories;

import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.*;

/**
 * Implementation of {@link IGameRepository}.
 * Stores all {@link Game} instances in a {@link Map} where the keys are the ids of the games.
 */
public class GameRepository implements IGameRepository {

    /**
     * Keys are ids of games
     */
    private final Map<String, Game> games = new HashMap<>();

    @Override
    public void addGame(Game game) throws DuplicateIdException {
        if (games.containsKey(game.getId())) {
            throw new DuplicateIdException(game.getId());
        }
        games.put(game.getId(), game);
    }

    @Override
    public Game getGame(String id) {
        return games.get(id);
    }

    @Override
    public void updateGame(Game game) {
        games.put(game.getId(), game);
    }

    @Override
    public List<Game> getAllGames() {
        return games.values().stream().toList();
    }

    @Override
    public int getGamesCount() {
        return games.size();
    }

    public List<Game> getWaitingGames() {
        return getGamesWithStatus(WAITING_FOR_PLAYERS);
    }

    /**
     * Since started games can have one of the states {@link GameStatus#PREPARATION} amd
     * {@link GameStatus#STARTED}, this method returns a list with both.
     */
    public List<Game> getStartedGames() {
        List<Game> preparingGames = getGamesWithStatus(PREPARATION);
        List<Game> startedGames = getGamesWithStatus(STARTED);
        preparingGames.addAll(startedGames);
        return preparingGames;
    }

    public List<Game> getFinishedGames() {
        return getGamesWithStatus(FINISHED);
    }

    private List<Game> getGamesWithStatus(GameStatus gameStatus) {
        return games.values().stream()
                .filter((game) -> game.getStatus() == gameStatus).toList();
    }
}
