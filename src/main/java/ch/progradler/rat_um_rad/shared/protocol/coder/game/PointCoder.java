package ch.progradler.rat_um_rad.shared.protocol.coder.game;

import ch.progradler.rat_um_rad.shared.models.Point;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper;

import java.util.List;

public class PointCoder implements Coder<Point> {
    @Override
    public String encode(Point point, int level) {
        return CoderHelper.encodeFields(level,
                String.valueOf(point.getX()),
                String.valueOf(point.getY()));
    }

    @Override
    public Point decode(String encoded, int level) {
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        Double x = Double.parseDouble(fields.get(0));
        Double y = Double.parseDouble(fields.get(1));
        return new Point(x, y);
    }
}