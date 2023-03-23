package ch.progradler.rat_um_rad.client.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ComputerInfo {

    public ComputerInfo() {}

    public String getSystemUsername() {
        try {
            return System.getProperty("user.name"); //tested on fedora and windows TODO: test on mac
        } catch (Exception e) {
            e.printStackTrace();
            return ""; //TODO: improve
        }
    }
}
