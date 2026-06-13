package cinemax.eccezioni;

public class CostoNonValidoException extends RuntimeException {
    public CostoNonValidoException(String message) {
        super(message);
    }
}
