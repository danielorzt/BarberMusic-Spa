package com.sena.barberspa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sena.barberspa.model.Usuario;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByEmail(String email);

	List<Usuario> findByRol(String rol);

	@Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.activo = true")
	List<Usuario> findByRolAndEstadoActivo(@Param("rol") String rol);

	@Query("SELECT u FROM Usuario u WHERE u.rol IN ('EMPLEADO', 'ADMIN_SUCURSAL', 'ADMIN_GENERAL')")
	List<Usuario> findAllStaff();

	@Query("SELECT u FROM Usuario u WHERE u.rol = 'CLIENTE'")
	List<Usuario> findAllClientes();
}
