package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.UsernameChange;

import java.util.List;

/**
 * En-/decodes {@link UsernameChange}
 */
public class UsernameChangeCoder implements Coder<UsernameChange> {

    /**
     * receives a UsernameChange and encodes it to the defined serialized format.
     *
     * @param usernameChange
     * @param level
     * @return
     */
    @Override
    public String encode(UsernameChange usernameChange, int level) {
        return CoderHelper.encodeFields(level, usernameChange.getOldName(),
                usernameChange.getNewName());
    }

    /**
     * receives an encoded String and decodes it to class UsernameChange
     *
     * @param encoded
     * @param level
     * @return
     */
    @Override
    public UsernameChange decode(String encoded, int level) {
        List<String> fields = CoderHelper.decodeFields(level, encoded);
        String oldName = fields.get(0);
        String newName = fields.get(1);
        return new UsernameChange(oldName, newName);
    }
}
