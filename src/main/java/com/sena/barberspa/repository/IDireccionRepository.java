package com.sena.barberspa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sena.barberspa.model.Direccion;

@Repository
public interface IDireccionRepository extends JpaRepository<Direccion, Long> {

    @Query("SELECT d FROM Direccion d WHERE d.direccionableType = :type AND d.direccionableId = :id")
    List<Direccion> findByDireccionableTypeAndId(@Param("type") String type, @Param("id") Long id);

    List<Direccion> findByCiudad(String ciudad);

    List<Direccion> findByEstado(String estado);
    
    List<Direccion> findByCodigoPostal(String codigoPostal);
    
    List<Direccion> findByEsPredeterminada(Boolean esPredeterminada);
}