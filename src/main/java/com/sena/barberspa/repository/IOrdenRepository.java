package com.sena.barberspa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sena.barberspa.model.Orden;
import com.sena.barberspa.model.Usuario;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Long> {
	List<Orden> findByClienteUsuario(Usuario clienteUsuario);
	
	// Convenience method for backward compatibility
	default List<Orden> findByUsuario(Usuario usuario) {
		return findByClienteUsuario(usuario);
	}
}
