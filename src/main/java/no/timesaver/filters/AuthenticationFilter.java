package no.timesaver.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.timesaver.ThreadLocalCurrentUser;
import no.timesaver.ThreadLocalJwt;
import no.timesaver.domain.User;
import no.timesaver.exception.HttpResponseWrapper;
import no.timesaver.jwt.JwtVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AuthenticationFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    private final JwtVerifier jwtVerifier;

    public AuthenticationFilter(JwtVerifier jwtVerifier) {
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        String requestURI = httpReq.getRequestURI();
        String requestMethod = httpReq.getMethod();

        log.debug("AuthFilter: " +httpReq.getHeader("Authorization"));

        try {
            Optional<String> authorizationHeader = getAuthorizationHeader(httpReq);

            if (isOpenUrl(requestURI, requestMethod)) {
                chain.doFilter(request, response);
            } else {
                if (!authorizationHeader.isPresent()) {
                    String errorMsg = "Missing jwt when accessing restricted API " + requestURI;
                    log.info(errorMsg);
                    unauthorized(errorMsg, response);
                    return;
                }
                boolean validated = validateTokenAndSetThreadLocalCurrentUserIfAvailable(authorizationHeader.get());
                if (validated) {
                    chain.doFilter(request, response);
                } else {
                    String errorMsg = "Attempting to access restricted API without a valid token: " + requestURI;
                    log.info(errorMsg);
                    unauthorized(errorMsg, response);
                }
            }
        } finally {
            ThreadLocalCurrentUser.unset();
            ThreadLocalJwt.unset();
        }
    }

    private void unauthorized(String errorMsg, ServletResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED,mapper.writeValueAsString(new HttpResponseWrapper(errorMsg)));
    }

    private boolean validateTokenAndSetThreadLocalCurrentUserIfAvailable(String authorizationHeader) {
        if(StringUtils.isEmpty(authorizationHeader)){
            return false;
        }
        Optional<User> u = jwtVerifier.verify(authorizationHeader);
        if(u.isPresent()){
            ThreadLocalCurrentUser.set(u.get());
            ThreadLocalJwt.set(authorizationHeader);
            return true;
        }
        return false;
    }

    private Optional<String> getAuthorizationHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        /*Attempt to extract jwt from queryParam if not present in header */
        if(StringUtils.isEmpty(authorization) && !StringUtils.isEmpty(request.getQueryString())){
            String key = "jwt=";
            int startIndex = request.getQueryString().indexOf(key);
            if(startIndex != -1){
                String subStr = request.getQueryString().substring(startIndex);
                int endIndex = subStr.contains("&") ? subStr.indexOf("&") : subStr.length();
                authorization = subStr.substring(key.length(),endIndex);
            }
        }
        if(StringUtils.isEmpty(authorization)){
            return Optional.empty();
        }
        return Optional.of(authorization.replace("Bearer ", ""));
    }

    private boolean isOpenUrl(String uri, String requestMethod) {

        if(HttpMethod.POST.name().equals(requestMethod)){
            List<String> openPostExactEndingUrls = Arrays.asList(
                    "/user/",
                    "/user/auth/login",
                    "/user/auth/login/",
                    "/user/auth/reset/request",
                    "/user/auth/reset",
                    "/user/email/verification",
                    "/user/email/verification/",
                    "/user/mobile/verification",
                    "/user/mobile/verification/",
                    "/mobile/verification/new",
                    "/mobile/verification/new/"
            );
            return openPostExactEndingUrls.stream().anyMatch(uri::endsWith);
        }

        return false;
    }

    @Override
    public void destroy() {
        ThreadLocalCurrentUser.unset();
        ThreadLocalJwt.unset();
    }
}
