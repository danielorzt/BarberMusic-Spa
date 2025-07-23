package com.sena.barberspa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sena.barberspa.model.Personal;
import com.sena.barberspa.model.Sucursal;
import com.sena.barberspa.model.Usuario;

@Repository
public interface IPersonalRepository extends JpaRepository<Personal, Long> {

    List<Personal> findBySucursalAsignada(Sucursal sucursalAsignada);

    Optional<Personal> findByUsuario(Usuario usuario);

    List<Personal> findByActivoEnEmpresa(Boolean activoEnEmpresa);

    @Query("SELECT p FROM Personal p WHERE p.sucursalAsignada.id = :sucursalId AND p.activoEnEmpresa = true")
    List<Personal> findBySucursalIdAndEstadoActivo(@Param("sucursalId") Long sucursalId);

    @Query("SELECT p FROM Personal p WHERE p.usuario.id = :usuarioId")
    Optional<Personal> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}