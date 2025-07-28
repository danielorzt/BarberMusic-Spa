package com.sena.barberspa.controller.web;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;

import com.sena.barberspa.model.DetalleOrden;
import com.sena.barberspa.model.Orden;
import com.sena.barberspa.model.Producto;
import com.sena.barberspa.model.Servicio;
import com.sena.barberspa.model.Sucursal;
import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IAgendamientosService;
import com.sena.barberspa.service.IDetalleOrdenService;
import com.sena.barberspa.service.IOrdenService;
import com.sena.barberspa.service.IProductoService;
import com.sena.barberspa.service.IServiciosService;
import com.sena.barberspa.service.ISucursalesService;
import com.sena.barberspa.service.IUsuarioService;

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
                    } else {
                        // Usuario no encontrado en BD, limpiar sesión
                        session.removeAttribute(SESSION_USER_ID);
                        session.removeAttribute("usuario");
                        LOGGER.warn("Usuario no encontrado en BD, sesión limpiada para ID: {}", userId);
                    }
                } catch (Exception e) {
                    LOGGER.warn("Error procesando sesión HTTP: {}", e.getMessage());
                    // Limpiar sesión corrupta
                    session.removeAttribute(SESSION_USER_ID);
                    session.removeAttribute("usuario");
                }
            }

            // Fallback: intentar desde Spring Security
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                    Optional<Usuario> usuarioOpt = usuarioService.findByEmail(auth.getName());
                    if (usuarioOpt.isPresent()) {
                        Usuario usuario = usuarioOpt.get();
                        // Sincronizar la sesión HTTP con Spring Security
                        session.setAttribute(SESSION_USER_ID, usuario.getId());
                        session.setAttribute("usuario", usuario);
                        
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

            return "public/home";

        } catch (Exception e) {
            LOGGER.error("💥 HOME: Error crítico al cargar datos para la página principal: {}", e.getMessage(), e);
            // Fallback seguro con listas vacías en caso de error
            model.addAttribute("productos", new ArrayList<>());
            model.addAttribute("servicios", new ArrayList<>());
            model.addAttribute("sucursales", new ArrayList<>());
            model.addAttribute("error", "No se pudieron cargar los datos de la página. Por favor, intente más tarde.");
            return "public/home";
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

            return "public/serviciosVista";
        } catch (Exception e) {
            LOGGER.error("❌ Error cargando la vista de servicios: {}", e.getMessage(), e);
            model.addAttribute("error", "No se pudieron cargar los servicios. Intente más tarde.");
            model.addAttribute("servicios", new ArrayList<>());
            return "public/serviciosVista";
        }
    }

    // Método productosVista eliminado para evitar conflicto con PublicProductoController

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
