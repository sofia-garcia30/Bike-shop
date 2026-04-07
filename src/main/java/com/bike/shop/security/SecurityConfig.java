package com.bike.shop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.bike.shop.repository.UsuarioRepository;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UsuarioRepository usuarioRepository;

    // ✅ Constructor con @Lazy para romper el ciclo
    public SecurityConfig(@Lazy JwtFilter jwtFilter, UsuarioRepository usuarioRepository) {
        this.jwtFilter = jwtFilter;
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con email: " + email));
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        String allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        List<String> allowedOrigins;

        if (allowedOriginsEnv != null && !allowedOriginsEnv.isBlank()) {
            allowedOrigins = Arrays.asList(allowedOriginsEnv.split(","));
        } else {
            allowedOrigins = List.of(
                    "http://localhost:4200",
                    "https://bike-shop-frontend-production.up.railway.app"
            );
        }

        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET,    "/api/bicicletas/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.POST,   "/api/bicicletas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/bicicletas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/bicicletas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/api/clientes/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.POST, "/api/clientes").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.PUT,  "/api/clientes/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.POST,  "/api/ventas").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.GET,   "/api/ventas/mis-ventas/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.GET,   "/api/ventas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/ventas/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.GET,   "/api/pedidos/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.POST,  "/api/pedidos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/pedidos/recibido/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.GET,    "/api/proveedores/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.POST,   "/api/proveedores").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/proveedores/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/proveedores/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/dashboard").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/top-bicicletas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reportes/ventas/pdf/mis-ventas").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.GET, "/api/reportes/ventas/excel/mis-ventas").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.GET, "/api/reportes/**").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}