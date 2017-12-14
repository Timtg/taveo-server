package no.timesaver.filters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class AccessControlFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(AccessControlFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        log.debug("AccessControlFilter: " +request.getHeader("Authorization"));

        response.addHeader("Cache-Control","no-cache, no-store, must-revalidate, max-age=0");
        response.addHeader("Expires","0");
        response.addHeader("Pragma","no-cache");

        String origin = request.getHeader("Origin");

        response.addHeader("Access-Control-Allow-Origin", getAccessAllowedOriginForDomain(origin));

        if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
            // CORS "pre-flight" request
            response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE");
            response.addHeader("Access-Control-Allow-Headers","X-Requested-With,x-request-id,Origin,Content-Type, Accept,Authorization," +
                    "host,connection,user-agent,referer,accept-encoding,content-length,x-forwarded-proto,x-forwarded-port," +
                    "via,connect-time,x-request-start,total-route-time");
            return;
        }


        chain.doFilter(request, response);
    }

    private String getAccessAllowedOriginForDomain(String origin) {
        String scheme = !StringUtils.isEmpty(origin) && origin.toLowerCase().contains("https") ? "https://" : "http://";
        try {
            URL originUrl = new URL(origin);
            Pattern hostAllowedPattern = Pattern.compile("(.+\\.)*time-saver\\.no", Pattern.CASE_INSENSITIVE);
            Pattern devsAllowedPattern = Pattern.compile("128\\.1\\.11\\.*\\d{2,3}", Pattern.CASE_INSENSITIVE);

            // Verify host
            if (hostAllowedPattern.matcher(originUrl.getHost()).matches() || devsAllowedPattern.matcher(originUrl.getHost()).matches()) {
                return origin;
            } else {
                log.info("Unknown origin in CORS pre-flight header: {}", origin);
                return scheme+"time-saver.no";
            }
        } catch (MalformedURLException ex) {
            //fallback
            return scheme + "taveoclient.time-saver.no";
        }
    }

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig arg0) throws ServletException {}

}

