package cinemax.eccezioni;

/**
 * @author Fabio Maffiolini - Matr: 765567 - Sede: VA
 */

public class CostoNonValidoException extends RuntimeException {
    public CostoNonValidoException(String message) {
        super(message);
    }
}
