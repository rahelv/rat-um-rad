package ch.progradler.rat_um_rad.server.validation;

import ch.progradler.rat_um_rad.shared.protocol.ErrorResponse;

/**
 * Interface which allow validating an object of type:
 *
 * @param <T>
 */
public interface Validator<T> {
    /**
     * @return error message ({@link ErrorResponse} if invalid or null if valid.
     */
    String validate(T item);
}
