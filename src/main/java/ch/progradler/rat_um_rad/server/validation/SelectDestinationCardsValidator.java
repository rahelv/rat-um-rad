package ch.progradler.rat_um_rad.server.validation;

import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;

import java.util.List;

/**
 * Validator for selected destination card ids.
 */
public class SelectDestinationCardsValidator {
    /**
     * @return whether or not they are valid.
     */
    public boolean validate(Player player, List<String> selectedCardIds) {
        if (selectedCardIds.isEmpty()) return false;
        if (selectedCardIds.size() > 3) return false;

        List<String> optionCardIds = player.getShortDestinationCardsToChooseFrom().stream()
                .map(DestinationCard::getCardID).toList();
        for (String selectedCardId : selectedCardIds) {
            if (!optionCardIds.contains(selectedCardId)) {
                return false;
            }
        }
        return true;
    }
}
