package com.sena.barberspa.service;

import java.util.List;
import java.util.Optional;

import com.sena.barberspa.model.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUsuarioService extends UserDetailsService {

	public Usuario save(Usuario usuario);

	public Optional<Usuario> get(Long id);

	public void update(Usuario usuario);

	public void delete(Long id);

	Optional<Usuario> findById(Long id);

	Optional<Usuario> findByEmail(String email);

	List<Usuario> findAll();

}
