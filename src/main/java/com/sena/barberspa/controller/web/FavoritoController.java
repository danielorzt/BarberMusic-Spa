package com.sena.barberspa.controller.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sena.barberspa.model.Favorito;
import com.sena.barberspa.model.Producto;
import com.sena.barberspa.model.Servicio;
import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IFavoritoService;
import com.sena.barberspa.service.IProductoService;
import com.sena.barberspa.service.IServiciosService;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador de Favoritos para clientes
 * Permite gestionar productos y servicios favoritos
 * según el Manual de Roles BarberMusic&Spa
 */
@Controller
@RequestMapping("/cliente/favoritos")
public class FavoritoController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FavoritoController.class);
    
    @Autowired
    private IFavoritoService favoritoService;
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private IProductoService productoService;
    
    @Autowired
    private IServiciosService servicioService;
    
    @ModelAttribute
    public void addCommonAttributes(Model model, HttpSession session) {
        try {
            // Intentar obtener usuario de la sesión HTTP primero
            Object userIdObj = session.getAttribute("idUsuario");
            if (userIdObj != null) {
                Long userId = Long.parseLong(userIdObj.toString());
                Optional<Usuario> usuarioOpt = usuarioService.findById(userId);
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("sesion", userId);
                    LOGGER.debug("✅ FavoritoController: Usuario cargado desde sesión HTTP: {} (ID: {})", usuario.getNombre(), userId);
                    return;
                }
            }
            
            // Fallback: intentar obtener usuario desde Spring Security
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                Optional<Usuario> usuarioOpt = usuarioService.findByEmail(auth.getName());
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    // Sincronizar la sesión HTTP con Spring Security
                    session.setAttribute("idUsuario", usuario.getId());
                    session.setAttribute("usuario", usuario);
                    
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("sesion", usuario.getId());
                    LOGGER.debug("✅ FavoritoController: Usuario cargado desde Spring Security: {} (ID: {})", usuario.getNombre(), usuario.getId());
                    return;
                }
            }
            
            // No hay usuario autenticado
            LOGGER.debug("ℹ️ FavoritoController: No hay usuario autenticado en la sesión");
            
        } catch (Exception e) {
            LOGGER.warn("FavoritoController: Error loading user session data: {}", e.getMessage());
        }
    }
    
    /**
     * Mostrar página de favoritos del usuario
     */
    @GetMapping({ "", "/" })
    public String mostrarFavoritos(Model model, HttpSession session) {
        try {
            Usuario usuario = obtenerUsuarioActual(session);
            if (usuario == null) {
                return "redirect:/publico/login";
            }
            
            // Obtener favoritos del usuario
            List<Favorito> todosFavoritos = favoritoService.obtenerFavoritosUsuario(usuario);
            List<Favorito> productosFavoritos = favoritoService.obtenerProductosFavoritos(usuario);
            List<Favorito> serviciosFavoritos = favoritoService.obtenerServiciosFavoritos(usuario);
            
            // Estadísticas
            long totalFavoritos = favoritoService.contarFavoritosUsuario(usuario);
            long totalProductos = favoritoService.contarProductosFavoritos(usuario);
            long totalServicios = favoritoService.contarServiciosFavoritos(usuario);
            
            // Añadir al modelo
            model.addAttribute("usuario", usuario);
            model.addAttribute("sesion", usuario.getId());
            model.addAttribute("todosFavoritos", todosFavoritos);
            model.addAttribute("productosFavoritos", productosFavoritos);
            model.addAttribute("serviciosFavoritos", serviciosFavoritos);
            model.addAttribute("totalFavoritos", totalFavoritos);
            model.addAttribute("totalProductos", totalProductos);
            model.addAttribute("totalServicios", totalServicios);
            
            LOGGER.info("📋 Mostrando favoritos para usuario: {} - Total: {}", 
                       usuario.getNombre(), totalFavoritos);
            
            return "cliente/favoritos";
            
        } catch (Exception e) {
            LOGGER.error("Error mostrando favoritos: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar favoritos");
            return "error/500";
        }
    }
    
    /**
     * Agregar/Remover producto de favoritos (AJAX)
     */
    @PostMapping("/toggle-producto")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleProductoFavorito(
            @RequestParam("productoId") Long productoId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Usuario usuario = obtenerUsuarioActual(session);
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Debes iniciar sesión para usar favoritos");
                return ResponseEntity.status(401).body(response);
            }
            
            Optional<Producto> productoOpt = productoService.get(productoId);
            if (productoOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Producto no encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            
            Producto producto = productoOpt.get();
            boolean esFavorito = favoritoService.toggleProductoFavorito(usuario, producto);
            
            response.put("success", true);
            response.put("esFavorito", esFavorito);
            response.put("message", esFavorito ? 
                "Producto agregado a favoritos" : 
                "Producto removido de favoritos");
            response.put("totalFavoritos", favoritoService.contarFavoritosUsuario(usuario));
            
            LOGGER.info("❤️ Usuario {} {} producto {} {}", 
                       usuario.getNombre(),
                       esFavorito ? "agregó" : "removió",
                       producto.getNombre(),
                       esFavorito ? "a" : "de");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOGGER.error("Error en toggle producto favorito: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Agregar/Remover servicio de favoritos (AJAX)
     */
    @PostMapping("/toggle-servicio")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleServicioFavorito(
            @RequestParam("servicioId") Long servicioId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Usuario usuario = obtenerUsuarioActual(session);
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Debes iniciar sesión para usar favoritos");
                return ResponseEntity.status(401).body(response);
            }
            
            Optional<Servicio> servicioOpt = servicioService.get(servicioId);
            if (servicioOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Servicio no encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            
            Servicio servicio = servicioOpt.get();
            boolean esFavorito = favoritoService.toggleServicioFavorito(usuario, servicio);
            
            response.put("success", true);
            response.put("esFavorito", esFavorito);
            response.put("message", esFavorito ? 
                "Servicio agregado a favoritos" : 
                "Servicio removido de favoritos");
            response.put("totalFavoritos", favoritoService.contarFavoritosUsuario(usuario));
            
            LOGGER.info("❤️ Usuario {} {} servicio {} {}", 
                       usuario.getNombre(),
                       esFavorito ? "agregó" : "removió",
                       servicio.getNombre(),
                       esFavorito ? "a" : "de");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOGGER.error("Error en toggle servicio favorito: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Verificar si un producto es favorito (AJAX)
     */
    @GetMapping("/check-producto/{productoId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkProductoFavorito(
            @PathVariable Long productoId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Usuario usuario = obtenerUsuarioActual(session);
            if (usuario == null) {
                response.put("esFavorito", false);
                response.put("authenticated", false);
                return ResponseEntity.ok(response);
            }
            
            Optional<Producto> productoOpt = productoService.get(productoId);
            if (productoOpt.isEmpty()) {
                response.put("esFavorito", false);
                response.put("error", "Producto no encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean esFavorito = favoritoService.esProductoFavorito(usuario, productoOpt.get());
            
            response.put("esFavorito", esFavorito);
            response.put("authenticated", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOGGER.error("Error verificando producto favorito: {}", e.getMessage(), e);
            response.put("esFavorito", false);
            response.put("error", "Error interno");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Verificar si un servicio es favorito (AJAX)
     */
    @GetMapping("/check-servicio/{servicioId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkServicioFavorito(
            @PathVariable Long servicioId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Usuario usuario = obtenerUsuarioActual(session);
            if (usuario == null) {
                response.put("esFavorito", false);
                response.put("authenticated", false);
                return ResponseEntity.ok(response);
            }
            
            Optional<Servicio> servicioOpt = servicioService.get(servicioId);
            if (servicioOpt.isEmpty()) {
                response.put("esFavorito", false);
                response.put("error", "Servicio no encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean esFavorito = favoritoService.esServicioFavorito(usuario, servicioOpt.get());
            
            response.put("esFavorito", esFavorito);
            response.put("authenticated", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOGGER.error("Error verificando servicio favorito: {}", e.getMessage(), e);
            response.put("esFavorito", false);
            response.put("error", "Error interno");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Eliminar favorito específico
     */
    @PostMapping("/eliminar/{favoritoId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarFavorito(
            @PathVariable Long favoritoId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Usuario usuario = obtenerUsuarioActual(session);
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Debes iniciar sesión");
                return ResponseEntity.status(401).body(response);
            }
            
            boolean eliminado = favoritoService.eliminarFavorito(favoritoId, usuario);
            
            response.put("success", eliminado);
            response.put("message", eliminado ? 
                "Favorito eliminado exitosamente" : 
                "No se pudo eliminar el favorito");
            response.put("totalFavoritos", favoritoService.contarFavoritosUsuario(usuario));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOGGER.error("Error eliminando favorito: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Limpiar todos los favoritos del usuario
     */
    @PostMapping("/limpiar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> limpiarFavoritos(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Usuario usuario = obtenerUsuarioActual(session);
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Debes iniciar sesión");
                return ResponseEntity.status(401).body(response);
            }
            
            boolean limpiado = favoritoService.limpiarFavoritosUsuario(usuario);
            
            response.put("success", limpiado);
            response.put("message", limpiado ? 
                "Todos los favoritos han sido eliminados" : 
                "No se pudieron limpiar los favoritos");
            response.put("totalFavoritos", 0);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOGGER.error("Error limpiando favoritos: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Obtener usuario actual desde la sesión
     */
    private Usuario obtenerUsuarioActual(HttpSession session) {
        try {
            Object userIdObj = session.getAttribute("idUsuario");
            if (userIdObj != null) {
                Long userId = Long.parseLong(userIdObj.toString());
                return usuarioService.findById(userId).orElse(null);
            }
            
            // Fallback: intentar obtener desde Spring Security
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                return usuarioService.findByEmail(auth.getName()).orElse(null);
            }
            
            return null;
            
        } catch (Exception e) {
            LOGGER.error("Error obteniendo usuario actual: {}", e.getMessage(), e);
            return null;
        }
    }
}