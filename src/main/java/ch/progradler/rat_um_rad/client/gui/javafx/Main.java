package ch.progradler.rat_um_rad.client.gui.javafx;

import ch.progradler.rat_um_rad.client.Client;
import javafx.application.Application;

public class Main {

    /**
     * This is simply a wrapper to launch the {@link GUI} class.
     * The reason this class exists is documented in {@link GUI#main(String[])}
     * */
    public static void main(String[] args) {
        Application.launch(GUI.class, args);
    }
}
