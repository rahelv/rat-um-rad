package ch.progradler.rat_um_rad.shared.protocol.coder.player;

import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.PlayerColor;
import ch.progradler.rat_um_rad.shared.models.game.PlayerEndResult;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;

import java.util.List;

import static ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper.*;

public class PlayerCoder implements Coder<Player> {
    private final Coder<WheelCard> wheelCardCoder;
    private final Coder<DestinationCard> destinationCardCoder;
    private final Coder<PlayerEndResult> playerEndResultCoder;

    public PlayerCoder(Coder<WheelCard> wheelCardCoder, Coder<DestinationCard> destinationCardCoder, Coder<PlayerEndResult> playerEndResultCoder) {
        this.wheelCardCoder = wheelCardCoder;
        this.destinationCardCoder = destinationCardCoder;
        this.playerEndResultCoder = playerEndResultCoder;
    }

    @Override
    public String encode(Player player, int level) {
        return encodeNullableField(player, (p) -> encodeNonNull(p, level));
    }

    private String encodeNonNull(Player player, int level) {
        String wheelCardsListEncoded = encodeList(wheelCardCoder, player.getWheelCards(), level + 1);
        String longDestinationCardEncoded = destinationCardCoder.encode(player.getLongDestinationCard(), level + 1);
        String shortDestinationCardsListEncoded = encodeList(destinationCardCoder, player.getShortDestinationCards(), level + 1);
        String shortDestinationCardsChooseFromListEncoded = encodeList(destinationCardCoder, player.getShortDestinationCardsToChooseFrom(), level + 1);

        return encodeFields(level,
                player.getName(),
                player.getColor().toString(),
                String.valueOf(player.getScore()),
                String.valueOf(player.getWheelsRemaining()),
                String.valueOf(player.getPlayingOrder()),
                wheelCardsListEncoded,
                longDestinationCardEncoded,
                shortDestinationCardsListEncoded,
                shortDestinationCardsChooseFromListEncoded,
                playerEndResultCoder.encode(player.getEndResult(), level + 1));
    }

    @Override
    public Player decode(String encoded, int level) {
        return decodeNullableField(encoded, (s) -> decodeNonNull(s, level));
    }

    private Player decodeNonNull(String encoded, int level) {
        List<String> fields = decodeFields(level, encoded);
        String name = fields.get(0);
        PlayerColor color = PlayerColor.valueOf(fields.get(1));
        int score = Integer.parseInt(fields.get(2));
        int wheelsRemaining = Integer.parseInt(fields.get(3));
        int playingOrder = Integer.parseInt(fields.get(4));

        List<WheelCard> wheelCardsList = decodeList(wheelCardCoder, fields.get(5), level + 1);
        DestinationCard longDestinationCard = destinationCardCoder.decode(fields.get(6), level + 1);

        List<DestinationCard> shortDestinationCardsList = decodeList(destinationCardCoder, fields.get(7), level + 1);
        List<DestinationCard> shortDestinationCardsChooseFrom = decodeList(destinationCardCoder, fields.get(8), level + 1);

        PlayerEndResult endResult = playerEndResultCoder.decode(fields.get(9), level + 1);

        return new Player(name, color, score, wheelsRemaining, playingOrder, wheelCardsList, longDestinationCard, shortDestinationCardsList, shortDestinationCardsChooseFrom, endResult);
    }
}