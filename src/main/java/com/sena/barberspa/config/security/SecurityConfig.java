package com.sena.barberspa.config.security;

import com.sena.barberspa.config.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF para APIs, habilitarlo para formularios web
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                        .disable())
                // Configurar las reglas de autorización de las peticiones HTTP
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso público (sin autenticación) a estas rutas
                        .requestMatchers(
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/home/**"),
                                new AntPathRequestMatcher("/debug/**"), // CRITICO: Permitir acceso a debug
                                new AntPathRequestMatcher("/usuario/login"),
                                new AntPathRequestMatcher("/usuario/registro"),
                                new AntPathRequestMatcher("/assets/**"),
                                new AntPathRequestMatcher("/static/**"),
                                new AntPathRequestMatcher("/images/**"),
                                new AntPathRequestMatcher("/css/**"),
                                new AntPathRequestMatcher("/js/**"),
                                new AntPathRequestMatcher("/img/**"),
                                new AntPathRequestMatcher("/favicon.ico"),
                                new AntPathRequestMatcher("/node_modules/**"),
                                new AntPathRequestMatcher("/api/v1/auth/**"),
                                new AntPathRequestMatcher("/swagger-ui/**"),
                                new AntPathRequestMatcher("/api-docs/**"),
                                new AntPathRequestMatcher("/productosVista"),
                                new AntPathRequestMatcher("/serviciosVista"))
                        .permitAll()
                        // Cualquier otra petición debe ser autenticada
                        .anyRequest().authenticated())
                // Configurar login form para web y session management
                .formLogin(formLogin -> formLogin
                        .loginPage("/usuario/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .permitAll())
                // Configurar la gestión de sesiones - STATEFUL para web, STATELESS para APIs
                .sessionManagement(sessionManager -> sessionManager
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                // Añadir el proveedor de autenticación
                .authenticationProvider(authenticationProvider)
                // Añadir nuestro filtro de JWT antes del filtro de usuario/contraseña
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}