package ch.progradler.rat_um_rad.shared.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementation of {@link LocalStorage} using Files.
 */
public class FileStorage implements LocalStorage {
    private static final String LOCALE_FILES_STORAGE_FOLDER = "locale_database";

    @Override
    public void save(String content, String filename) throws IOException {
        ensureCreateDatabaseDir();
        filename = LOCALE_FILES_STORAGE_FOLDER + "/" + filename;
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(content);
        writer.close();
    }

    private void ensureCreateDatabaseDir() {
        File DB_Dir = new File(LOCALE_FILES_STORAGE_FOLDER);
        DB_Dir.mkdirs();
    }

    @Override
    public String read(String filename) throws IOException {
        filename = LOCALE_FILES_STORAGE_FOLDER + "/" + filename;
        Path path = Paths.get(filename);
        return Files.readString(path);
    }
}
