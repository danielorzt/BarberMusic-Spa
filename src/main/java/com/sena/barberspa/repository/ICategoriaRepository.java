package com.sena.barberspa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sena.barberspa.model.Categoria;

@Repository
public interface ICategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByActivo(Boolean activo);

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT c FROM Categoria c WHERE c.activo = true")
    List<Categoria> findAllActive();
    
    // Convenience method for backward compatibility
    default List<Categoria> findByEstado(String estado) {
        Boolean activo = "activo".equalsIgnoreCase(estado);
        return findByActivo(activo);
    }
}