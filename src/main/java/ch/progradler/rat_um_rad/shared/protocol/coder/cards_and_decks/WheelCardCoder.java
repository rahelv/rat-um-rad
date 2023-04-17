package ch.progradler.rat_um_rad.shared.protocol.coder.cards_and_decks;

import ch.progradler.rat_um_rad.shared.models.game.City;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper;
import ch.progradler.rat_um_rad.shared.protocol.coder.game.CityCoder;

import java.util.List;

public class WheelCardCoder implements Coder<WheelCard> {
    @Override
    public String encode(WheelCard card, int level) {
        if(card == null) {
            return "null";
        }
        return CoderHelper.encodeFields(level,
                String.valueOf(card.getCardID()));
    }

    @Override
    public WheelCard decode(String encoded, int level) {
        if(encoded.equals("null")) {
            return null;
        }
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        int cardID = Integer.parseInt(fields.get(0));
        return new WheelCard(cardID);
    }
}