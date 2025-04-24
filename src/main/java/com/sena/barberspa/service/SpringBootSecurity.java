package com.sena.barberspa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SpringBootSecurity {

	@Autowired
	private UserDetailServiceImplement userDetailService;

	@Autowired
	private CustomOAuth2UserService customOAuth2UserService;

	@Bean
	public AuthenticationSuccessHandler successHandler() {
		SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
		handler.setDefaultTargetUrl("/usuario/acceder");
		handler.setUseReferer(true);
		return handler;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeRequests(authorize -> authorize
						// Rutas de la API REST que pueden ser accedidas sin autenticación
						.requestMatchers("/api/v1/auth/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/servicios/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/sucursales/**").permitAll()
						// Rutas de la API REST que requieren autenticación
						.requestMatchers("/api/v1/agendamientos/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/v1/productos/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v1/productos/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/v1/servicios/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v1/servicios/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/servicios/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/v1/sucursales/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v1/sucursales/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/sucursales/**").hasRole("ADMIN")
						// Rutas de la aplicación web
						.requestMatchers("/administrador/**").hasRole("ADMIN")
						.requestMatchers("/productos/**").hasRole("ADMIN")
						.requestMatchers("/recordatorios/**").hasRole("ADMIN")
						// Permitir acceso público a la ruta de agendamientos
						.requestMatchers("/agendamientos/save", "/agendamientos/**").permitAll()
						// Permitir acceso a API de servicios para modal de citas
						.requestMatchers("/servicios/sucursales/json").permitAll()
						.requestMatchers("/usuario/resetPassword", "/usuario/cambiarPassword",
								"/usuario/saveNewPassword", "/usuario/token-invalido").permitAll()
						.requestMatchers("/assets/**", "/assetsADMINS/**", "/css/**", "/js/**", "/images/**",
								"/", "/serviciosVista", "/servicioHome/**", "/productosVista", "/productoHome/**",
								"/usuario/registro", "/usuario/login", "/usuario/save", "/error/**").permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(formLogin -> formLogin
						.loginPage("/usuario/login")
						.permitAll()
						.defaultSuccessUrl("/usuario/acceder")
				)
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/usuario/login")
						.userInfoEndpoint(userInfo -> userInfo
								.userService(customOAuth2UserService)
						)
						.successHandler(successHandler())
						.failureUrl("/error/oauth2_error")
				)
				.logout(logout -> logout
						.logoutSuccessUrl("/")
						.permitAll()
				)
				.exceptionHandling(exceptionHandling -> exceptionHandling
						.accessDeniedPage("/error/403")
				)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(csrf -> csrf.disable());

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*")); // Permitir todas las origenes para desarrollo
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
		configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);
		return source;
	}
}