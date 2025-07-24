package com.sena.barberspa.controller.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.barberspa.model.*;
import com.sena.barberspa.service.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/home")
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
            Object userIdObj = session.getAttribute(SESSION_USER_ID);
            if (userIdObj != null) {
                Long userId = Long.parseLong(userIdObj.toString());
                usuarioService.findById(userId).ifPresent(usuario -> {
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("sesion", userId);
                });
            }
        } catch (Exception e) {
            LOGGER.warn("Error loading user session data: {}", e.getMessage());
            // Don't fail the whole request if session data fails
        }
    }

    // SOLUCION TEMPORAL: Método simplificado para debugging
    @GetMapping({ "", "/" })
    public String home(Model model) {
        try {
            LOGGER.info("🏠 HOME: Iniciando carga simplificada...");
            
            // Añadir datos mínimos requeridos por el template
            model.addAttribute("productos", new ArrayList<>());
            model.addAttribute("servicios", new ArrayList<>());
            model.addAttribute("sucursales", new ArrayList<>());
            
            // Añadir atributos que el template podría necesitar
            model.addAttribute("sesion", null); // Simular usuario no logueado
            
            LOGGER.info("✅ HOME: Modelo preparado, retornando template");
            return "usuario/home";
            
        } catch (Exception e) {
            LOGGER.error("💥 HOME: Error crítico: {}", e.getMessage(), e);
            // Fallback a página de error simple
            return "error/500";
        }
    }
    
    // METODO COMPLETO COMENTADO TEMPORALMENTE
    @GetMapping("/full")
    public String homeFull(Model model) {
        long startTime = System.currentTimeMillis();
        try {
            LOGGER.info("🏠 Iniciando carga de página home completa...");
            
            // Load data safely with individual error handling per service
            try {
                long startProducts = System.currentTimeMillis();
                List<Producto> productos = productoService.findAll();
                long timeProducts = System.currentTimeMillis() - startProducts;
                
                model.addAttribute("productos", productos != null ? productos : new ArrayList<>());
                LOGGER.info("✅ Productos cargados: {} en {}ms", productos != null ? productos.size() : 0, timeProducts);
            } catch (Exception e) {
                LOGGER.error("❌ Error loading productos: {}", e.getMessage(), e);
                model.addAttribute("productos", new ArrayList<>());
            }
            
            try {
                long startServices = System.currentTimeMillis();
                List<Servicio> servicios = servicioService.findAll();
                long timeServices = System.currentTimeMillis() - startServices;
                
                model.addAttribute("servicios", servicios != null ? servicios : new ArrayList<>());
                LOGGER.info("✅ Servicios cargados: {} en {}ms", servicios != null ? servicios.size() : 0, timeServices);
            } catch (Exception e) {
                LOGGER.error("❌ Error loading servicios: {}", e.getMessage(), e);
                model.addAttribute("servicios", new ArrayList<>());
            }
            
            try {
                long startSucursales = System.currentTimeMillis();
                List<Sucursal> sucursales = sucursalService.findAll();
                long timeSucursales = System.currentTimeMillis() - startSucursales;
                
                model.addAttribute("sucursales", sucursales != null ? sucursales : new ArrayList<>());
                LOGGER.info("✅ Sucursales cargadas: {} en {}ms", sucursales != null ? sucursales.size() : 0, timeSucursales);
            } catch (Exception e) {
                LOGGER.error("❌ Error loading sucursales: {}", e.getMessage(), e);
                model.addAttribute("sucursales", new ArrayList<>());
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            LOGGER.info("🎉 Página home cargada exitosamente en {}ms", totalTime);
            return "usuario/home";
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            LOGGER.error("💥 Error crítico en home controller después de {}ms: {}", totalTime, e.getMessage(), e);
            // Fallback: return a simple page without database data
            model.addAttribute("productos", new ArrayList<>());
            model.addAttribute("servicios", new ArrayList<>());
            model.addAttribute("sucursales", new ArrayList<>());
            model.addAttribute("error", "Error cargando datos. Por favor, contacte al administrador.");
            return "usuario/home";
        }
    }

    @GetMapping("/test-home")
    @ResponseBody
    public String testHome() {
        StringBuilder result = new StringBuilder();
        result.append("Testing HomeController...\n");

        try {
            result.append("Testing ProductoService...\n");
            List<Producto> productos = productoService.findAll();
            result.append("Productos count: ").append(productos != null ? productos.size() : 0).append("\n");

            result.append("Testing ServiciosService...\n");
            List<Servicio> servicios = servicioService.findAll();
            result.append("Servicios count: ").append(servicios != null ? servicios.size() : 0).append("\n");

            result.append("Testing SucursalesService...\n");
            List<Sucursal> sucursales = sucursalService.findAll();
            result.append("Sucursales count: ").append(sucursales != null ? sucursales.size() : 0).append("\n");

            result.append("All services working correctly!\n");

        } catch (Exception e) {
            result.append("Error: ").append(e.getMessage()).append("\n");
            result.append("Stack trace: ").append(e.getStackTrace()[0]).append("\n");
        }

        return result.toString();
    }

    @GetMapping("/mantenimiento")
    public String mantenimiento() {
        return "usuario/mantenimiento";
    }

    @GetMapping("/productosVista")
    public String productosVista(Model model) {
        try {
            List<Producto> productos = productoService.findAll();
            model.addAttribute("productos", productos != null ? productos : new ArrayList<>());
        } catch (Exception e) {
            LOGGER.error("Error loading products: {}", e.getMessage());
            model.addAttribute("productos", new ArrayList<>());
        }
        return "usuario/productosVista";
    }

    @GetMapping("/serviciosVista")
    public String serviciosVista(Model model) {
        try {
            List<Servicio> servicios = servicioService.findAll();
            model.addAttribute("servicios", servicios != null ? servicios : new ArrayList<>());
        } catch (Exception e) {
            LOGGER.error("Error loading services: {}", e.getMessage());
            model.addAttribute("servicios", new ArrayList<>());
        }
        return "usuario/serviciosVista";
    }

    // Métodos utilitarios privados
    private Optional<Usuario> getUsuarioFromSession(HttpSession session) {
        try {
            return Optional.ofNullable(session.getAttribute(SESSION_USER_ID))
                    .map(Object::toString)
                    .map(Long::parseLong)
                    .flatMap(usuarioService::findById);
        } catch (Exception e) {
            LOGGER.warn("Error getting user from session: {}", e.getMessage());
            return Optional.empty();
        }
    }
}