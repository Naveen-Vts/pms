//package com.vts.pfms.login;
//
//import java.io.IOException;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class CaptchaFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        if ("/login".equals(request.getServletPath()) && "POST".equalsIgnoreCase(request.getMethod())) {
//            String userCaptcha = request.getParameter("captchaInput");
//            String sessionCaptcha = (String) request.getSession().getAttribute("LOGIN_CAPTCHA");
//
//      
//            System.out.println(sessionCaptcha+"----"+userCaptcha);
//            
////            if (sessionCaptcha == null || !sessionCaptcha.equals(userCaptcha)) {
////                // captcha invalid → redirect back to login
////                response.sendRedirect(request.getContextPath() + "/login?error=Invalid Captcha");
////                return; 
////            }
//
//          
//            request.getSession().removeAttribute("LOGIN_CAPTCHA");
//        }
//
//        // only runs if captcha is valid OR request is not /login POST
//        filterChain.doFilter(request, response);
//    }
//}
