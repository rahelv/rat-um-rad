package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.Highscore;

import java.util.List;

/**
 * Coder for {@link Highscore}
 */
public class HighscoreCoder implements Coder<Highscore> {
    @Override
    public String encode(Highscore highscore, int level) {
        return CoderHelper.encodeFields(level,
                highscore.getUsername(),
                String.valueOf(highscore.getScore()),
                CoderHelper.encodeDate(highscore.getDate()));
    }

    @Override
    public Highscore decode(String encoded, int level) {
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        return new Highscore(
                fields.get(0),
                Integer.parseInt(fields.get(1)),
                CoderHelper.decodeDate(fields.get(2))
        );
    }
}
