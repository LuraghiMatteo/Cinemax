package cinemax.eccezioni;

public class DataNonValidaException extends RuntimeException {
    public DataNonValidaException(String message) {
        super(message);
    }
}
