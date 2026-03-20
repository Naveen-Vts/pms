package com.vts.pfms.cfg;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("loginFilter")
public class FilterForLogInFromOtherApp extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        
        // Robust path check (sometimes servletPath can vary)
        if (path != null && path.contains("/TMDS")) {
            String headerKey = request.getHeader("X-API-KEY");
            String[]keys = request.getParameter("api_key").split("_");
            String paramKey = keys[0];
            System.out.println("paramKey"+paramKey);
            System.out.println("headerKey"+headerKey);
            String decryptedUsername;
            try {
                byte[] decodedBytes = java.util.Base64.getDecoder().decode(keys[1]);
                decryptedUsername = new String(decodedBytes);
                System.out.println(decryptedUsername+"---decryptedUsername");
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Username Encoding");
                return;
            }
            if ("VTS".equals(headerKey) || "VTS".equals(paramKey)) {
                // 1. Create Authentication
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                		decryptedUsername, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                
                // 2. Set Context
                SecurityContextHolder.getContext().setAuthentication(auth);
                
                // 3. IMPORTANT: Persist the context to the Session
                // In Spring Security 6, the contex	t doesn't automatically save on manual redirects
                request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

                // 4. Mark for TMDS
                request.getSession().setAttribute("IS_TMDS_LOGIN", "Y");
                request.getSession().setAttribute("loginPage", "login");
                
                // 5. Redirect
                response.sendRedirect(request.getContextPath() + "/welcome");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}