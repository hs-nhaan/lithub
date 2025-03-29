package com.hsnhaan.lithub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AdminDetailsService adminDetailsSvc, CustomUserDetailsService userDetailsSvc) {
        DaoAuthenticationProvider adminAuthProvider = new DaoAuthenticationProvider();
        adminAuthProvider.setUserDetailsService(adminDetailsSvc);
        adminAuthProvider.setPasswordEncoder(passwordEncoder());
        
        DaoAuthenticationProvider userAuthProvider = new DaoAuthenticationProvider();
        userAuthProvider.setUserDetailsService(userDetailsSvc);
        userAuthProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(adminAuthProvider, userAuthProvider);
    }
	
	@Bean
	public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http, AdminAuthenticationSuccessHandler adminSuccessHandler) throws Exception {
		http
			.securityMatcher("/admin/**")
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/admin/css/**", "/admin/js/**").permitAll()
				.requestMatchers("/admin/genre/**", "/admin/story/delete/**", "/admin/account/**").hasRole("SUPER_ADMIN")
				.requestMatchers("/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
				.anyRequest().permitAll()
			)
			.formLogin(login -> login
				.loginPage("/admin/login")
				.loginProcessingUrl("/admin/do-login")
			    .successHandler(adminSuccessHandler)
				.permitAll()
			)
			.logout(logout -> logout
				.logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout"))
				.logoutSuccessUrl("/admin/login")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
			)
			.sessionManagement(session -> session
				.maximumSessions(1)
				.expiredUrl("/admin/login")
			);
		return http.build();
	}
	
	@Bean
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http, UserAuthenticationSuccessHandler userSuccessHandler) throws Exception {
        http
        	.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/{slug}/comment", "/tai-khoan/**").hasRole("USER")
                .anyRequest().permitAll()
            )
            .formLogin(login -> login
                .loginPage("/dang-nhap")
                .loginProcessingUrl("/dang-nhap")
                .successHandler(userSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
    				.logoutRequestMatcher(new AntPathRequestMatcher("/dang-xuat"))
    				.logoutSuccessUrl("/trang-chu")
    				.invalidateHttpSession(true)
    				.deleteCookies("JSESSIONID")
			)
			.sessionManagement(session -> session
				.maximumSessions(1)
				.expiredUrl("/trang-chu")
			);
        return http.build();
    }
	
}
