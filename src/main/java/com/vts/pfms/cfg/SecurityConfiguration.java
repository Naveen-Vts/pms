package com.vts.pfms.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.vts.pfms.login.CaptchaFilter;
import com.vts.pfms.login.CustomAuthenticationFailureHandler;
import com.vts.pfms.login.CustomLogoutHandler;
import com.vts.pfms.login.LoginDetailsServiceImpl;
import com.vts.pfms.login.LoginSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration{
	
	@Autowired
    private LoginSuccessHandler successHandler;
	
	@Autowired
	private PasswordDecryptFilter passwordDecryptFilter;
	@Autowired
	private CaptchaFilter captchaFilter;

	@Autowired
	private CustomAuthenticationFailureHandler failureHandler;
	
	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
        http.authorizeHttpRequests(request -> 
        		request.requestMatchers("/").hasAnyRole("USER", "ADMIN")
	        		   .requestMatchers("/webjars/**", "/resources/**", "/view/**", "/LoginPage/*", "/pfms-dg/*", "/api/sync/**","/refresh-captcha", 
	        					  "/login", "/wr", "/login?sessionInvalid", "/login?sessionExpired", "/wr?sessionInvalid", "/wr?sessionExpired", "/ProjectBriefingPaper.htm", 
	        					  "/ProjectRequirementAttachmentDownload.htm", "/ProjectClosureChecklistFileDownload.htm", "/TimeSheetWorkFlowPdf.htm", "/CommitteeMinutesViewAllDownloadPdf.htm/**").permitAll()
	        		   .requestMatchers(HttpMethod.OPTIONS, "/**").denyAll()
	        		   .anyRequest()
	        		   .authenticated()
        		)
        	.formLogin(login -> 
        			login.loginPage("/login")
        			   	 .defaultSuccessUrl("/welcome")
        			   	 .failureUrl("/login?error=1")
        			   	 .permitAll()
        			   	 .usernameParameter("username")
        			   	 .passwordParameter("password")
        			   	 .successHandler(successHandler)
        			   	 .failureHandler(failureHandler)
        			)
        	.logout(logout -> 
        			logout
        				  //.logoutSuccessUrl("/login?logout=1")
        				  //.invalidateHttpSession(true)
        				  .deleteCookies("JSESSIONID")
        				  .addLogoutHandler(logoutSuccessHandler())
        				  .invalidateHttpSession(false) 
        			)
        	.exceptionHandling(exception -> 
        			exception.accessDeniedPage("/login")
        			)
        	.csrf(Customizer.withDefaults())
        	.sessionManagement(session ->
        			session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        				   .invalidSessionUrl("/login?sessionInvalid")
        				   .sessionFixation().migrateSession()
        				   .maximumSessions(2)
        				   .maxSessionsPreventsLogin(false)
        			)
        	.headers(headers -> headers
        		    .cacheControl(cache -> cache.disable()) // Disable default cache control
        		    .frameOptions(frame -> frame.sameOrigin()) // Optional: Protect against clickjacking
        		    .httpStrictTransportSecurity(hsts -> hsts.disable()) // Optional: Adjust as needed
        		    .addHeaderWriter((request, response) -> {
        		        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        		        response.setHeader("Pragma", "no-cache");
        		        response.setDateHeader("Expires", 0);
        		    })
//         		   .contentSecurityPolicy(csp -> 
//                  csp.policyDirectives(
//       				    "default-src 'self'; " +
//       				    "script-src 'self' 'unsafe-inline' blob:; " +
//       				    "style-src 'self' ; " +
//       				    "img-src 'self' data: blob:; " +
//       				    "font-src 'self'; " +
//       				    "connect-src 'self'; " +
//       				    "object-src 'self' data: blob:; " +  
//       				    "frame-src 'self' data: blob:; " +   
//       				    "frame-ancestors 'self'; " +
//       				    "base-uri 'self'; " +
//       				    "form-action 'self';"
//       				))
        		  
        		)
        	.addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)
        	.addFilterBefore(passwordDecryptFilter, UsernamePasswordAuthenticationFilter.class);
        

        	;
          return http.build();
    }
	
	@Bean
	AuthenticationProvider authenticationProvider() throws Exception {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return daoAuthenticationProvider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception {
	    return http.getSharedObject(AuthenticationManagerBuilder.class)
	               .userDetailsService(userDetailsService)
	               .passwordEncoder(passwordEncoder)
	               .and()
	               .build();
	}
	
	@Bean
	UserDetailsService userDetailsService() {
		return new LoginDetailsServiceImpl();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() throws Exception{
		
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	LogoutHandler logoutSuccessHandler() {
		return new CustomLogoutHandler();
	}
}
