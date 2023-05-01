package ch.progradler.rat_um_rad.shared.database;

import java.io.IOException;

/**
 * Interface which allows saving String content to a locally.
 */
public interface LocalStorage {

    void save(String content, String filename) throws IOException;

    String read(String filename) throws IOException;
}
