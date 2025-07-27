package com.sena.barberspa.controller.web;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.barberspa.model.Producto;
import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IProductoService;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador para vistas de productos individuales
 * Maneja tanto vistas públicas como de cliente autenticado
 */
@Controller
public class ProductoClienteController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductoClienteController.class);
    
    @Autowired
    private IProductoService productoService;
    
    @Autowired
    private IUsuarioService usuarioService;
    
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
                    model.addAttribute("isAuthenticated", true);
                    LOGGER.debug("✅ Usuario autenticado: {} (ID: {})", usuario.getNombre(), userId);
                    return;
                }
            }
            
            // Fallback: intentar obtener desde Spring Security
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
                    model.addAttribute("isAuthenticated", true);
                    LOGGER.debug("✅ Usuario sincronizado desde Spring Security: {} (ID: {})", usuario.getNombre(), usuario.getId());
                    return;
                }
            }
            
            // No hay usuario autenticado
            model.addAttribute("isAuthenticated", false);
            LOGGER.debug("ℹ️ Usuario no autenticado");
            
        } catch (Exception e) {
            LOGGER.warn("Error loading user session data: {}", e.getMessage());
            model.addAttribute("isAuthenticated", false);
        }
    }
    
    /**
     * Vista individual de producto para usuarios públicos y autenticados
     */
    @GetMapping("/productoHome/{id}")
    public String verProducto(@PathVariable Long id, Model model, HttpSession session) {
        try {
            LOGGER.info("🛍️ Mostrando producto ID: {}", id);
            
            Optional<Producto> productoOpt = productoService.get(id);
            if (productoOpt.isEmpty()) {
                LOGGER.warn("❌ Producto no encontrado: {}", id);
                return "redirect:/home/productosVista";
            }
            
            Producto producto = productoOpt.get();
            model.addAttribute("producto", producto);
            
            // Verificar si usuario está autenticado para mostrar diferentes opciones
            boolean isAuthenticated = session.getAttribute("idUsuario") != null;
            model.addAttribute("isAuthenticated", isAuthenticated);
            
            if (isAuthenticated) {
                // Cliente autenticado - mostrar vista con opciones de compra
                return "cliente/producto-detalle";
            } else {
                // Usuario público - mostrar vista básica
                return "publico/producto-detalle";
            }
            
        } catch (Exception e) {
            LOGGER.error("❌ Error mostrando producto {}: {}", id, e.getMessage(), e);
            return "redirect:/home/productosVista";
        }
    }
    
    /**
     * Agregar producto al carrito (solo clientes autenticados)
     */
    @PostMapping("/cliente/agregar-carrito")
    public String agregarAlCarrito(
            @RequestParam("productoId") Long productoId,
            @RequestParam(value = "cantidad", defaultValue = "1") Integer cantidad,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Verificar autenticación
            Object userIdObj = session.getAttribute("idUsuario");
            if (userIdObj == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para agregar productos al carrito");
                return "redirect:/publico/login";
            }
            
            // Verificar que el producto existe
            Optional<Producto> productoOpt = productoService.get(productoId);
            if (productoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/home/productosVista";
            }
            
            Producto producto = productoOpt.get();
            
            // TODO: Implementar lógica de carrito de compras
            // Por ahora, simulamos añadir al carrito
            LOGGER.info("🛒 Agregando al carrito: {} (Cantidad: {}) para usuario ID: {}", 
                       producto.getNombre(), cantidad, userIdObj);
            
            redirectAttributes.addFlashAttribute("success", 
                String.format("Producto '%s' agregado al carrito exitosamente", producto.getNombre()));
            
            return "redirect:/productoHome/" + productoId;
            
        } catch (Exception e) {
            LOGGER.error("❌ Error agregando producto al carrito: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al agregar producto al carrito");
            return "redirect:/productoHome/" + productoId;
        }
    }
    
    /**
     * Comprar producto directamente (solo clientes autenticados)
     */
    @PostMapping("/cliente/comprar-producto")
    public String comprarProducto(
            @RequestParam("productoId") Long productoId,
            @RequestParam(value = "cantidad", defaultValue = "1") Integer cantidad,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Verificar autenticación
            Object userIdObj = session.getAttribute("idUsuario");
            if (userIdObj == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para comprar productos");
                return "redirect:/publico/login";
            }
            
            // Verificar que el producto existe
            Optional<Producto> productoOpt = productoService.get(productoId);
            if (productoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/home/productosVista";
            }
            
            Producto producto = productoOpt.get();
            
            // TODO: Implementar lógica de compra directa
            // Por ahora, redirigimos a una página de checkout simulada
            LOGGER.info("💳 Comprando producto: {} (Cantidad: {}) para usuario ID: {}", 
                       producto.getNombre(), cantidad, userIdObj);
            
            redirectAttributes.addFlashAttribute("success", 
                "Redirigiendo al proceso de pago...");
            
            // TODO: Redirigir a página de checkout real
            return "redirect:/cliente/checkout?productoId=" + productoId + "&cantidad=" + cantidad;
            
        } catch (Exception e) {
            LOGGER.error("❌ Error comprando producto: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error en el proceso de compra");
            return "redirect:/productoHome/" + productoId;
        }
    }
}