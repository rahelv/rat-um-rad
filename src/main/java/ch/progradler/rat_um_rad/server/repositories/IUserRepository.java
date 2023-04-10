package ch.progradler.rat_um_rad.server.repositories;

public interface IUserRepository {
    void addUsername(String username, String ipAddress);

    String getUsername(String ipAddress);

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
