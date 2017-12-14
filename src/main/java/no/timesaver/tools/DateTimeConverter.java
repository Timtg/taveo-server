package no.timesaver.tools;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class DateTimeConverter {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public LocalDateTime toLocalDateTime(String in)  throws  DateTimeParseException {
        return LocalDateTime.parse(in,formatter);
    }
}
