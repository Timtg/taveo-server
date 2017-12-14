package no.timesaver.filters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MDCFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(MDCFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) req;
        log.debug("MDCFilter: " +httpReq.getHeader("Authorization"));

        StopWatch watch = new StopWatch();

        watch.start();
        chain.doFilter(req, res);
        watch.stop();

        log.info("httpTook={}ms reqUrl={} regQueryParams={} httpMethod={} srcHost={}",
                watch.getTotalTimeMillis(),
                httpReq.getRequestURL().toString(),
                httpReq.getQueryString(),
                httpReq.getMethod(),
                httpReq.getRemoteHost()
        );
    }

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig arg0) throws ServletException {}

}

