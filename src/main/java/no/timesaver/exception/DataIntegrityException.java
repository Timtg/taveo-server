package no.timesaver.exception;


public class DataIntegrityException extends RuntimeException{

    public DataIntegrityException(String message) {
        super(message);
    }

    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataIntegrityException(Throwable cause) {
        super(cause);
    }
}
