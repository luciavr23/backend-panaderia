package com.example.config;

import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.example.security.CustomUserDetailsService;
import com.example.security.JwtAuthenticationFilter;
import com.example.security.JwtTokenProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtTokenProvider tokenProvider;
	private final CustomUserDetailsService customUserDetailsService;

	public SecurityConfig(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
		this.tokenProvider = tokenProvider;
		this.customUserDetailsService = customUserDetailsService;
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(customUserDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(request -> {
			var corsConfig = new CorsConfiguration();
			corsConfig.setAllowedOrigins(List.of(
"http://localhost:3000",
  "https://frontend-panaderia-6mbg1hob2-lucias-projects-d203b45e.vercel.app/"
));

			corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
			corsConfig.setAllowedHeaders(List.of("*"));
			corsConfig.setAllowCredentials(true);
			return corsConfig;
		})).csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/**", "/users/register", "/products/**", "/dailySpecials",
								"/products/popular", "/reviews", "/products/**", "/reviews/**", "/info", "/schedules",
								"/users/forgot-password", "users/reset-password", "/allergens", "/public/**",
								"/email/send", "/cloudinary/delete/{publicId}", "/info/**", "/info/contact", "/ws/**",
								"/topic/**", "/orders/**")
						.permitAll().requestMatchers("/cart/**", "/account/**").hasRole("CLIENT")
						.requestMatchers("/payment/create-payment-intent").hasRole("CLIENT")
						.requestMatchers("/favicon.ico").permitAll().requestMatchers(HttpMethod.GET, "/users/me")
						.authenticated().requestMatchers(HttpMethod.PUT, "/users/me").authenticated()
						.requestMatchers("/orders/today").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/schedules/**").hasRole("ADMIN")
						.requestMatchers("/admin/stats").hasRole("ADMIN").requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/categories").permitAll().requestMatchers("/categories/{id}").permitAll()
						.requestMatchers("/categories").hasRole("ADMIN").requestMatchers("/categories/{id}")
						.hasRole("ADMIN").requestMatchers("/orders/**").authenticated()

						// Fallback
						.anyRequest().authenticated())
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(new JwtAuthenticationFilter(tokenProvider, customUserDetailsService),
						UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		SecretKeySpec secretKey = new SecretKeySpec(tokenProvider.getSecret().getBytes(), "HmacSHA256");
		return NimbusJwtDecoder.withSecretKey(secretKey).build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
