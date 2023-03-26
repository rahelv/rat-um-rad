package ch.progradler.rat_um_rad.shared.util;

import ch.progradler.rat_um_rad.client.command_line.UsernameHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidator {
    private final String usernameRegex = "^[A-Za-z][A-Za-z0-9_]{4,29}$";
    /**
     * Method to validate a given username.
     * regex rules: [] first character has to be a letter, [] all other allowed chars (letters, digits and _), {} length constraint min. 5 characters, max. 30
     * @param username: Username to be validated
     * @return boolean: returns true if username is valid.
     */
    public boolean isUsernameValid(String username) {
        Pattern pattern = Pattern.compile(usernameRegex);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
