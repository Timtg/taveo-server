package no.timesaver.exception;


public class MailTemplateGenerationException extends RuntimeException{

    public MailTemplateGenerationException(String message) {
        super(message);
    }

    public MailTemplateGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailTemplateGenerationException(Throwable cause) {
        super(cause);
    }
}
