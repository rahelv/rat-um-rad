package ch.progradler.rat_um_rad.shared.protocol.coder;

/**
 * @param <T> Type of object to be en- or decoded.
 */
public interface Coder<T> {
    String encode(T object, int level);

    T decode(String encoded, int level);
}
