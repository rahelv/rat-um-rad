package ch.progradler.rat_um_rad.server.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {
    private static final String NAME_1 = "John";
    private static final String IP_ADDRESS_1 = "clientJ";
    private static final String NAME_2 = "Albert";
    private static final String IP_ADDRESS_2 = "clientA";
    private UserRepository userRepository;

    @BeforeEach
    public void initUserRepository() {
        userRepository = new UserRepository();
        userRepository.addUsername(NAME_1, IP_ADDRESS_1);
        userRepository.addUsername(NAME_2, IP_ADDRESS_2);
    }

    @Test
    void isEmptyAfterConstruction() {
        userRepository = new UserRepository();
        assertEquals(0, userRepository.getUserCount());
    }

    @Test
    void hasCorrectCountAfterAddingAndRemoving() {
        assertEquals(2, userRepository.getUserCount());
        userRepository.addUsername("user3", "client3");
        assertEquals(3, userRepository.getUserCount());
        userRepository.getUsername("client3"); // does nothing
        assertEquals(3, userRepository.getUserCount());
        userRepository.removeUsername("client3");
        assertEquals(2, userRepository.getUserCount());
        userRepository.updateUsername("new user name 2", IP_ADDRESS_2);  // does nothing
        assertEquals(2, userRepository.getUserCount());
    }

    @Test
    void getAllUsernamesTest() {
        List<String> expected = Arrays.asList(NAME_1, NAME_2);
        List<String> result = userRepository.getAllUsernames();

        Collections.sort(result);
        Collections.sort(expected);

        assertEquals(expected, result);
    }

    @Test
    void addUsername() {
        String username = "user3";
        String ipAddress = "client3";
        assertNull(userRepository.getUsername(ipAddress));
        userRepository.addUsername(username, ipAddress);
        assertEquals(username, userRepository.getUsername(ipAddress));
    }

    @Test
    void updateUsername() {
        String newUsername = "newUsername 2";
        assertEquals(NAME_2, userRepository.getUsername(IP_ADDRESS_2));
        userRepository.updateUsername(newUsername, IP_ADDRESS_2);
        assertEquals(newUsername, userRepository.getUsername(IP_ADDRESS_2));
    }

    @Test
    void getIpAddress() {
        assertEquals(IP_ADDRESS_2, userRepository.getIpAddress(NAME_2));
    }

    @Test
    void removeUsername() {
        assertEquals(NAME_1, userRepository.getUsername(IP_ADDRESS_1));
        userRepository.removeUsername(IP_ADDRESS_1);
        assertNull(userRepository.getUsername(IP_ADDRESS_1));
    }

    @Test
    void hasDuplicates() {
        String username = "Albert";
        userRepository.addUsername(username, "client1");
        assertTrue(userRepository.hasDuplicate(username));
    }
}
