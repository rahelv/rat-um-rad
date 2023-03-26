package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.UsernameChange;

import static ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper.SEPARATOR;

/**
 * En-/decodes {@link UsernameChange}
 */
public class UsernameChangeCoder implements Coder<UsernameChange> {

    /** receives a UsernameChange and encodes it to the defined serialized format.
     * @param usernameChange
     * @return
     */
    @Override
    public String encode(UsernameChange usernameChange) {
        return "{" +
                usernameChange.getOldName() + SEPARATOR +
                usernameChange.getNewName() +
                "}";
    }

    /** receives an encoded String and decodes it to class UsernameChange
     * @param encoded
     * @return
     */
    @Override
    public UsernameChange decode(String encoded) {
        String unwrapped = encoded.substring(1, encoded.length() - 1);
        String[] fields = unwrapped.split(SEPARATOR);
        String oldName = fields[0];
        String newName = fields[1];
        return new UsernameChange(oldName, newName);
    }
}
