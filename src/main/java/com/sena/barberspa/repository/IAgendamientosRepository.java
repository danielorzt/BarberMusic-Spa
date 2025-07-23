package com.sena.barberspa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sena.barberspa.model.Agendamiento;
import com.sena.barberspa.model.Usuario;

@Repository
public interface IAgendamientosRepository extends JpaRepository<Agendamiento, Long> {
    List<Agendamiento> findByClienteUsuario(Usuario clienteUsuario);
    
    // Convenience method for backward compatibility
    default List<Agendamiento> findByUsuario(Usuario usuario) {
        return findByClienteUsuario(usuario);
    }
}