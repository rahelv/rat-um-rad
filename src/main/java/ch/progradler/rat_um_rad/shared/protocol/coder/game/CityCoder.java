package ch.progradler.rat_um_rad.shared.protocol.coder.game;

import ch.progradler.rat_um_rad.shared.models.Point;
import ch.progradler.rat_um_rad.shared.models.game.City;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper;

import java.util.List;

public class CityCoder implements Coder<City> {
    private final PointCoder pointCoder;

    public CityCoder(PointCoder pointCoder) {
        this.pointCoder = pointCoder;
    }

    @Override
    public String encode(City city, int level) {
        if (city == null) {
            return "null";
        }
        String pointEncoded = pointCoder.encode(city.getPoint(), level + 1);
        return CoderHelper.encodeFields(level,
                city.getId(),
                city.getName(),
                pointEncoded);
    }

    @Override
    public City decode(String encoded, int level) {
        if (encoded.equals("null")) {
            return null;
        }
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        String id = fields.get(0);
        String name = fields.get(1);
        Point point = pointCoder.decode(fields.get(2), level + 1); //TODO: check if right level
        return new City(id, name, point);
    }
}