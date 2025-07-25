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
            LOGGER.info("🔍 ModelAttribute: Evaluando sesión y autenticación...");
            
            // Intentar obtener usuario de la sesión HTTP primero
            Object userIdObj = session.getAttribute(SESSION_USER_ID);
            LOGGER.info("🔍 HTTP Session userID: {}", userIdObj);
            
            if (userIdObj != null) {
                Long userId = Long.parseLong(userIdObj.toString());
                Optional<Usuario> usuarioOpt = usuarioService.findById(userId);
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("sesion", userId);
                    model.addAttribute("isAuthenticated", true);
                    LOGGER.info("✅ Usuario cargado desde sesión HTTP: {} (ID: {})", usuario.getNombre(), userId);
                    LOGGER.info("🎯 ModelAttribute: sesion={}, isAuthenticated=true", userId);
                    return;
                }
            }
            
            // Fallback: intentar obtener usuario desde Spring Security
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("🔍 Spring Security auth: {}, isAuthenticated: {}", 
                       auth != null ? auth.getName() : "null", 
                       auth != null ? auth.isAuthenticated() : false);
            
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                Optional<Usuario> usuarioOpt = usuarioService.findByEmail(auth.getName());
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    // Sincronizar la sesión HTTP con Spring Security
                    session.setAttribute(SESSION_USER_ID, usuario.getId());
                    session.setAttribute("usuario", usuario);
                    
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("sesion", usuario.getId());
                    model.addAttribute("isAuthenticated", true);
                    LOGGER.info("✅ Usuario cargado desde Spring Security: {} (ID: {})", usuario.getNombre(), usuario.getId());
                    LOGGER.info("🎯 ModelAttribute: sesion={}, isAuthenticated=true", usuario.getId());
                    return;
                }
            }
            
            // No hay usuario autenticado - esto está bien para páginas públicas
            model.addAttribute("sesion", null);
            model.addAttribute("usuario", null);
            model.addAttribute("isAuthenticated", false);
            LOGGER.info("ℹ️ No hay usuario autenticado - modo público");
            LOGGER.info("🎯 ModelAttribute: sesion=null, isAuthenticated=false");
            
        } catch (Exception e) {
            LOGGER.error("💥 Error loading user session data: {}", e.getMessage(), e);
            // Set defaults for safe fallback
            model.addAttribute("sesion", null);
            model.addAttribute("usuario", null);
            model.addAttribute("isAuthenticated", false);
            LOGGER.error("🎯 ModelAttribute: FALLBACK - sesion=null, isAuthenticated=false");
        }
    }

    // SOLUCION DEFINITIVA: Método con carga real de datos y @Transactional
    @GetMapping({ "", "/" })
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String home(Model model, HttpSession session) {
        try {
            LOGGER.info("🏠 HOME: Iniciando carga de datos con transacción...");
            
            // Debug: verificar atributos de sesión
            Object sesionAttr = model.getAttribute("sesion");
            Object usuarioAttr = model.getAttribute("usuario");
            LOGGER.info("🏠 HOME: sesion = {}, usuario = {}", sesionAttr, usuarioAttr != null ? "presente" : "null");
            
            // Cargar datos reales con protección transaccional
            List<Producto> productos = productoService.findAll();
            List<Servicio> servicios = servicioService.findAll();
            List<Sucursal> sucursales = sucursalService.findAll();
            
            // Filtrar solo elementos activos
            productos = productos != null ? productos.stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .collect(java.util.stream.Collectors.toList()) : new ArrayList<>();
                
            servicios = servicios != null ? servicios.stream()
                .filter(s -> s.getActivo() != null && s.getActivo())
                .collect(java.util.stream.Collectors.toList()) : new ArrayList<>();
                
            sucursales = sucursales != null ? sucursales.stream()
                .filter(s -> s.getActivo() != null && s.getActivo())
                .collect(java.util.stream.Collectors.toList()) : new ArrayList<>();
            
            model.addAttribute("productos", productos);
            model.addAttribute("servicios", servicios);
            model.addAttribute("sucursales", sucursales);
            
            LOGGER.info("✅ HOME: Datos cargados - {} productos, {} servicios, {} sucursales", 
                       productos.size(), servicios.size(), sucursales.size());
            
            return "usuario/home";
            
        } catch (Exception e) {
            LOGGER.error("💥 HOME: Error crítico: {}", e.getMessage(), e);
            // Fallback con listas vacías en caso de error
            model.addAttribute("productos", new ArrayList<>());
            model.addAttribute("servicios", new ArrayList<>());
            model.addAttribute("sucursales", new ArrayList<>());
            model.addAttribute("error", "Error cargando datos. Usando modo seguro.");
            return "usuario/home";
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
    public String testHome(HttpSession session, Model model) {
        StringBuilder result = new StringBuilder();
        result.append("🧪 Testing HomeController & Session...\n\n");

        try {
            // Test session
            Object sessionUserId = session.getAttribute(SESSION_USER_ID);
            result.append("📋 Session Data:\n");
            result.append("   - User ID in session: ").append(sessionUserId).append("\n");
            
            // Test Spring Security
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            result.append("   - Spring Security auth: ").append(auth != null ? auth.getName() : "null").append("\n");
            result.append("   - Is authenticated: ").append(auth != null ? auth.isAuthenticated() : false).append("\n");
            
            // Test user loading
            if (sessionUserId != null) {
                Long userId = Long.parseLong(sessionUserId.toString());
                Optional<Usuario> usuarioOpt = usuarioService.findById(userId);
                result.append("   - User loaded from DB: ").append(usuarioOpt.isPresent() ? usuarioOpt.get().getNombre() : "Not found").append("\n");
            } else if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                Optional<Usuario> usuarioOpt = usuarioService.findByEmail(auth.getName());
                result.append("   - User loaded by email: ").append(usuarioOpt.isPresent() ? usuarioOpt.get().getNombre() : "Not found").append("\n");
            }

            // Test model attributes (these get set by @ModelAttribute)
            result.append("\n🎨 Model Attributes:\n");
            result.append("   - sesion: ").append(model.getAttribute("sesion")).append("\n");
            result.append("   - usuario: ").append(model.getAttribute("usuario")).append("\n");
            result.append("   - isAuthenticated: ").append(model.getAttribute("isAuthenticated")).append("\n");

            result.append("\n📊 Services Test:\n");
            result.append("   - Productos count: ").append(productoService.findAll().size()).append("\n");
            result.append("   - Servicios count: ").append(servicioService.findAll().size()).append("\n");
            result.append("   - Sucursales count: ").append(sucursalService.findAll().size()).append("\n");

            result.append("\n✅ All tests completed successfully!\n");

        } catch (Exception e) {
            result.append("\n❌ Error: ").append(e.getMessage()).append("\n");
            result.append("Stack trace: ").append(e.getStackTrace()[0]).append("\n");
        }

        return result.toString();
    }

    @GetMapping("/mantenimiento")
    public String mantenimiento() {
        return "usuario/mantenimiento";
    }

    @GetMapping("/productosVista")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String productosVista(Model model) {
        try {
            List<Producto> productos = productoService.findAll();
            productos = productos != null ? productos.stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .collect(java.util.stream.Collectors.toList()) : new ArrayList<>();
            model.addAttribute("productos", productos);
            LOGGER.info("✅ ProductosVista: {} productos activos cargados", productos.size());
        } catch (Exception e) {
            LOGGER.error("❌ Error loading products: {}", e.getMessage(), e);
            model.addAttribute("productos", new ArrayList<>());
        }
        return "usuario/productosVista";
    }

    @GetMapping("/serviciosVista")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String serviciosVista(Model model) {
        try {
            List<Servicio> servicios = servicioService.findAll();
            servicios = servicios != null ? servicios.stream()
                .filter(s -> s.getActivo() != null && s.getActivo())
                .collect(java.util.stream.Collectors.toList()) : new ArrayList<>();
            model.addAttribute("servicios", servicios);
            LOGGER.info("✅ ServiciosVista: {} servicios activos cargados", servicios.size());
        } catch (Exception e) {
            LOGGER.error("❌ Error loading services: {}", e.getMessage(), e);
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