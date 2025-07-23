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

	private String mapRoleToSpringRole(String rol) {
		if (rol == null) {
			return "CLIENTE";
		}
		switch (rol.toUpperCase()) {
			case "USER":
				return "CLIENTE";
			case "ADMIN":
				return "GERENTE";
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
