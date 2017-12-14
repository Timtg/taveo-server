package no.timesaver.filters;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DDOSProtectionFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(DDOSProtectionFilter.class);
    private final Map<String,Boolean> blacklistedIps;
    private final Map<String,Map<LocalDateTime,Boolean>> requestPerIp;
    private final static int MAX_REQUEST_PER_1_SEC = 30;

    public DDOSProtectionFilter() {
        blacklistedIps = ExpiringMap.builder()
                .expirationPolicy(ExpirationPolicy.CREATED)
                .expiration(1, TimeUnit.MINUTES)
                .build();

        requestPerIp = ExpiringMap.builder()
                .expirationPolicy(ExpirationPolicy.ACCESSED)
                .expiration(5, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        log.debug("DDosProtectionFilter: " +httpReq.getHeader("Authorization"));

        String remoteIp = httpReq.getRemoteHost();
        log.debug("DDOS check started for host {}",remoteIp);

        if(blacklistedIps.containsKey(remoteIp)){
            log.warn("Blocked request from blacklisted host {}",remoteIp);
            blockRequest();
        }

        if(!requestPerIp.containsKey(remoteIp)){
            Map<LocalDateTime,Boolean> accessMap = ExpiringMap.builder()
                    .expirationPolicy(ExpirationPolicy.CREATED)
                    .expiration(1, TimeUnit.SECONDS)
                    .build();
            accessMap.put(LocalDateTime.now(),Boolean.TRUE);
            requestPerIp.put(remoteIp,accessMap);
            chain.doFilter(request, response);
        } else {
            Map<LocalDateTime, Boolean> accessMap = requestPerIp.get(remoteIp);
            if(accessMap.size() > MAX_REQUEST_PER_1_SEC){
                log.warn("Too many request from host {}. Rejecting and blacklisting",remoteIp);
                blacklistedIps.put(remoteIp,Boolean.TRUE);
                blockRequest();
            }
            accessMap.put(LocalDateTime.now(),Boolean.TRUE);
            requestPerIp.put(remoteIp,accessMap);
            chain.doFilter(request, response);
        }
    }

    private void blockRequest() {
        throw new SecurityException("DDOS protection activated, please retry in a short while");
    }


    @Override
    public void destroy() {

    }
}
