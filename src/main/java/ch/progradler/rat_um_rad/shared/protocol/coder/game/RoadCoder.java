package ch.progradler.rat_um_rad.shared.protocol.coder.game;

import ch.progradler.rat_um_rad.shared.models.game.Road;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper;

import java.util.List;

public class RoadCoder implements Coder<Road> {
    @Override
    public String encode(Road road, int level) {
        if (road == null) {
            return "null";
        }
        return CoderHelper.encodeFields(level,
                road.getId(),
                road.getFromCityId(),
                road.getToCityId(),
                String.valueOf(road.getRequiredWheels()),
                road.getColor().name());
    }

    @Override
    public Road decode(String encoded, int level) {
        if (encoded.equals("null")) {
            return null;
        }
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        String id = fields.get(0);
        String fromCityId = fields.get(1);
        String toCityId = fields.get(2);
        int requiredWheels = Integer.parseInt(fields.get(3));
        WheelColor color = WheelColor.valueOf(fields.get(4));
        return new Road(fromCityId, toCityId, requiredWheels, color);
    }
}