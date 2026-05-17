package com.vts.pfms.cfg;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class CookieConfig {

    @Bean
    public org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory webServerFactory() {
        org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory factory =
                new org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory();

        factory.addContextCustomizers(context -> {
            context.setSessionCookieName("JSESSIONID");
            context.setUseHttpOnly(true);
            context.getServletContext()
                   .getSessionCookieConfig()
                   .setSecure(false);
            context.getServletContext()
                   .getSessionCookieConfig()
                   .setHttpOnly(true);
            context.getServletContext()
                   .getSessionCookieConfig()
                   .setComment("SameSite=Strict");
        });
        return factory;
    }
}
