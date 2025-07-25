package com.sena.barberspa.service;

import java.util.List;
import java.util.Optional;

import com.sena.barberspa.model.Favorito;
import com.sena.barberspa.model.Producto;
import com.sena.barberspa.model.Servicio;
import com.sena.barberspa.model.Usuario;

/**
 * Interfaz de servicio para gestión de Favoritos
 * Define las operaciones para que los clientes gestionen sus favoritos
 * según el Manual de Roles BarberMusic&Spa
 */
public interface IFavoritoService {
    
    /**
     * Agregar producto a favoritos
     */
    Favorito agregarProductoFavorito(Usuario usuario, Producto producto);
    
    /**
     * Agregar servicio a favoritos
     */
    Favorito agregarServicioFavorito(Usuario usuario, Servicio servicio);
    
    /**
     * Remover producto de favoritos
     */
    boolean removerProductoFavorito(Usuario usuario, Producto producto);
    
    /**
     * Remover servicio de favoritos
     */
    boolean removerServicioFavorito(Usuario usuario, Servicio servicio);
    
    /**
     * Alternar estado de favorito para producto (agregar si no existe, remover si existe)
     */
    boolean toggleProductoFavorito(Usuario usuario, Producto producto);
    
    /**
     * Alternar estado de favorito para servicio (agregar si no existe, remover si existe)
     */
    boolean toggleServicioFavorito(Usuario usuario, Servicio servicio);
    
    /**
     * Verificar si un producto es favorito del usuario
     */
    boolean esProductoFavorito(Usuario usuario, Producto producto);
    
    /**
     * Verificar si un servicio es favorito del usuario
     */
    boolean esServicioFavorito(Usuario usuario, Servicio servicio);
    
    /**
     * Obtener todos los favoritos de un usuario
     */
    List<Favorito> obtenerFavoritosUsuario(Usuario usuario);
    
    /**
     * Obtener favoritos de productos de un usuario
     */
    List<Favorito> obtenerProductosFavoritos(Usuario usuario);
    
    /**
     * Obtener favoritos de servicios de un usuario
     */
    List<Favorito> obtenerServiciosFavoritos(Usuario usuario);
    
    /**
     * Contar favoritos totales de un usuario
     */
    long contarFavoritosUsuario(Usuario usuario);
    
    /**
     * Contar favoritos de productos de un usuario
     */
    long contarProductosFavoritos(Usuario usuario);
    
    /**
     * Contar favoritos de servicios de un usuario
     */
    long contarServiciosFavoritos(Usuario usuario);
    
    /**
     * Obtener productos más populares (más agregados a favoritos)
     */
    List<Producto> obtenerProductosMasPopulares(int limite);
    
    /**
     * Obtener servicios más populares (más agregados a favoritos)
     */
    List<Servicio> obtenerServiciosMasPopulares(int limite);
    
    /**
     * Buscar favorito específico por ID
     */
    Optional<Favorito> buscarPorId(Long id);
    
    /**
     * Eliminar favorito por ID
     */
    boolean eliminarFavorito(Long id, Usuario usuario);
    
    /**
     * Limpiar todos los favoritos de un usuario
     */
    boolean limpiarFavoritosUsuario(Usuario usuario);
}