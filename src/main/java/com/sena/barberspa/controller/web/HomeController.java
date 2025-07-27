package com.sena.barberspa.controller.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.barberspa.model.*;
import com.sena.barberspa.service.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping({ "/", "/home" })
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
    private static final String SESSION_USER_ID = "idUsuario";
    private static final String REDIRECT_LOGIN = "redirect:/usuario/login";

    @Autowired
    private IProductoService productoService;
    @Autowired
    private IServiciosService servicioService;
    @Autowired
    private ISucursalesService sucursalService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IOrdenService ordenService;
    @Autowired
    private IDetalleOrdenService detalleOrdenService;
    @Autowired
    private IAgendamientosService agendamientosService;

    private List<DetalleOrden> detalles = new ArrayList<>();
    private Orden orden = new Orden();

    @ModelAttribute
    public void addCommonAttributes(Model model, HttpSession session) {
        try {
            // Inicializar valores por defecto
            model.addAttribute("sesion", null);
            model.addAttribute("usuario", null);
            model.addAttribute("isAuthenticated", false);

            // Intentar obtener usuario de la sesión HTTP
            Object userIdObj = session.getAttribute(SESSION_USER_ID);
            
            if (userIdObj != null) {
                try {
                    Long userId = Long.parseLong(userIdObj.toString());
                    Optional<Usuario> usuarioOpt = usuarioService.findById(userId);
                    if (usuarioOpt.isPresent()) {
                        Usuario usuario = usuarioOpt.get();
                        model.addAttribute("usuario", usuario);
                        model.addAttribute("sesion", userId);
                        model.addAttribute("isAuthenticated", true);
                        LOGGER.debug("✅ Usuario cargado desde sesión HTTP: {}", usuario.getNombre());
                        return;
                    }
                } catch (Exception e) {
                    LOGGER.warn("Error procesando sesión HTTP: {}", e.getMessage());
                }
            }

            // Fallback: intentar desde Spring Security
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                    Optional<Usuario> usuarioOpt = usuarioService.findByEmail(auth.getName());
                    if (usuarioOpt.isPresent()) {
                        Usuario usuario = usuarioOpt.get();
                        session.setAttribute(SESSION_USER_ID, usuario.getId());
                        
                        model.addAttribute("usuario", usuario);
                        model.addAttribute("sesion", usuario.getId());
                        model.addAttribute("isAuthenticated", true);
                        LOGGER.debug("✅ Usuario sincronizado desde Spring Security: {}", usuario.getNombre());
                        return;
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Error procesando Spring Security: {}", e.getMessage());
            }

            LOGGER.debug("ℹ️ Usuario no autenticado - modo público");

        } catch (Exception e) {
            LOGGER.error("💥 Error crítico en ModelAttribute: {}", e.getMessage());
            // Asegurar valores por defecto seguros
            model.addAttribute("sesion", null);
            model.addAttribute("usuario", null);
            model.addAttribute("isAuthenticated", false);
        }
    }

    // Método principal y único para la página de inicio
    @GetMapping({ "", "/" })
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String home(Model model) {
        try {
            LOGGER.info("🏠 HOME: Iniciando carga de datos para la página principal...");

            // Cargar productos y filtrar activos
            List<Producto> productos = productoService.findAll().stream()
                    .filter(p -> p.getActivo() != null && p.getActivo())
                    .collect(Collectors.toList());
            model.addAttribute("productos", productos);

            // Cargar servicios y filtrar activos
            List<Servicio> servicios = servicioService.findAll().stream()
                    .filter(s -> s.getActivo() != null && s.getActivo())
                    .collect(Collectors.toList());
            model.addAttribute("servicios", servicios);

            // Cargar sucursales y filtrar activos
            List<Sucursal> sucursales = sucursalService.findAll().stream()
                    .filter(s -> s.getActivo() != null && s.getActivo())
                    .collect(Collectors.toList());
            model.addAttribute("sucursales", sucursales);

            LOGGER.info("✅ HOME: Datos cargados - {} productos, {} servicios, {} sucursales",
                    productos.size(), servicios.size(), sucursales.size());

            return "publico/home";

        } catch (Exception e) {
            LOGGER.error("💥 HOME: Error crítico al cargar datos para la página principal: {}", e.getMessage(), e);
            // Fallback seguro con listas vacías en caso de error
            model.addAttribute("productos", new ArrayList<>());
            model.addAttribute("servicios", new ArrayList<>());
            model.addAttribute("sucursales", new ArrayList<>());
            model.addAttribute("error", "No se pudieron cargar los datos de la página. Por favor, intente más tarde.");
            return "publico/home";
        }
    }

    @GetMapping("/serviciosVista")
    public String serviciosVista(Model model) {
        try {
            LOGGER.info("🌿 Cargando página de vista de servicios...");
            List<Servicio> servicios = servicioService.findAll().stream()
                    .filter(s -> s.getActivo() != null && s.getActivo())
                    .collect(Collectors.toList());

            model.addAttribute("servicios", servicios);
            LOGGER.info("✅ Vista de servicios cargada con {} servicios activos.", servicios.size());

            return "publico/serviciosVista";
        } catch (Exception e) {
            LOGGER.error("❌ Error cargando la vista de servicios: {}", e.getMessage(), e);
            model.addAttribute("error", "No se pudieron cargar los servicios. Intente más tarde.");
            model.addAttribute("servicios", new ArrayList<>());
            return "publico/serviciosVista";
        }
    }

    @GetMapping("/productosVista")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String productosVista(Model model) {
        try {
            List<Producto> productos = productoService.findAll().stream()
                    .filter(p -> p.getActivo() != null && p.getActivo())
                    .collect(Collectors.toList());
            model.addAttribute("productos", productos);
            LOGGER.info("✅ ProductosVista: {} productos activos cargados", productos.size());
        } catch (Exception e) {
            LOGGER.error("❌ Error loading products: {}", e.getMessage(), e);
            model.addAttribute("productos", new ArrayList<>());
        }
        return "publico/productosVista";
    }

    @GetMapping("/mantenimiento")
    public String mantenimiento() {
        return "publico/mantenimiento";
    }

    @GetMapping("/getCart")
    public String getCart(HttpSession session) {
        LOGGER.info("🛒 Acceso a carrito desde navbar");
        
        // Si el usuario está logueado, llevarlo al carrito
        Object userIdObj = session.getAttribute("idUsuario");
        if (userIdObj != null) {
            return "redirect:/cliente/carrito";
        }
        
        // Si no está logueado, llevarlo al login
        return "redirect:/publico/login";
    }
}
