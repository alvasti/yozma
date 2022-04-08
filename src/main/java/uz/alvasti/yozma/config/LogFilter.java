package uz.alvasti.yozma.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class LogFilter extends OncePerRequestFilter {

    static private final Set<String> urlsNotToSave = new HashSet<>() {{
        // add links for do not save in the log
        // add("/[your link]");
    }};

    private final LogService logService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public LogFilter(LogService logService) {
        this.logService = logService;
    }

    static private ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {

        return new ContentCachingRequestWrapper(request);

    }

    static private ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {

        return new ContentCachingResponseWrapper(response);

    }

    private static boolean allowToSave(String uri) {
        for (String url : urlsNotToSave) {
            if (uri.contains(url))
                return false;
        }
        return true;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (isAsyncDispatch(httpServletRequest)) {

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            doFilterWrapped(new MultiReadHttpServletRequest(httpServletRequest), wrapResponse(httpServletResponse), filterChain);
        }
    }

    private void doFilterWrapped(MultiReadHttpServletRequest request, ContentCachingResponseWrapper response, FilterChain filterChain) throws IOException, ServletException {

        try {

            logService.logComingRequestAndFillThreadContext(request);

            filterChain.doFilter(request, response);

        } finally {
            afterRequest(request, response);
            response.copyBodyToResponse();
            ThreadContext.clearAll();
        }
    }

    private void afterRequest(MultiReadHttpServletRequest request, ContentCachingResponseWrapper response) {
        if (allowToSave(request.getRequestURI())) {
            logService.logOutgoingRequestAndFillThreadContext(request, response);
        }
    }

    public static Set<String> getUrlsNotToSave() {
        return urlsNotToSave;
    }

}

