package com.sena.barberspa.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sena.barberspa.model.Usuario;

import jakarta.servlet.http.HttpSession;

@Service
public class UserDetailServiceImplement implements UserDetailsService {

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private BCryptPasswordEncoder bCrypt;

	@Autowired
	HttpSession session;

	private Logger log = LoggerFactory.getLogger(UserDetailServiceImplement.class);

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Intentando autenticar usuario con email: {}", username);
		Optional<Usuario> optionalUser = usuarioService.findByEmail(username);
		if (optionalUser.isPresent()) {
			Usuario usuario = optionalUser.get();
			log.info("Usuario encontrado: {} con ID: {}", usuario.getNombre(), usuario.getId());
			log.info("Rol del usuario: {}", usuario.getRol());

			// Establecer la sesión
			session.setAttribute("idUsuario", usuario.getId());

			String mappedRole = mapRoleToSpringRole(usuario.getRol());
			log.info("Rol mapeado para Spring Security: {}", mappedRole);

			return User.builder()
					.username(usuario.getEmail()) // Usar email como username
					.password(usuario.getPassword())
					.roles(mappedRole)
					.build();
		} else {
			log.warn("Usuario no encontrado con email: {}", username);
			throw new UsernameNotFoundException("Usuario no encontrado con email: " + username);
		}
	}

	private String mapRoleToSpringRole(String rol) {
		if (rol == null) {
			return "CLIENTE";
		}
		switch (rol.toUpperCase()) {
			case "CLIENTE":
				return "CLIENTE";
			case "EMPLEADO":
				return "EMPLEADO";
			case "ADMIN_SUCURSAL":
				return "ADMIN_SUCURSAL";
			case "GERENTE":
				return "GERENTE";
			default:
				return "CLIENTE";
		}
	}
}