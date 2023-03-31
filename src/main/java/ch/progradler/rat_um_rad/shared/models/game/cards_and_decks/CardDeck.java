package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

/**
 * This interface is needed for instances of @link DestinationCardDeck and @link WheelCardDeck. Common
 * operations on decks are mix(), takeFromTop() or putAtBottom(). In order to write just each one such
 * method for all decks used in this game, the input of these methods will be CardDeck
 *
 * These methods will be implemented in a GameService in the Server and not in this interface. The
 * reason why is, because only the server (and not the client) should be able to use them.
 */
public interface CardDeck {
}
