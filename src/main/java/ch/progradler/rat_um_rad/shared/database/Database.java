package ch.progradler.rat_um_rad.shared.database;

import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;

import java.io.IOException;

/**
 * Abstraction for saving object of type <code>T</code> to local storage.
 */
public abstract class Database<T> {
    private final LocalStorage storage;

    public Database(LocalStorage storage) {
        this.storage = storage;
    }

    protected abstract Coder<T> getCoder();

    public void save(T content, String key) throws IOException {
        storage.save(getCoder().encode(content, 0), key);
    }

    public T read(String key) throws IOException {
        return getCoder().decode(storage.read(key), 0);
    }
}
