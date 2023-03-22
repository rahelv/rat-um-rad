package ch.progradler.rat_um_rad.client.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ComputerInfo {

    String systemUsername;

    public ComputerInfo(String host) {
        try {
            systemUsername = System.getProperty("user.name"); //tested on fedora and windows TODO: test on mac
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSystemUsername() {
        return systemUsername;
    }
}
