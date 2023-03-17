package ch.progradler.rat_um_rad.shared.protocol;

import java.io.Serializable;

public class Packet implements Serializable { //TODO: remove Serializable and write own serialization method
    private String username; //TODO: implement username
    private String message;

    public Packet(String message, String username) {
        this.message = message;
        this.username = username;
    }

    /**
     * // TODO: A GUI class should do this stuff
     */
    public String getMessageAndUsername() { //gets the message String how it should be written
        return username + ": " + message;
    }

    public String getUsername() {
        return username;
    }

    //TODO: test if there's problems with the usage of getMessage, when writing new methods.

    /**
     * // TODO: as class for de- and encoding should do this stuff
     * @return String encoded in our network protocol
     */
    public String encode() {
        return "";
    }
}

