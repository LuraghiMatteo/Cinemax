package eccezioni;

public class CostoNonValidoExeption extends RuntimeException {
    public CostoNonValidoExeption(String message) {
        super(message);
    }
}
