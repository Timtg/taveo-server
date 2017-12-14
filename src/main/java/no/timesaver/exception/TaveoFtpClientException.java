package no.timesaver.exception;


public class TaveoFtpClientException extends RuntimeException{

    public TaveoFtpClientException(String message) {
        super(message);
    }

    public TaveoFtpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaveoFtpClientException(Throwable cause) {
        super(cause);
    }
}
