package eccezioni;

public class DataNonValidaExeption extends RuntimeException {
    public DataNonValidaExeption(String message) {
        super(message);
    }
}
