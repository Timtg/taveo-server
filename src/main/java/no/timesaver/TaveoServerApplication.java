package no.timesaver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class TaveoServerApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Oslo"));
		System.out.println("Server running with timeZone: " + TimeZone.getDefault().getDisplayName());
		SpringApplication.run(TaveoServerApplication.class, args);
	}
}
