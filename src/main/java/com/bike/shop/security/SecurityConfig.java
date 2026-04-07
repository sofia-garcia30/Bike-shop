package com.bike.shop.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.util.List;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UsuarioRepository usuarioRepository;

    // ─── PasswordEncoder ────────────────────────────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ─── UserDetailsService ─────────────────────────────────────
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe usuario con email: " + email));
    }

    // ─── AuthenticationProvider ─────────────────────────────────
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ─── AuthenticationManager ──────────────────────────────────
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    // ─── CORS ────────────────────────────────────────────────────
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Leer orígenes permitidos desde variable de entorno (separados por coma si hay varios)
        String allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOriginsEnv == null || allowedOriginsEnv.isBlank()) {
            allowedOriginsEnv = "http://localhost:4200"; // valor por defecto para desarrollo local
        }
        List<String> allowedOrigins = Arrays.asList(allowedOriginsEnv.split(","));
        config.setAllowedOrigins(allowedOrigins);

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ─── Security Filter Chain ───────────────────────────────────
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ── PÚBLICO ──────────────────────────────────────
                        .requestMatchers("/auth/**").permitAll()

                        // ── BICICLETAS ───────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/bicicletas/**")
                        .hasAnyRole("ADMIN", "EMPLEADO")        // ver ✅
                        .requestMatchers(HttpMethod.POST, "/api/bicicletas")
                        .hasRole("ADMIN")                        // crear ✅
                        .requestMatchers(HttpMethod.PUT, "/api/bicicletas/**")
                        .hasRole("ADMIN")                        // editar ✅
                        .requestMatchers(HttpMethod.DELETE, "/api/bicicletas/**")
                        .hasRole("ADMIN")                        // eliminar ✅

                        // ── CLIENTES ─────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/clientes/**")
                        .hasAnyRole("ADMIN", "EMPLEADO")         // ver ✅
                        .requestMatchers(HttpMethod.POST, "/api/clientes")
                        .hasAnyRole("ADMIN", "EMPLEADO")         // crear ✅
                        .requestMatchers(HttpMethod.PUT, "/api/clientes/**")
                        .hasAnyRole("ADMIN", "EMPLEADO")         // editar ✅

                        // ── VENTAS ───────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/ventas")
                        .hasAnyRole("ADMIN", "EMPLEADO")         // registrar ✅
                        .requestMatchers(HttpMethod.GET, "/api/ventas/mis-ventas/**")
                        .hasAnyRole("ADMIN", "EMPLEADO")         // sus ventas ✅
                        .requestMatchers(HttpMethod.GET, "/api/ventas/**")
                        .hasRole("ADMIN")                        // todas las ventas ✅
                        .requestMatchers(HttpMethod.PATCH, "/api/ventas/**")
                        .hasAnyRole("ADMIN", "EMPLEADO")

                        // ── PEDIDOS ──────────────────────────────────────
                        .requestMatchers(HttpMethod.PATCH, "/api/pedidos/recibido/**")
                        .hasAnyRole("ADMIN", "EMPLEADO")         // marcar recibido ✅
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/**")
                        .hasAnyRole("ADMIN", "EMPLEADO")                        // ver pedidos ✅
                        .requestMatchers(HttpMethod.POST, "/api/pedidos")
                        .hasRole("ADMIN")                        // crear pedidos ✅

                        // ── PROVEEDORES ──────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/proveedores/**")
                        .hasAnyRole("ADMIN", "EMPLEADO")         // ver ✅
                        .requestMatchers(HttpMethod.POST, "/api/proveedores")
                        .hasRole("ADMIN")                        // crear ✅
                        .requestMatchers(HttpMethod.PUT, "/api/proveedores/**")
                        .hasRole("ADMIN")                        // editar ✅
                        .requestMatchers(HttpMethod.DELETE, "/api/proveedores/**")
                        .hasRole("ADMIN")                        // eliminar ✅

                        // ── DASHBOARD ────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/dashboard")
                        .hasAnyRole("ADMIN", "EMPLEADO")         // básico ✅
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/top-bicicletas")
                        .hasRole("ADMIN")                        // top bicicletas ✅

                        // ── REPORTES ─────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/reportes/ventas/pdf/mis-ventas").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers(HttpMethod.GET, "/api/reportes/ventas/excel/mis-ventas").hasAnyRole("ADMIN", "EMPLEADO")         // sus reportes ✅
                        .requestMatchers(HttpMethod.GET,"/api/reportes/**")
                        .hasRole("ADMIN")                        // reportes globales ✅

                        // ── USUARIOS ─────────────────────────────────────
                        .requestMatchers("/api/usuarios/**")
                        .hasRole("ADMIN")                        // gestión usuarios ✅

                        // ── CUALQUIER OTRA → autenticado ─────────────────
                        .anyRequest().authenticated()

                )
                .authenticationProvider(authProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
