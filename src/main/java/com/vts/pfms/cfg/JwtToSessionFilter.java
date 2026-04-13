package com.vts.pfms.cfg;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

public class JwtToSessionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {

            // ✅ 1. If already authenticated (session exists), skip
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            // ✅ 2. Extract Bearer token
            String token = extractBearerToken(request);

            if (token != null) {

                // ✅ 3. Validate JWT
                Claims claims = JwtUtil.validateToken(token);

                // ✅ 4. Check expiry
                if (claims.getExpiration().before(new Date())) {
                    handleExpiredToken(request, response);
                    return;
                }

                // ✅ 5. Extract user info
                String username = claims.getSubject();

                System.out.println("username"+username);
                
                // 👉 Optional: extract roles from claims
                // List<GrantedAuthority> authorities = extractRoles(claims);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.emptyList() // replace if roles needed
                        );

                // ✅ 6. Set authentication
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // ✅ 7. Create session
                HttpSession session = request.getSession(true);
                session.setAttribute(
                        "SPRING_SECURITY_CONTEXT",
                        SecurityContextHolder.getContext()
                );
                session.setAttribute("username", username);
                request.getSession().setAttribute("loginPage", "login");
                
                response.sendRedirect(request.getContextPath() + "/welcome");
                return;
            }

        } catch (Exception ex) {
            // ❌ Invalid token
        	ex.printStackTrace();
            handleInvalidToken(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    // ✅ Extract token from Authorization header
    private String extractBearerToken(HttpServletRequest request) {

        // 1️⃣ First check Authorization header
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        // 2️⃣ If not in header, check URL param
        String tokenParam = request.getParameter("token");
        String username = request.getParameter("username");
        System.out.println("username**** " + username + "tokenParam *******" + tokenParam);
        if (tokenParam != null && !tokenParam.isEmpty()) {
            return tokenParam;
        }

        return null;
    }

    // ❌ Handle expired token
    private void handleExpiredToken(HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {

        invalidateSession(request);
        SecurityContextHolder.clearContext();

        response.sendRedirect("/login?sessionExpired");
    }

    // ❌ Handle invalid token
    private void handleInvalidToken(HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {

        invalidateSession(request);
        SecurityContextHolder.clearContext();

        response.sendRedirect("/login?error");
    }

    // 🔄 Invalidate session safely
    private void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    // ✅ OPTIONAL: Extract roles
    /*
    private List<GrantedAuthority> extractRoles(Claims claims) {
        List<String> roles = (List<String>) claims.get("roles");

        if (roles == null) return Collections.emptyList();

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }
    */

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        // ✅ Skip preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String header = request.getParameter("token");

        // ✅ Run ONLY if Bearer token present
        return (header == null);
    }


    
    
}
