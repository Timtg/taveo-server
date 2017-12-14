package no.timesaver.exception;

public class HttpResponseWrapper {
    private String msg;
    private String cause;

    public HttpResponseWrapper(String msg) {
        this.msg = msg;
    }

    public HttpResponseWrapper(String msg, String cause) {
        this.msg = msg;
        this.cause = cause;
    }

    public String getMsg() {
        return msg;
    }

    public String getCause() {
        return cause;
    }
}
