package com.edu3d.plateforme3d.config;

import com.edu3d.plateforme3d.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // 1. Routes publiques
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs"
                        ).permitAll()

                        // 2. GET public
                        .requestMatchers(HttpMethod.GET,
                                "/api/courses", "/api/courses/**",
                                "/api/classrooms", "/api/classrooms/**"
                        ).permitAll()

                        // 3. Users — me et photo AVANT la règle ADMIN
                        .requestMatchers(HttpMethod.GET,  "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/users/*/photo").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/users/me").authenticated()

                        // 4. Users — ADMIN seulement
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // 5. Courses écriture
                        .requestMatchers(HttpMethod.POST,   "/api/courses").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH,  "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")

                        // 6. Slides
                        .requestMatchers(HttpMethod.POST,   "/api/slides").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/slides/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/slides/**").hasAnyRole("TEACHER", "ADMIN")

                        // 7. Classrooms écriture
                        .requestMatchers(HttpMethod.POST,   "/api/classrooms").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/classrooms/join").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/classrooms/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/classrooms/**").hasAnyRole("TEACHER", "ADMIN")

                        // 8. Enrollment
                        .requestMatchers(HttpMethod.POST, "/api/enroll").hasAnyRole("STUDENT", "ADMIN")

                        // 9. Tout le reste — juste connecté
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}