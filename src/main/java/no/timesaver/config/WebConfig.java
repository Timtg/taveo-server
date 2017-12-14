package no.timesaver.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.timesaver.filters.AccessControlFilter;
import no.timesaver.filters.AuthenticationFilter;
import no.timesaver.filters.DDOSProtectionFilter;
import no.timesaver.filters.MDCFilter;
import no.timesaver.jwt.JwtVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebMvcRegistrationsAdapter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;


@Configuration
@ComponentScan("no.timesaver")
public class WebConfig extends WebMvcConfigurerAdapter {

    /** ==== Static sub-dir discovery ==== **/

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/confirm-email").setViewName("redirect:/confirm-email/");
        registry.addViewController("/confirm-email/").setViewName("forward:/confirm-email/index.html");
        super.addViewControllers(registry);
    }

    /** ==== Filters ==== **/
    @Autowired private JwtVerifier jwtVerifier;

    /*
    order: lowest = first

     |
     |
    \_/

     */
    @Bean
    public FilterRegistrationBean accessControlFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        AccessControlFilter accessControlFilter = new AccessControlFilter();
        registrationBean.setUrlPatterns(Collections.singletonList("/api/*"));
        registrationBean.setFilter(accessControlFilter);
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean mdcFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        MDCFilter mdcFilter = new MDCFilter();
        registrationBean.setUrlPatterns(Collections.singletonList("/api/*"));
        registrationBean.setFilter(mdcFilter);
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean DDOSProtectionFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        DDOSProtectionFilter ddosProtectionFilter = new DDOSProtectionFilter();
        registrationBean.setFilter(ddosProtectionFilter);
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean authenticationFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(jwtVerifier);
        registrationBean.setUrlPatterns(Collections.singletonList("/api/*"));
        registrationBean.setFilter(authenticationFilter);
        registrationBean.setOrder(3);
        return registrationBean;
    }

    /** === End Filters === **/

    @Bean
    public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(jacksonObjectMapper());
        return jsonConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false); // See SPR-7316
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(stringHttpMessageConverter);
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        converters.add(new Jaxb2RootElementHttpMessageConverter());
        converters.add(customJackson2HttpMessageConverter());
    }

    @Bean
    public javax.validation.Validator localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return objectMapper;
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}


