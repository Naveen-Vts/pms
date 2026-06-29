package com.vts.pfms.cfg;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Run this filter first
public class HttpMethodFilter implements Filter {

    // Allowed HTTP methods
    private static final Set<String> ALLOWED_METHODS = Set.of("GET", "POST", "HEAD");

    // File extensions to skip (static resources)
    private static final Set<String> STATIC_EXTENSIONS = Set.of(
            ".js", ".css", ".png", ".jpg", ".jpeg", ".gif", ".ico", ".woff", ".woff2", ".ttf", ".map"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest req && response instanceof HttpServletResponse res) {

            String path = req.getRequestURI().toLowerCase();

            
            // Skip filter for static files
            boolean isStatic = STATIC_EXTENSIONS.stream().anyMatch(path::endsWith);

            if (!isStatic && path.startsWith("/pfms-dg/")) { // Only filter PMS endpoints
                String method = req.getMethod();

                // Block all methods except GET, POST, HEAD
                if (!ALLOWED_METHODS.contains(method)) {
                    res.setHeader("Allow", String.join(", ", ALLOWED_METHODS));
                    res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "HTTP Method Not Allowed");
                    return;
                }

                // Explicitly block OPTIONS
                if ("OPTIONS".equalsIgnoreCase(method)) {
                    res.setHeader("Allow", String.join(", ", ALLOWED_METHODS));
                    res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "OPTIONS Not Allowed");
                    return;
                }
            }
        }

        // Continue the filter chain
        chain.doFilter(request, response);
    }
}
