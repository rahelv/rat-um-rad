package ch.progradler.rat_um_rad.server.repositories;

import java.util.HashMap;
import java.util.Map;

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
