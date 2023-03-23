package ch.progradler.rat_um_rad.server.repositories;

public interface IUserRepository {
    void addUsername(String username, String ipAddress);

    String getUsername(String ipAddress);

    void updateUsername(String username, String ipAddress);

    String removeUsername(String ipAddress);

    int getUserCount();
}
