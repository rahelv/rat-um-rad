package ch.progradler.rat_um_rad.shared.protocol.coder.player;

import ch.progradler.rat_um_rad.shared.models.game.PlayerBase;
import ch.progradler.rat_um_rad.shared.models.game.PlayerColor;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper;

import java.util.List;

public class PlayerBaseCoder implements Coder<PlayerBase> {
    @Override
    public String encode(PlayerBase player, int level) {
        if (player == null) {
            return "null";
        }
        return CoderHelper.encodeFields(level,
                player.getName(),
                player.getColor().toString(),
                String.valueOf(player.getScore()),
                String.valueOf(player.getWheelsRemaining()),
                String.valueOf(player.getPlayingOrder()));
    }

    @Override
    public PlayerBase decode(String encoded, int level) {
        if (encoded.equals("null")) {
            return null;
        }
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        String name = fields.get(0);
        PlayerColor color = PlayerColor.valueOf(fields.get(1));
        int score = Integer.parseInt(fields.get(2));
        int wheelsRemaining = Integer.parseInt(fields.get(3));
        int playingOrder = Integer.parseInt(fields.get(4));
        return new PlayerBase(name, color, score, wheelsRemaining, playingOrder);
    }
}