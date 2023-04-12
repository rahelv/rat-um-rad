package ch.progradler.rat_um_rad.start;

/**
 * Model class containing parsed application-starting arguments.
 */
public class MainArgs {
    private final boolean startServer;
    private final int port;
    private final String host;
    private final String username;

    public MainArgs(boolean startServer, int port) {
        this.startServer = startServer;
        this.port = port;
        this.host = null;
        this.username = null;
    }

    public MainArgs(boolean startServer, int port, String host, String username) {
        this.startServer = startServer;
        this.port = port;
        this.host = host;
        this.username = username;
    }

    public boolean isStartServer() {
        return startServer;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }
}
