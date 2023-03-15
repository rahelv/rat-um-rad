package ch.progradler.rat_um_rad.shared;

import java.io.Serializable;

public class Message implements Serializable { //TODO: remove Serializable and write own serialization method
    private String username; //TODO: implement username
    private String message;

    public Message(String message, String username) {
        this.message = message;
        this.username = username;
    }

    public String getMessageAndUsername() { //gets the message String how it should be written
        return username + ": " + message;
    }

    public String getUsername() {
        return username;
    }

    //TODO: test if there's problems with the usage of getMessage, when writing new methods.

    /**
     *
     * @return String encoded in our network protocol
     */
    public String encode() {
        return "";
    }
}

