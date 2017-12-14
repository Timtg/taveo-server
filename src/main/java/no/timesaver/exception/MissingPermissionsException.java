package no.timesaver.exception;


public class MissingPermissionsException extends RuntimeException{

    public MissingPermissionsException(String message) {
        super(message);
    }

    public MissingPermissionsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingPermissionsException(Throwable cause) {
        super(cause);
    }
}
