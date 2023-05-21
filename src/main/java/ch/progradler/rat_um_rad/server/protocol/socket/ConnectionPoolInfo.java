package ch.progradler.rat_um_rad.server.protocol.socket;

/**
 * This is an interface which gives controlled access to information stored in the ConnectionPool.
 */
public interface ConnectionPoolInfo {
    public String getIpOfThread(Thread thread);
}
