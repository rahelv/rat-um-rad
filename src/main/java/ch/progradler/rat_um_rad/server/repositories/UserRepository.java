package ch.progradler.rat_um_rad.server.repositories;

import java.util.*;

/**
 * Implementation of {@link IUserRepository}
 */
public class UserRepository implements IUserRepository {

    /**
     * Keys are ipAddresses
     */
    private final Map<String, String> names = new HashMap<>();

    @Override
    public void addUsername(String username, String ipAddress) {
        names.put(ipAddress, username);
    }

    @Override
    public String getUsername(String ipAddress) {
        return names.get(ipAddress);
    }

    @Override
    public List<String> getAllUsernames() {
        List<String> usernames = new LinkedList<String>();
        usernames.addAll(names.values());
        return usernames;
    }

    @Override
    public String getIpAddress(String username) {
        for (Map.Entry<String, String> entry : names.entrySet()) {
            if (entry.getValue().equals(username)) return entry.getKey();
        }
        return null;
    }

    @Override
    public void updateUsername(String username, String ipAddress) {
        names.put(ipAddress, username);
    }

    @Override
    public String removeUsername(String ipAddress) {
        return names.remove(ipAddress);
    }

    @Override
    public int getUserCount() {
        return names.size();
    }

    @Override
    public boolean hasDuplicate(String username) {
        return names.containsValue(username);
    }
}
