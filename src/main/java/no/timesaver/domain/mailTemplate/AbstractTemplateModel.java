package no.timesaver.domain.mailTemplate;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AbstractTemplateModel {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM-yyyy HH:mm:ss");

    private final String now = LocalDateTime.now().format(dateTimeFormatter);

    public String getNow() {
        return now;
    }

    public String getTaveoIconBase64() {
        return IconBase64.taveo;
    }

    public String getTimesaverIconBase64() {
        return IconBase64.timesaver;
    }
}
