package com.example.kitchensink.config;

import com.example.kitchensink.security.CustomAccessDeniedHandler;
import com.example.kitchensink.security.CustomAuthenticationEntryPoint;
import com.example.kitchensink.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomAccessDeniedHandler accessDeniedHandler,
                          CustomAuthenticationEntryPoint authenticationEntryPoint) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("token")
                        .logoutSuccessUrl("/auth/login")
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/kitchensink").permitAll()
                        .requestMatchers("/access-denied").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}