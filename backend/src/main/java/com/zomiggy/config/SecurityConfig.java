package com.zomiggy.config;

import com.zomiggy.security.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.*;

@Configuration
public class SecurityConfig {
	@Bean SecurityFilterChain filterChain(HttpSecurity http, JwtFilter filter, CorsConfigurationSource corsSource) throws Exception {
		return http.csrf(c -> c.disable()).cors(c -> c.configurationSource(corsSource))
		.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(a -> a.requestMatchers("/api/auth/**", "/ws/**", "/actuator/health", "/error").permitAll()
			.anyRequest().authenticated())
		.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean CorsConfigurationSource corsConfigurationSource(
	    @Value("${app.cors.origin}") String configuredOrigins) {
	CorsConfiguration c = new CorsConfiguration();
	c.setAllowedOrigins(Arrays.stream(configuredOrigins.split(","))
		.map(String::trim).filter(origin -> !origin.isEmpty()).toList());
	c.setAllowedMethods(List.of("GET", "POST", "PATCH", "OPTIONS"));
	c.setAllowedHeaders(List.of("Authorization", "Content-Type"));
	c.setAllowCredentials(true);
	UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
	s.registerCorsConfiguration("/**", c);
	return s;
    }
}