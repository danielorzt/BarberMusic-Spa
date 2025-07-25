package com.sena.barberspa.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sena.barberspa.model.Favorito;
import com.sena.barberspa.model.Producto;
import com.sena.barberspa.model.Servicio;
import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.repository.IFavoritoRepository;

/**
 * Implementación del servicio de Favoritos
 * Gestiona las operaciones CRUD para favoritos de usuarios
 * según el Manual de Roles BarberMusic&Spa
 */
@Service
@Transactional
public class FavoritoServiceImplement implements IFavoritoService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FavoritoServiceImplement.class);
    
    @Autowired
    private IFavoritoRepository favoritoRepository;
    
    @Override
    public Favorito agregarProductoFavorito(Usuario usuario, Producto producto) {
        try {
            // Verificar si ya es favorito
            if (esProductoFavorito(usuario, producto)) {
                LOGGER.warn("Producto {} ya es favorito del usuario {}", producto.getId(), usuario.getId());
                return favoritoRepository.findByUsuarioAndProducto(usuario, producto).orElse(null);
            }
            
            Favorito favorito = new Favorito(usuario, producto);
            Favorito savedFavorito = favoritoRepository.save(favorito);
            
            LOGGER.info("✅ Producto {} agregado a favoritos del usuario {}", 
                       producto.getNombre(), usuario.getNombre());
            
            return savedFavorito;
            
        } catch (Exception e) {
            LOGGER.error("Error agregando producto a favoritos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al agregar producto a favoritos", e);
        }
    }
    
    @Override
    public Favorito agregarServicioFavorito(Usuario usuario, Servicio servicio) {
        try {
            // Verificar si ya es favorito
            if (esServicioFavorito(usuario, servicio)) {
                LOGGER.warn("Servicio {} ya es favorito del usuario {}", servicio.getId(), usuario.getId());
                return favoritoRepository.findByUsuarioAndServicio(usuario, servicio).orElse(null);
            }
            
            Favorito favorito = new Favorito(usuario, servicio);
            Favorito savedFavorito = favoritoRepository.save(favorito);
            
            LOGGER.info("✅ Servicio {} agregado a favoritos del usuario {}", 
                       servicio.getNombre(), usuario.getNombre());
            
            return savedFavorito;
            
        } catch (Exception e) {
            LOGGER.error("Error agregando servicio a favoritos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al agregar servicio a favoritos", e);
        }
    }
    
    @Override
    public boolean removerProductoFavorito(Usuario usuario, Producto producto) {
        try {
            Optional<Favorito> favoritoOpt = favoritoRepository.findByUsuarioAndProducto(usuario, producto);
            
            if (favoritoOpt.isPresent()) {
                favoritoRepository.delete(favoritoOpt.get());
                LOGGER.info("🗑️ Producto {} removido de favoritos del usuario {}", 
                           producto.getNombre(), usuario.getNombre());
                return true;
            }
            
            LOGGER.warn("Producto {} no estaba en favoritos del usuario {}", producto.getId(), usuario.getId());
            return false;
            
        } catch (Exception e) {
            LOGGER.error("Error removiendo producto de favoritos: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean removerServicioFavorito(Usuario usuario, Servicio servicio) {
        try {
            Optional<Favorito> favoritoOpt = favoritoRepository.findByUsuarioAndServicio(usuario, servicio);
            
            if (favoritoOpt.isPresent()) {
                favoritoRepository.delete(favoritoOpt.get());
                LOGGER.info("🗑️ Servicio {} removido de favoritos del usuario {}", 
                           servicio.getNombre(), usuario.getNombre());
                return true;
            }
            
            LOGGER.warn("Servicio {} no estaba en favoritos del usuario {}", servicio.getId(), usuario.getId());
            return false;
            
        } catch (Exception e) {
            LOGGER.error("Error removiendo servicio de favoritos: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean toggleProductoFavorito(Usuario usuario, Producto producto) {
        if (esProductoFavorito(usuario, producto)) {
            return !removerProductoFavorito(usuario, producto); // Retorna false si se removió exitosamente
        } else {
            return agregarProductoFavorito(usuario, producto) != null; // Retorna true si se agregó exitosamente
        }
    }
    
    @Override
    public boolean toggleServicioFavorito(Usuario usuario, Servicio servicio) {
        if (esServicioFavorito(usuario, servicio)) {
            return !removerServicioFavorito(usuario, servicio); // Retorna false si se removió exitosamente
        } else {
            return agregarServicioFavorito(usuario, servicio) != null; // Retorna true si se agregó exitosamente
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean esProductoFavorito(Usuario usuario, Producto producto) {
        return favoritoRepository.existsByUsuarioAndProducto(usuario, producto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean esServicioFavorito(Usuario usuario, Servicio servicio) {
        return favoritoRepository.existsByUsuarioAndServicio(usuario, servicio);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Favorito> obtenerFavoritosUsuario(Usuario usuario) {
        return favoritoRepository.findByUsuarioOrderByCreatedAtDesc(usuario);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Favorito> obtenerProductosFavoritos(Usuario usuario) {
        return favoritoRepository.findByUsuarioAndProductoIsNotNullOrderByCreatedAtDesc(usuario);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Favorito> obtenerServiciosFavoritos(Usuario usuario) {
        return favoritoRepository.findByUsuarioAndServicioIsNotNullOrderByCreatedAtDesc(usuario);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarFavoritosUsuario(Usuario usuario) {
        return favoritoRepository.countByUsuario(usuario);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarProductosFavoritos(Usuario usuario) {
        return favoritoRepository.countByUsuarioAndProductoIsNotNull(usuario);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarServiciosFavoritos(Usuario usuario) {
        return favoritoRepository.countByUsuarioAndServicioIsNotNull(usuario);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosMasPopulares(int limite) {
        try {
            List<Object[]> resultados = favoritoRepository.findProductosMasPopulares();
            return resultados.stream()
                    .limit(limite)
                    .map(resultado -> (Producto) resultado[0])
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Error obteniendo productos más populares: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Servicio> obtenerServiciosMasPopulares(int limite) {
        try {
            List<Object[]> resultados = favoritoRepository.findServiciosMasPopulares();
            return resultados.stream()
                    .limit(limite)
                    .map(resultado -> (Servicio) resultado[0])
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Error obteniendo servicios más populares: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Favorito> buscarPorId(Long id) {
        return favoritoRepository.findById(id);
    }
    
    @Override
    public boolean eliminarFavorito(Long id, Usuario usuario) {
        try {
            Optional<Favorito> favoritoOpt = favoritoRepository.findById(id);
            
            if (favoritoOpt.isPresent()) {
                Favorito favorito = favoritoOpt.get();
                
                // Verificar que el favorito pertenece al usuario
                if (!favorito.getUsuario().getId().equals(usuario.getId())) {
                    LOGGER.warn("Usuario {} intentó eliminar favorito {} que no le pertenece", 
                               usuario.getId(), id);
                    return false;
                }
                
                favoritoRepository.delete(favorito);
                LOGGER.info("🗑️ Favorito {} eliminado por usuario {}", id, usuario.getNombre());
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            LOGGER.error("Error eliminando favorito {}: {}", id, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean limpiarFavoritosUsuario(Usuario usuario) {
        try {
            List<Favorito> favoritos = obtenerFavoritosUsuario(usuario);
            favoritoRepository.deleteAll(favoritos);
            
            LOGGER.info("🧹 Favoritos limpiados para usuario {}: {} favoritos eliminados", 
                       usuario.getNombre(), favoritos.size());
            
            return true;
            
        } catch (Exception e) {
            LOGGER.error("Error limpiando favoritos del usuario {}: {}", usuario.getId(), e.getMessage(), e);
            return false;
        }
    }
}