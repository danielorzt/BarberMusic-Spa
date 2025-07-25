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
                                                                "/",
                                                                "/home",
                                                                "/home/",
                                                                "/home/productosVista",
                                                                "/home/serviciosVista",
                                                                "/usuario/login",
                                                                "/usuario/login-success",
                                                                "/usuario/registro",
                                                                "/usuario/save",
                                                                "/usuario/acceder",
                                                                "/usuario/resetPassword",
                                                                "/usuario/cambiarPassword",
                                                                "/usuario/saveNewPassword",
                                                                "/usuario/token-invalido",
                                                                "/usuario/create-test-user",
                                                                "/usuario/test-login",
                                                                "/assets/**",
                                                                "/img/**",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/vendor/**",
                                                                "/swagger-ui/**",
                                                                "/api-docs/**",
                                                                "/debug/**",
                                                                "/error",
                                                                "/403")
                                                .permitAll()

                                                // Rutas específicas de cliente autenticado
                                                .requestMatchers("/usuario/perfil", "/usuario/compras/**",
                                                                "/usuario/favoritos/**", "/cart/**", "/getCart",
                                                                "/home/test-home", "/home/full", "/home/mantenimiento")
                                                .hasAuthority("ROLE_CLIENTE")

                                                // Rutas de empleado y superiores
                                                .requestMatchers("/empleado/**")
                                                .hasAnyAuthority("ROLE_EMPLEADO", "ROLE_ADMIN_SUCURSAL", "ROLE_GERENTE")

                                                // Rutas de admin sucursal y superiores
                                                .requestMatchers("/admin-sucursal/**")
                                                .hasAnyAuthority("ROLE_ADMIN_SUCURSAL", "ROLE_GERENTE")

                                                // Rutas exclusivas de gerente
                                                .requestMatchers("/administrador/**").hasAuthority("ROLE_GERENTE")

                                                // Todo lo demás requiere autenticación
                                                .anyRequest().authenticated())
                                .formLogin(formLogin -> formLogin
                                                .loginPage("/usuario/login")
                                                .loginProcessingUrl("/usuario/acceder")
                                                .successHandler((request, response, authentication) -> {
                                                    // Custom success handler para sincronizar sesión INMEDIATAMENTE
                                                    try {
                                                        String email = authentication.getName();
                                                        
                                                        // Obtener la sesión HTTP
                                                        var session = request.getSession(true);
                                                        
                                                        // Obtener ApplicationContext para acceder a servicios
                                                        var ctx = org.springframework.web.context.support.WebApplicationContextUtils
                                                            .getRequiredWebApplicationContext(request.getServletContext());
                                                        var usuarioService = ctx.getBean(com.sena.barberspa.service.IUsuarioService.class);
                                                        
                                                        // Buscar usuario y establecer sesión INMEDIATAMENTE
                                                        var usuarioOpt = usuarioService.findByEmail(email);
                                                        if (usuarioOpt.isPresent()) {
                                                            var usuario = usuarioOpt.get();
                                                            session.setAttribute("idUsuario", usuario.getId());
                                                            session.setAttribute("usuario", usuario);
                                                            System.out.println("✅ SUCCESS HANDLER: Sesión establecida para " + usuario.getNombre() + " (ID: " + usuario.getId() + ")");
                                                        }
                                                        
                                                        response.sendRedirect("/usuario/login-success");
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        response.sendRedirect("/usuario/login?error=true");
                                                    }
                                                })
                                                .failureUrl("/usuario/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/usuario/cerrar"))
                                                .logoutSuccessUrl("/usuario/login?logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .sessionManagement(sessionManager -> sessionManager
                                                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                                .authenticationProvider(authenticationProvider);

                return http.build();
        }
}