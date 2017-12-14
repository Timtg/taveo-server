package no.timesaver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;


@Configuration
//@ComponentScan(basePackages = "no.timesaver.spring")
public class MailConfig
{

    @Value("${smtp.host}") String smtpHost;
    @Value("${smtp.port}") int smtpPort;
    @Value("${smtp.un}") String smtpUn;
    @Value("${smtp.pw}") String smtpPw;

    @Bean
    public JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);
        mailSender.setUsername(smtpUn);
        mailSender.setPassword(smtpPw);

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.starttls.enable", "true");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.transport.protocol", "smtp");
        javaMailProperties.put("mail.debug", "true");


        mailSender.setJavaMailProperties(javaMailProperties);
        return mailSender;
    }

}