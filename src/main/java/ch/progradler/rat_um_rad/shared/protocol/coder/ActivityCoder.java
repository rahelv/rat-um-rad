package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;

import java.util.List;

public class ActivityCoder implements Coder<Activity> {
    @Override
    public String encode(Activity activity, int level) {
        return CoderHelper.encodeFields(level, activity.getUsername(), activity.getCommand().name());
    }

    @Override
    public Activity decode(String encoded, int level) {
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        return new Activity(fields.get(0), ServerCommand.valueOf(fields.get(1)));
    }
}
