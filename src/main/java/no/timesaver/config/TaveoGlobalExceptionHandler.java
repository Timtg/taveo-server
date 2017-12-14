package no.timesaver.config;

import no.timesaver.exception.HttpResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class TaveoGlobalExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(TaveoGlobalExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(value = {IllegalArgumentException.class})
    @ResponseBody
    public HttpResponseWrapper handleIllegalArgumentException(HttpServletRequest req, Exception e) {
        log.error("GlobalExceptionHandler caught : ",e);
        return new HttpResponseWrapper(e.getMessage(),e.getCause() == null ? "" : e.getCause().toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(value = {IllegalStateException.class})
    @ResponseBody
    public HttpResponseWrapper handleIllegalStateException(HttpServletRequest req, Exception e) {
        log.error("GlobalExceptionHandler caught : ",e);
        return new HttpResponseWrapper(e.getMessage(),e.getCause() == null ? "" : e.getCause().toString());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)  // 403
    @ExceptionHandler(value = {SecurityException.class})
    @ResponseBody
    public HttpResponseWrapper handleSecurityException(HttpServletRequest req, Exception e) {
        log.error("GlobalExceptionHandler caught : ",e);
        return new HttpResponseWrapper(e.getMessage(),e.getCause() == null ? "" : e.getCause().toString());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseBody
    public HttpResponseWrapper handleRuntimeException(HttpServletRequest req, Exception e) {
        log.error("GlobalExceptionHandler caught : ",e);
        return new HttpResponseWrapper(e.getMessage(),e.getCause() == null ? "" : e.getCause().toString());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(value = {ServletException.class})
    @ResponseBody
    public HttpResponseWrapper handleServletException(HttpServletRequest req, Exception e) {
        log.error("GlobalExceptionHandler caught : ",e);
        return new HttpResponseWrapper(e.getMessage(),e.getCause() == null ? "" : e.getCause().toString());
    }

}
