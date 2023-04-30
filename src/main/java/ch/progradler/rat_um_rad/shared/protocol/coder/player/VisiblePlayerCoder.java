package ch.progradler.rat_um_rad.shared.protocol.coder.player;

import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.PlayerEndResult;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper;

import java.util.List;

public class VisiblePlayerCoder implements Coder<VisiblePlayer> {
    private final Coder<PlayerEndResult> playerEndResultCoder;

    public VisiblePlayerCoder(Coder<PlayerEndResult> playerEndResultCoder) {
        this.playerEndResultCoder = playerEndResultCoder;
    }

    @Override
    public String encode(VisiblePlayer player, int level) {
        if (player == null) {
            return "null";
        }
        return CoderHelper.encodeFields(level,
                player.getName(),
                player.getColor().toString(),
                String.valueOf(player.getScore()),
                String.valueOf(player.getWheelsRemaining()),
                String.valueOf(player.getPlayingOrder()),
                player.getIpAddress(),
                String.valueOf(player.getWheelCardsCount()),
                String.valueOf(player.getShortDestinationCardsCount()),
                playerEndResultCoder.encode(player.getEndResult(), level + 1)
        );
    }

    @Override
    public VisiblePlayer decode(String encoded, int level) {
        if (encoded.equals("null")) {
            return null;
        }
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        String name = fields.get(0);
        WheelColor color = WheelColor.valueOf(fields.get(1));
        int score = Integer.parseInt(fields.get(2));
        int wheelsRemaining = Integer.parseInt(fields.get(3));
        int playingOrder = Integer.parseInt(fields.get(4));
        String ipAddress = fields.get(5);
        int wheelCardsCount = Integer.parseInt(fields.get(6));
        int shortDestinationCardsCount = Integer.parseInt(fields.get(7));
        PlayerEndResult endResult = playerEndResultCoder.decode(fields.get(8), level + 1);
        return new VisiblePlayer(name, color, score, wheelsRemaining, playingOrder, ipAddress, wheelCardsCount, shortDestinationCardsCount, endResult);
    }
}