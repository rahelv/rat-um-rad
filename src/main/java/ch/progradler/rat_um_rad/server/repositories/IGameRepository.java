package ch.progradler.rat_um_rad.server.repositories;

import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;

import java.util.List;

/**
 * Interface which allows interacting with the game instances and stores them.
 */
public interface IGameRepository {
    void addGame(Game game) throws DuplicateIdException;

    Game getGame(String id);

    void updateGame(Game game);

    /**
     * @return List of game instances that have not yet started
     * (i.e. their {@link Game#getStatus()} is {@link GameStatus#WAITING_FOR_PLAYERS}
     */
    List<Game> getAllGames();

    int getGamesCount();

    class DuplicateIdException extends Exception {
        private final String id;

        public DuplicateIdException(String id) {
            this.id = id;
        }


        @Override
        public String getMessage() {
            return "Attempted to create a new game with existing game id " + id + ".";
        }
    }
}
