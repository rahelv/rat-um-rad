package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.game.BuildRoadInfo;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

import java.util.List;

/**
 * Coder for {@link BuildRoadInfo}
 */
public class BuildRoadInfoCoder implements Coder<BuildRoadInfo> {
    @Override
    public String encode(BuildRoadInfo info, int level) {
        return CoderHelper.encodeFields(level, info.getRoadId(), info.getColor().name());
    }

    @Override
    public BuildRoadInfo decode(String encoded, int level) {
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        return new BuildRoadInfo(fields.get(0), WheelColor.valueOf(fields.get(1)));
    }
}
