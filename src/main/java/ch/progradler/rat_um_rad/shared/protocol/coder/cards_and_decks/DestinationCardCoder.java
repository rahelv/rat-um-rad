package ch.progradler.rat_um_rad.shared.protocol.coder.cards_and_decks;

import ch.progradler.rat_um_rad.shared.models.game.City;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper;
import ch.progradler.rat_um_rad.shared.protocol.coder.game.CityCoder;

import java.util.List;

public class DestinationCardCoder implements Coder<DestinationCard> {
    private final CityCoder cityCoder; //TODO: decode null

    public DestinationCardCoder(CityCoder cityCoder) {
        this.cityCoder = cityCoder;
    }
    @Override
    public String encode(DestinationCard card, int level) {
        if(card == null) {
            return "null";
        }
        String destination1Encoded = cityCoder.encode(card.getDestination1(), level + 1);
        String destination2Encoded = cityCoder.encode(card.getDestination2(), level + 1);

        return CoderHelper.encodeFields(level,
                card.getCardID(),
                destination1Encoded,
                destination2Encoded,
                String.valueOf(card.getPoints()));
    }

    @Override
    public DestinationCard decode(String encoded, int level) {
        if(encoded.equals("null")) {
            return null;
        }
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        String cardID = fields.get(0);
        City destination1 = cityCoder.decode(fields.get(1), level + 1);
        City destination2 = cityCoder.decode(fields.get(2), level + 1);
        int points = Integer.parseInt(fields.get(3));
        return new DestinationCard(cardID, destination1, destination2, points);
    }
}