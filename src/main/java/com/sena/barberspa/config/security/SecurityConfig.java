package com.sena.barberspa.config.security;

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
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para simplificar en entorno de
                                              // desarrollo
                .authorizeHttpRequests(auth -> auth
                        // Recursos públicos sin autenticación
                        .requestMatchers(
                                // Rutas públicas principales
                                "/",
                                "/home",
                                "/home/**",
                                "/publico/**",

                                // Rutas de productos y servicios públicos
                                "/productosVista",
                                "/serviciosVista",
                                "/productoHome/**",
                                "/servicioHome/**",
                                "/searchProductos",
                                "/searchServicios",

                                // Rutas de autenticación y registro (NUEVAS RUTAS)
                                "/publico/login",
                                "/publico/registro",
                                "/publico/cambiar-password",
                                "/publico/token-invalido",
                                "/publico/acceder",
                                "/usuario/login", // Mantener compatibilidad
                                "/usuario/login-success",
                                "/usuario/registro", // Mantener compatibilidad
                                "/usuario/save",
                                "/usuario/acceder",
                                "/usuario/resetPassword",
                                "/usuario/cambiarPassword",
                                "/usuario/saveNewPassword",
                                "/usuario/token-invalido",
                                "/usuario/create-test-user",
                                "/usuario/test-login",
                                "/usuario/validar-email",
                                "/usuario/test-registro",
                                "/usuario/create-admin",

                                // Recursos estáticos
                                "/assets/**",
                                "/img/**",
                                "/images/**",
                                "/css/**",
                                "/js/**",
                                "/vendor/**",
                                "/node_modules/**",
                                "/bootstrap/**",
                                "/webjars/**",
                                "/static/**",
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/debug/**",
                                "/error",
                                "/403",
                                "/favicon.ico")
                        .permitAll()

                        // Rutas específicas de cliente autenticado
                        .requestMatchers("/cliente/**", "/cart/**", "/getCart",
                                "/home/test-home", "/home/full", "/home/mantenimiento")
                        .hasAuthority("ROLE_CLIENTE")

                        // Rutas de empleado y superiores
                        .requestMatchers("/empleado/**")
                        .hasAnyAuthority("ROLE_EMPLEADO", "ROLE_ADMIN_SUCURSAL", "ROLE_GERENTE")

                        // Rutas de admin sucursal y superiores
                        .requestMatchers("/admin-sucursal/**")
                        .hasAnyAuthority("ROLE_ADMIN_SUCURSAL", "ROLE_GERENTE")

                        // Rutas exclusivas de gerente (Super Admin)
                        .requestMatchers("/gerente/**", "/administrador/**",
                                "/productos/**", "/servicios/**", "/sucursales/**",
                                "/agendamientos/**", "/recordatorios/**")
                        .hasAuthority("ROLE_GERENTE")

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/publico/login")
                        .loginProcessingUrl("/publico/acceder")
                        .defaultSuccessUrl("/usuario/login-success", true)
                        .failureUrl("/publico/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/cliente/cerrar"))
                        .logoutSuccessUrl("/publico/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .sessionManagement(sessionManager -> sessionManager
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authenticationProvider(authenticationProvider);

        return http.build();
    }
}