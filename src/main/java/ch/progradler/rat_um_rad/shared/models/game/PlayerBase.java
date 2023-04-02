package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.client.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;

import java.util.List;

/**
 * Since the players kept by the server and the own player kept by each client {@link Player} is different from the
 * other players kept by the client {@link VisiblePlayer}, both extend this class.
 */
public class PlayerBase {
    public static final int STARTING_WHEELS_PER_PLAYER = 35;
    private String name;
    private int color;
    private int score;
    private int wheelsRemaining;
    private List<WheelCard> wheelCards;
    private DestinationCard longDestinationCard;
    private List<DestinationCard> shortDestinationCards;
}
