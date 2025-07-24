package com.sena.barberspa.service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.model.enums.RolUsuario;
import com.sena.barberspa.repository.IUsuarioRepository;

@Service
public class UsuarioServiceImplement implements IUsuarioService {

	// Objeto de tipo privado que es de tipo repositorio (instancia)
	@Autowired
	private IUsuarioRepository repository;

	@Override
	public Usuario save(Usuario usuario) {
		// TODO Auto-generated method stub
		return repository.save(usuario);
	}

	@Override
	public Optional<Usuario> get(Long id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public void update(Usuario usuario) {
		// TODO Auto-generated method stub
		repository.save(usuario);

	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		repository.deleteById(id);
	}

	@Override
	public Optional<Usuario> findById(Long id) {
		// TODO Auto-generated method stub
		return repository.findById(id);
	}

	@Override
	public Optional<Usuario> findByEmail(String email) {
		// TODO Auto-generated method stub
		return repository.findByEmail(email);
	}

	@Override
	public List<Usuario> findAll() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = repository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("El usuario con email " + username + " no existe."));

		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + mapRoleToSpringRole(usuario.getRol())));

		return new User(usuario.getEmail(), usuario.getPassword(), authorities);
	}

	/**
	 * Mapea el rol del usuario al formato requerido por Spring Security
	 * Actualizado para usar RolUsuario enum
	 */
	private String mapRoleToSpringRole(RolUsuario rol) {
		if (rol == null) {
			return "CLIENTE";
		}
		return rol.getCodigo();
	}
	
	/**
	 * Método de compatibilidad para roles string (deprecado)
	 */
	@Deprecated
	private String mapRoleToSpringRole(String rol) {
		RolUsuario rolEnum = RolUsuario.fromCodigo(rol);
		return mapRoleToSpringRole(rolEnum);
	}
	
	// Métodos adicionales para gestión de roles según Manual BarberMusic&Spa
	
	/**
	 * Buscar usuarios por rol
	 */
	public List<Usuario> findByRol(RolUsuario rol) {
		return repository.findAll().stream()
			.filter(u -> u.getRol() == rol)
			.filter(u -> !u.isDeleted())
			.toList();
	}
	
	/**
	 * Buscar usuarios activos únicamente
	 */
	public List<Usuario> findActiveUsers() {
		return repository.findAll().stream()
			.filter(u -> u.getActivo() && !u.isDeleted())
			.toList();
	}
	
	/**
	 * Promover usuario al siguiente rol en la jerarquía
	 */
	public Usuario promoverUsuario(Long userId) {
		Optional<Usuario> usuarioOpt = repository.findById(userId);
		if (usuarioOpt.isEmpty()) {
			throw new RuntimeException("Usuario no encontrado");
		}
		
		Usuario usuario = usuarioOpt.get();
		RolUsuario nuevoRol = usuario.getRol().getSiguienteRol();
		
		if (nuevoRol != usuario.getRol()) { // Si hay cambio
			usuario.setRol(nuevoRol);
			return repository.save(usuario);
		}
		
		return usuario; // Ya está en el máximo rol
	}
	
	/**
	 * Verificar si un email ya existe (para validaciones)
	 */
	public boolean existsByEmail(String email) {
		return findByEmail(email).isPresent();
	}

}
