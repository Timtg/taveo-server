package no.timesaver.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


@Configuration
public class DbConfig{
    private final static int MAX_WAIT_SECONDS = 60;


    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSource dataSource(@Value("${spring.datasource.url}")String url,
                                 @Value("${spring.datasource.username}")String userName,
                                 @Value("${spring.datasource.password}")String passWord,
                                 @Value("${spring.datasource.driverClassName}")String driverClassName){
        DataSource dataSource = new DataSource();
        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setUsername(userName);
        poolProperties.setPassword(passWord);
        poolProperties.setDriverClassName(driverClassName);
        poolProperties.setInitialSize(7);
        poolProperties.setMaxActive(8);
        poolProperties.setMaxIdle(3);
        poolProperties.setMinIdle(3);
        poolProperties.setMaxWait(MAX_WAIT_SECONDS * 1000);
        poolProperties.setMinEvictableIdleTimeMillis(poolProperties.getMaxWait() * 2);
//        poolProperties.setJdbcInterceptors(
//                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
//                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("Select 1");
        dataSource.setPoolProperties(poolProperties);
        dataSource.setUrl(url);
        return dataSource;

    }

    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource);
    }

}