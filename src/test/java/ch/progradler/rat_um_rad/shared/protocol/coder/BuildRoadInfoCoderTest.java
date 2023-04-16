package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.game.BuildRoadInfo;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildRoadInfoCoderTest {

    private BuildRoadInfoCoder coder;

    @BeforeEach
    void setUp() {
        coder = new BuildRoadInfoCoder();
    }

    @Test
    void encode() {
        int level = 2;
        String roadId = "road1";
        WheelColor color = WheelColor.WHITE;

        BuildRoadInfo info = new BuildRoadInfo(roadId, color);
        String encoded = coder.encode(info, level);
        assertEquals(CoderHelper.encodeFields(level, roadId, color.name()), encoded);
    }

    @Test
    void decode() {
        int level = 2;
        String roadId = "road1";
        WheelColor color = WheelColor.WHITE;

        String encoded = CoderHelper.encodeFields(level, roadId, color.name());
        BuildRoadInfo decoded = coder.decode(encoded, level);
        assertEquals(new BuildRoadInfo(roadId, color), decoded);
    }
}