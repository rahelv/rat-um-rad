package ch.progradler.rat_um_rad;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A simple HelloWorld class.
 */
public class HelloWorld {
    public static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        System.out.println("Hello World");
        LOGGER.error("printed by default");
        LOGGER.debug("written to general log file");
        LOGGER.log(Level.forName("PINGPONG", 700),"written only to pingpong file");
    }

}
