package cinemax.eccezioni;

/**
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */

public class DataNonValidaException extends RuntimeException {
    public DataNonValidaException(String message) {
        super(message);
    }
}
