package com.sena.barberspa.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sena.barberspa.config.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SpringBootSecurity {

	@Autowired
	private CustomOAuth2UserService customOAuth2UserService;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher("/api/**")
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(authz -> authz
						.requestMatchers("/api/v1/auth/login").permitAll()
						.requestMatchers("/api/v1/productos/**", "/api/v1/servicios/**", "/api/v1/sucursales/**")
						.permitAll()
						.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthenticationFilter,
						org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(authz -> authz
						// PRIORIDAD 1: Permitir todos los recursos estáticos y páginas públicas
						.requestMatchers(
								"/assets/**", "/vendor/**", "/images/**", "/node_modules/**",
								"/", "/home", "/mantenimiento",
								"/usuario/login", "/usuario/registro", "/usuario/save", "/usuario/create-admin",
								"/usuario/resetPassword", "/usuario/cambiarPassword",
								"/usuario/saveNewPassword", "/usuario/token-invalido",
								"/serviciosVista", "/servicioHome/**",
								"/productosVista", "/productoHome/**", "/searchProductos",
								"/error/**", "/login-error",
								// Swagger UI endpoints
								"/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**",
								// Rutas públicas de visualización de productos y servicios
								"/productos", "/productos/search", "/servicios", "/servicios/search",
								"/servicios/servicioHome/**", "/sucursales")
						.permitAll()
						// PRIORIDAD 2: Reglas para roles
						.requestMatchers("/debug/**", "/test/**")
						.permitAll()
						// Panel de administración - Solo para GERENTE
						.requestMatchers("/administrador/**", "/admin/**")
						.hasRole("GERENTE")
						// Gestión administrativa de productos, servicios, sucursales - Admins y
						// Empleados
						.requestMatchers("/productos/create", "/productos/save", "/productos/edit/**",
								"/productos/update", "/productos/delete/**",
								"/servicios/create", "/servicios/save", "/servicios/edit/**",
								"/servicios/update", "/servicios/delete/**",
								"/sucursales/create", "/sucursales/save", "/sucursales/edit/**",
								"/sucursales/update", "/sucursales/delete/**",
								"/recordatorios/**")
						.hasAnyRole("GERENTE", "ADMIN_SUCURSAL", "EMPLEADO")
						// Funciones de usuario, agendamientos y pagos - Todos los roles autenticados
						.requestMatchers("/usuario/**", "/agendamientos/**", "/pagos/**", "/mercadopago/**",
								"/paypal/**")
						.hasAnyRole("CLIENTE", "EMPLEADO", "ADMIN_SUCURSAL", "GERENTE")
						// Secciones de empleado - Empleados y Administradores
						.requestMatchers("/empleado/**")
						.hasAnyRole("EMPLEADO", "ADMIN_SUCURSAL", "GERENTE")
						// PRIORIDAD 3: Cualquier otra petición web requiere autenticación
						.anyRequest().authenticated())
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/usuario/login")
						.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
						.successHandler(successHandler())
						.failureUrl("/error/oauth2_error?message=AuthenticationFailed"))
				.logout(logout -> logout
						.logoutUrl("/usuario/cerrar")
						.logoutSuccessUrl("/?logout")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.permitAll())
				.exceptionHandling(exceptions -> exceptions
						.accessDeniedPage("/error/403"));

		return http.build();
	}

	@Bean
	public AuthenticationSuccessHandler successHandler() {
		SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
		handler.setUseReferer(false);
		handler.setDefaultTargetUrl("/usuario/acceder");
		return handler;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*")); // Considera restringir esto en producción
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Auth-Token"));
		configuration.setExposedHeaders(Arrays.asList("X-Auth-Token"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailServiceImplement();
	}
}