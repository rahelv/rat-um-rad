package ch.progradler.rat_um_rad.shared.database;

import ch.progradler.rat_um_rad.shared.models.Highscore;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper;

import java.util.List;

/**
 * Database for {@link List<Highscore>>}.
 */
public class HighscoresDatabase extends Database<List<Highscore>> {
    private final Coder<Highscore> coder;

    public HighscoresDatabase(LocalStorage storage, Coder<Highscore> coder) {
        super(storage);
        this.coder = coder;
    }

    @Override
    protected Coder<List<Highscore>> getCoder() {
        return new Coder<>() {
            @Override
            public String encode(List<Highscore> highscores, int level) {
                return CoderHelper.encodeList(coder, highscores, level + 1);
            }

            @Override
            public List<Highscore> decode(String encoded, int level) {
                return CoderHelper.decodeList(coder, encoded, level + 1);
            }
        };
    }
}
