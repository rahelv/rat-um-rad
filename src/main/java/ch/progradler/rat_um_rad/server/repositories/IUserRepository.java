package ch.progradler.rat_um_rad.server.repositories;

import java.util.List;

public interface IUserRepository {
    void addUsername(String username, String ipAddress);

    String getUsername(String ipAddress);

    List<String> getAllUsernames();

    String getIpAddress(String username);

    void updateUsername(String username, String ipAddress);

    String removeUsername(String ipAddress);

    int getUserCount();

    /**
     * @param username to check for if has duplicates.
     * @return whether or not the username is already registered.
     */
    boolean hasDuplicate(String username);
}
