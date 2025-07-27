package com.sena.barberspa.controller.web;

import java.util.List;
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

import com.sena.barberspa.model.Servicio;
import com.sena.barberspa.model.Sucursal;
import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IServiciosService;
import com.sena.barberspa.service.ISucursalesService;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador para vistas de servicios individuales
 * Maneja tanto vistas públicas como de cliente autenticado
 */
@Controller
public class ServicioClienteController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServicioClienteController.class);
    
    @Autowired
    private IServiciosService servicioService;
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private ISucursalesService sucursalesService;
    
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
     * Vista individual de servicio para usuarios públicos y autenticados
     */
    @GetMapping("/servicioHome/{id}")
    public String verServicio(@PathVariable Long id, Model model, HttpSession session) {
        try {
            LOGGER.info("💇 Mostrando servicio ID: {}", id);
            
            Optional<Servicio> servicioOpt = servicioService.get(id);
            if (servicioOpt.isEmpty()) {
                LOGGER.warn("❌ Servicio no encontrado: {}", id);
                return "redirect:/home/serviciosVista";
            }
            
            Servicio servicio = servicioOpt.get();
            model.addAttribute("servicio", servicio);
            
            // Cargar lista de sucursales para el formulario de agendamiento
            List<Sucursal> sucursales = sucursalesService.findAll();
            model.addAttribute("sucursales", sucursales);
            
            // Verificar si usuario está autenticado para mostrar diferentes opciones
            boolean isAuthenticated = session.getAttribute("idUsuario") != null;
            model.addAttribute("isAuthenticated", isAuthenticated);
            
            if (isAuthenticated) {
                // Cliente autenticado - mostrar vista con opciones de agendamiento
                return "cliente/servicio-detalle";
            } else {
                // Usuario público - mostrar vista básica
                return "publico/servicio-detalle";
            }
            
        } catch (Exception e) {
            LOGGER.error("❌ Error mostrando servicio {}: {}", id, e.getMessage(), e);
            return "redirect:/home/serviciosVista";
        }
    }
    
    /**
     * Agendar cita para un servicio (solo clientes autenticados)
     */
    @PostMapping("/cliente/agendar-servicio")
    public String agendarServicio(
            @RequestParam("servicioId") Long servicioId,
            @RequestParam("sucursalId") Long sucursalId,
            @RequestParam("fechaCita") String fechaCita,
            @RequestParam("horaCita") String horaCita,
            @RequestParam(value = "notas", required = false) String notas,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Verificar autenticación
            Object userIdObj = session.getAttribute("idUsuario");
            if (userIdObj == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para agendar citas");
                return "redirect:/publico/login";
            }
            
            // Verificar que el servicio existe
            Optional<Servicio> servicioOpt = servicioService.get(servicioId);
            if (servicioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Servicio no encontrado");
                return "redirect:/home/serviciosVista";
            }
            
            // Verificar que la sucursal existe
            Optional<Sucursal> sucursalOpt = sucursalesService.get(sucursalId);
            if (sucursalOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Sucursal no encontrada");
                return "redirect:/servicioHome/" + servicioId;
            }
            
            Servicio servicio = servicioOpt.get();
            Sucursal sucursal = sucursalOpt.get();
            
            // TODO: Implementar lógica de agendamiento
            // Por ahora, simulamos el agendamiento
            LOGGER.info("📅 Agendando servicio: {} en sucursal: {} para fecha: {} hora: {} usuario ID: {}", 
                       servicio.getNombre(), sucursal.getNombre(), fechaCita, horaCita, userIdObj);
            
            redirectAttributes.addFlashAttribute("success", 
                String.format("Cita para '%s' agendada exitosamente en %s para el %s a las %s", 
                            servicio.getNombre(), sucursal.getNombre(), fechaCita, horaCita));
            
            return "redirect:/cliente/perfil";
            
        } catch (Exception e) {
            LOGGER.error("❌ Error agendando servicio: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al agendar la cita");
            return "redirect:/servicioHome/" + servicioId;
        }
    }
    
    /**
     * Mostrar formulario de agendamiento para un servicio específico
     */
    @GetMapping("/cliente/agendar/{servicioId}")
    public String mostrarFormularioAgendamiento(
            @PathVariable Long servicioId,
            Model model,
            HttpSession session) {
        
        try {
            // Verificar autenticación
            Object userIdObj = session.getAttribute("idUsuario");
            if (userIdObj == null) {
                return "redirect:/publico/login";
            }
            
            // Verificar que el servicio existe
            Optional<Servicio> servicioOpt = servicioService.get(servicioId);
            if (servicioOpt.isEmpty()) {
                return "redirect:/home/serviciosVista";
            }
            
            Servicio servicio = servicioOpt.get();
            model.addAttribute("servicio", servicio);
            
            // Cargar lista de sucursales
            List<Sucursal> sucursales = sucursalesService.findAll();
            model.addAttribute("sucursales", sucursales);
            
            // Obtener usuario para datos del formulario
            Long userId = Long.parseLong(userIdObj.toString());
            Optional<Usuario> usuarioOpt = usuarioService.findById(userId);
            if (usuarioOpt.isPresent()) {
                model.addAttribute("usuario", usuarioOpt.get());
            }
            
            return "cliente/agendar-cita";
            
        } catch (Exception e) {
            LOGGER.error("❌ Error mostrando formulario de agendamiento: {}", e.getMessage(), e);
            return "redirect:/home/serviciosVista";
        }
    }
}