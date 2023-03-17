package ch.progradler.rat_um_rad.client.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ComputerInfo {

    InetAddress userAddress;

    public ComputerInfo(String host) {
        try {
            userAddress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHostName() {
        return userAddress.getHostName();
    }
}
