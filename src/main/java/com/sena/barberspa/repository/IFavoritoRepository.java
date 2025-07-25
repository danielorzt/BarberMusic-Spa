package com.sena.barberspa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sena.barberspa.model.Favorito;
import com.sena.barberspa.model.Producto;
import com.sena.barberspa.model.Servicio;
import com.sena.barberspa.model.Usuario;

/**
 * Repositorio para gestión de Favoritos
 * Permite a los clientes gestionar sus productos y servicios favoritos
 * según el Manual de Roles BarberMusic&Spa
 */
@Repository
public interface IFavoritoRepository extends JpaRepository<Favorito, Long> {
    
    /**
     * Buscar todos los favoritos de un usuario
     */
    List<Favorito> findByUsuarioOrderByCreatedAtDesc(Usuario usuario);
    
    /**
     * Buscar favoritos de productos de un usuario
     */
    List<Favorito> findByUsuarioAndProductoIsNotNullOrderByCreatedAtDesc(Usuario usuario);
    
    /**
     * Buscar favoritos de servicios de un usuario
     */
    List<Favorito> findByUsuarioAndServicioIsNotNullOrderByCreatedAtDesc(Usuario usuario);
    
    /**
     * Verificar si un producto es favorito del usuario
     */
    Optional<Favorito> findByUsuarioAndProducto(Usuario usuario, Producto producto);
    
    /**
     * Verificar si un servicio es favorito del usuario
     */
    Optional<Favorito> findByUsuarioAndServicio(Usuario usuario, Servicio servicio);
    
    /**
     * Verificar si existe un favorito específico
     */
    boolean existsByUsuarioAndProducto(Usuario usuario, Producto producto);
    
    /**
     * Verificar si existe un favorito específico para servicio
     */
    boolean existsByUsuarioAndServicio(Usuario usuario, Servicio servicio);
    
    /**
     * Contar favoritos de un usuario
     */
    long countByUsuario(Usuario usuario);
    
    /**
     * Contar favoritos de productos de un usuario
     */
    long countByUsuarioAndProductoIsNotNull(Usuario usuario);
    
    /**
     * Contar favoritos de servicios de un usuario
     */
    long countByUsuarioAndServicioIsNotNull(Usuario usuario);
    
    /**
     * Eliminar favorito de producto
     */
    void deleteByUsuarioAndProducto(Usuario usuario, Producto producto);
    
    /**
     * Eliminar favorito de servicio
     */
    void deleteByUsuarioAndServicio(Usuario usuario, Servicio servicio);
    
    /**
     * Query personalizada para obtener productos más populares (más añadidos a favoritos)
     */
    @Query("SELECT f.producto, COUNT(f) as total FROM Favorito f " +
           "WHERE f.producto IS NOT NULL " +
           "GROUP BY f.producto " +
           "ORDER BY total DESC")
    List<Object[]> findProductosMasPopulares();
    
    /**
     * Query personalizada para obtener servicios más populares (más añadidos a favoritos)
     */
    @Query("SELECT f.servicio, COUNT(f) as total FROM Favorito f " +
           "WHERE f.servicio IS NOT NULL " +
           "GROUP BY f.servicio " +
           "ORDER BY total DESC")
    List<Object[]> findServiciosMasPopulares();
    
    /**
     * Buscar favoritos por usuario ID (útil para servicios)
     */
    @Query("SELECT f FROM Favorito f WHERE f.usuario.id = :usuarioId ORDER BY f.createdAt DESC")
    List<Favorito> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}