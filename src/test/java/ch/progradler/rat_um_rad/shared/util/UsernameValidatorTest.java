package ch.progradler.rat_um_rad.shared.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class UsernameValidatorTest {

    private UsernameValidator usernameValidator;

    @BeforeEach
    void setUp() {
        this.usernameValidator = new UsernameValidator();
    }


    @Test
    void isUsernameValidReturnsFalseWhenDigitAtBeginning() {
        String wrongUsername = "6username";

        assertFalse(usernameValidator.isUsernameValid(wrongUsername));
    }

    @Test
    void isUsernameValidReturnsFalseWhenSpecialCharUsed() {
        String wrongUsername = "u$ername";

        assertFalse(usernameValidator.isUsernameValid(wrongUsername));
    }

    @Test
    void isUsernameValidReturnsTrueWhenUnderScoreUsed() {
        String username = "user_name";

        assertTrue(usernameValidator.isUsernameValid(username));
    }

    @Test
    void isUsernameValidReturnsFalseWhenLessThan3Chars() {
        String wrongUsername = "us";

        assertFalse(usernameValidator.isUsernameValid(wrongUsername));
    }

    @Test
    void isUsernameValidReturnsFalseWhenMoreThan30Chars() {
        String wrongUsername = "usernamehatmehrals30zeichenlala";

        assertFalse(usernameValidator.isUsernameValid(wrongUsername));
    }
}