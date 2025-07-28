package com.sena.barberspa.controller.web;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sena.barberspa.model.Producto;
import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IProductoService;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PublicProductoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicProductoController.class);

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IUsuarioService usuarioService;

    @ModelAttribute
    public void addCommonAttributes(Model model, HttpSession session) {
        try {
            // Inicializar valores por defecto
            model.addAttribute("sesion", null);
            model.addAttribute("usuario", null);
            model.addAttribute("isAuthenticated", false);

            // Intentar obtener usuario de la sesión HTTP primero
            Object userIdObj = session.getAttribute("idUsuario");
            if (userIdObj != null) {
                try {
                    Long userId = Long.parseLong(userIdObj.toString());
                    Optional<Usuario> usuarioOpt = usuarioService.findById(userId);
                    if (usuarioOpt.isPresent()) {
                        Usuario usuario = usuarioOpt.get();
                        model.addAttribute("usuario", usuario);
                        model.addAttribute("sesion", userId);
                        model.addAttribute("isAuthenticated", true);
                        LOGGER.debug("✅ PublicProductoController: Usuario cargado desde sesión HTTP: {} (ID: {})",
                                usuario.getNombre(), userId);
                        return;
                    } else {
                        // Usuario no encontrado en BD, limpiar sesión
                        session.removeAttribute("idUsuario");
                        session.removeAttribute("usuario");
                        LOGGER.warn("Usuario no encontrado en BD, sesión limpiada para ID: {}", userId);
                    }
                } catch (Exception e) {
                    LOGGER.warn("Error procesando sesión HTTP: {}", e.getMessage());
                    // Limpiar sesión corrupta
                    session.removeAttribute("idUsuario");
                    session.removeAttribute("usuario");
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
                    model.addAttribute("isAuthenticated", true);
                    LOGGER.debug("✅ PublicProductoController: Usuario cargado desde Spring Security: {} (ID: {})",
                            usuario.getNombre(), usuario.getId());
                    return;
                }
            }

            // No hay usuario autenticado
            LOGGER.debug("ℹ️ PublicProductoController: No hay usuario autenticado en la sesión");

        } catch (Exception e) {
            LOGGER.warn("PublicProductoController: Error loading user session data: {}", e.getMessage());
            // Asegurar valores por defecto seguros
            model.addAttribute("sesion", null);
            model.addAttribute("usuario", null);
            model.addAttribute("isAuthenticated", false);
        }
    }

    @GetMapping("/productosVista")
    public String productosVista(Model model) {
        try {
            LOGGER.info("Loading productos vista page...");
            List<Producto> productos = productoService.findAll();
            model.addAttribute("productos", productos);
            LOGGER.info("Productos vista loaded successfully with {} products", productos.size());
            return "public/productosVista";
        } catch (Exception e) {
            LOGGER.error("Error loading productos vista: {}", e.getMessage(), e);
            model.addAttribute("error", "Error cargando productos: " + e.getMessage());
            return "public/productosVista";
        }
    }

    @PostMapping("/searchProductos")
    public String searchProducto(@RequestParam String nombreproducto, Model model) {
        try {
            LOGGER.info("Searching productos with term: {}", nombreproducto);
            List<Producto> productos = productoService.findAll().stream()
                    .filter(p -> p.getNombreproducto().toUpperCase().contains(nombreproducto.toUpperCase()))
                    .collect(Collectors.toList());
            model.addAttribute("productos", productos);
            model.addAttribute("searchTerm", nombreproducto);
            LOGGER.info("Search completed, found {} productos", productos.size());
            return "public/productosVista";
        } catch (Exception e) {
            LOGGER.error("Error searching productos: {}", e.getMessage(), e);
            model.addAttribute("error", "Error en la búsqueda: " + e.getMessage());
            return "public/productosVista";
        }
    }
}