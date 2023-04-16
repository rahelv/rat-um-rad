package ch.progradler.rat_um_rad.shared.protocol.coder.player;

import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper;

import java.util.List;

public class PlayerCoder implements Coder<Player> {
    private Coder<WheelCard> wheelCardCoder;
    private Coder<DestinationCard> destinationCardCoder;

     public PlayerCoder(Coder<WheelCard> wheelCardCoder, Coder<DestinationCard> destinationCardCoder) {
         this.wheelCardCoder = wheelCardCoder;
         this.destinationCardCoder = destinationCardCoder;
     }
    @Override
    public String encode(Player player, int level) {
         if(player == null) {
             return "null";
         }
        List<String> wheelCardsList = player.getWheelCards().stream()
                .map((wheelCard) -> wheelCardCoder.encode(wheelCard, level + 2))
                .toList();
        String wheelCardsListEncoded = CoderHelper.encodeStringList(level + 1, wheelCardsList);
        String longDestinationCardEncoded = destinationCardCoder.encode(player.getLongDestinationCard(), level + 1);
        List<String> shortDestinationCardsList = player.getShortDestinationCards().stream()
                .map((destinationCard) -> destinationCardCoder.encode(destinationCard, level + 2))
                .toList();
        String shortDestinationCardsListEncoded = CoderHelper.encodeStringList(level + 1, shortDestinationCardsList);
        return CoderHelper.encodeFields(level,
                player.getName(),
                player.getColor().toString(),
                String.valueOf(player.getScore()),
                String.valueOf(player.getWheelsRemaining()),
                String.valueOf(player.getPlayingOrder()),
                wheelCardsListEncoded,
                longDestinationCardEncoded,
                shortDestinationCardsListEncoded);
    }

    @Override
    public Player decode(String encoded, int level) {
        if(encoded.equals("null")) {
            return null;
        }
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        String name = fields.get(0);
        WheelColor color = WheelColor.valueOf(fields.get(1));
        int score = Integer.parseInt(fields.get(2));
        int wheelsRemaining = Integer.parseInt(fields.get(3));
        int playingOrder = Integer.parseInt(fields.get(4));
        List<String> wheelCardsListStrings = CoderHelper.decodeStringList(level + 1, fields.get(5));
        List<WheelCard> wheelCardsList = wheelCardsListStrings.stream()
                .map((s) -> {
                    if(wheelCardsListStrings.equals("[]")) { //TODO: is this needed ?
                        return null;
                    }
                    return  wheelCardCoder.decode(s, level + 2);
                }).toList();
        DestinationCard longDestinationCard = destinationCardCoder.decode(fields.get(6), level + 1);
        List<String> shortDestinationCardsListStrings = CoderHelper.decodeStringList(level + 1, fields.get(7));
        List<DestinationCard> shortDestinationCardsList = shortDestinationCardsListStrings.stream()
                .map((s) -> destinationCardCoder.decode(s, level + 2)).toList();
        return new Player(name, color, score, wheelsRemaining, playingOrder, wheelCardsList, longDestinationCard, shortDestinationCardsList);
    }
}