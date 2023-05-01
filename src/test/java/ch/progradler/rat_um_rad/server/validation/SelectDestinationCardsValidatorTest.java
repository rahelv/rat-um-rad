package ch.progradler.rat_um_rad.server.validation;

import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelectDestinationCardsValidatorTest {

    private SelectDestinationCardsValidator validator = new SelectDestinationCardsValidator();

    @Test
    void returnsFalseIfEmptyCards() {
        boolean valid = validator.validate(null, new ArrayList<>());
        assertFalse(valid);
    }

    @Test
    void returnsFalseIfMoreThan3() {
        boolean valid = validator.validate(null, Arrays.asList("a", "b", "c", "d"));
        assertFalse(valid);
    }

    @Test
    void returnsFalseIfASelectedCardIdIsNotThoseToChooseFrom() {
        DestinationCard card1 = mock(DestinationCard.class);
        DestinationCard card2 = mock(DestinationCard.class);
        DestinationCard card3 = mock(DestinationCard.class);
        when(card1.getCardID()).thenReturn("id1");
        when(card2.getCardID()).thenReturn("id2");
        when(card3.getCardID()).thenReturn("id3");
        List<DestinationCard> toChooseFrom = Arrays.asList(
                card1, card2, card3
        );
        Player player = mock(Player.class);
        when(player.getShortDestinationCardsToChooseFrom()).thenReturn(toChooseFrom);
        boolean valid = validator.validate(player, Arrays.asList("id2", "other"));
        assertFalse(valid);
    }

    @Test
    void returnsTrueIfAllSelectedCardIdIsAreInToChooseFrom() {
        DestinationCard card1 = mock(DestinationCard.class);
        DestinationCard card2 = mock(DestinationCard.class);
        DestinationCard card3 = mock(DestinationCard.class);
        when(card1.getCardID()).thenReturn("id1");
        when(card2.getCardID()).thenReturn("id2");
        when(card3.getCardID()).thenReturn("id3");
        List<DestinationCard> toChooseFrom = Arrays.asList(
                card1, card2, card3
        );
        Player player = mock(Player.class);
        when(player.getShortDestinationCardsToChooseFrom()).thenReturn(toChooseFrom);
        boolean valid = validator.validate(player, Arrays.asList("id2", "id1"));
        assertTrue(valid);
    }
}