package com.sena.barberspa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sena.barberspa.model.Especialidad;

@Repository
public interface IEspecialidadRepository extends JpaRepository<Especialidad, Long> {

    List<Especialidad> findByActivo(Boolean activo);

    List<Especialidad> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT e FROM Especialidad e WHERE e.activo = true")
    List<Especialidad> findAllActive();
    
    // Convenience method for backward compatibility
    default List<Especialidad> findByEstado(String estado) {
        Boolean activo = "activo".equalsIgnoreCase(estado);
        return findByActivo(activo);
    }
}