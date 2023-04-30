package ch.progradler.rat_um_rad.shared.protocol.coder.player;

import ch.progradler.rat_um_rad.shared.models.game.PlayerEndResult;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;

import java.util.List;

import static ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper.*;

/**
 * Can de- anc encode {@link PlayerEndResult}
 */
public class PlayerEndResultCoder implements Coder<PlayerEndResult> {
    private final Coder<DestinationCard> destinationCardCoder;

    public PlayerEndResultCoder(Coder<DestinationCard> destinationCardCoder) {
        this.destinationCardCoder = destinationCardCoder;
    }

    @Override
    public String encode(PlayerEndResult endResult, int level) {
        return encodeNullableField(endResult, (p) -> encodeNonNull(p, level));
    }

    private String encodeNonNull(PlayerEndResult endResult, int level) {
        String achievedEncoded = encodeList(destinationCardCoder, endResult.getAchievedShorts(), level + 1);
        String notAchievedEncoded = encodeList(destinationCardCoder, endResult.getNotAchievedShorts(), level + 1);
        return encodeFields(level, achievedEncoded, notAchievedEncoded, String.valueOf(endResult.hasAchievedLong()));
    }

    @Override
    public PlayerEndResult decode(String encoded, int level) {
        return decodeNullableField(encoded, (s) -> decodeNonNull(s, level));
    }

    private PlayerEndResult decodeNonNull(String encoded, int level) {
        List<String> fields = decodeFields(level, encoded);
        List<DestinationCard> achievedShorts = decodeList(destinationCardCoder, fields.get(0), level + 1);
        List<DestinationCard> notAchievedShorts = decodeList(destinationCardCoder, fields.get(1), level + 1);
        boolean achievedLong = Boolean.parseBoolean(fields.get(2));
        return new PlayerEndResult(achievedShorts, notAchievedShorts, achievedLong);
    }
}
