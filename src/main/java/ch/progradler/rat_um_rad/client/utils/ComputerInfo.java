package ch.progradler.rat_um_rad.client.utils;

public class ComputerInfo {

    public ComputerInfo() {
    }

    /**
     * returns the System username
     *
     * @return String: system username
     */
    public String getSystemUsername() {
        try {
            return System.getProperty("user.name"); //tested on fedora and windows TODO: test on mac
        } catch (Exception e) {
            e.printStackTrace();
            return ""; //TODO: improve
        }
    }
}
