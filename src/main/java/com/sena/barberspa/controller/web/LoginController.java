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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.model.enums.RolUsuario;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador de Login según Manual de Roles BarberMusic&Spa
 * 
 * Funcionalidades:
 * - Login de usuarios con redirección según rol
 * - Gestión de sesiones
 * - Logout seguro
 * - Validaciones de usuarios activos
 */
@Controller
@RequestMapping("/usuario")
public class LoginController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    
    @Autowired
    private IUsuarioService usuarioService;
    
    /**
     * Mostrar página de login
     */
    @GetMapping("/login")
    public String mostrarLogin(Model model, @RequestParam(value = "error", required = false) String error) {
        LOGGER.info("🔐 Mostrando página de login");
        
        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos");
        }
        
        return "usuario/login";
    }
    
    /**
     * Procesar login exitoso y redireccionar según rol
     * Este método se ejecuta después de la autenticación exitosa de Spring Security
     */
    @GetMapping("/login-success")
    public String loginSuccess(HttpSession session, RedirectAttributes flash) {
        try {
            LOGGER.info("🔄 LOGIN-SUCCESS: Procesando login exitoso...");
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            LOGGER.info("✅ Login exitoso para: {}", email);
            
            Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
            if (usuarioOpt.isEmpty()) {
                LOGGER.error("❌ Usuario autenticado pero no encontrado en BD: {}", email);
                flash.addFlashAttribute("error", "Error interno. Contacte al administrador.");
                return "redirect:/usuario/login";
            }
            
            Usuario usuario = usuarioOpt.get();
            
            // Verificar que el usuario esté activo
            if (!usuario.getActivo() || usuario.isDeleted()) {
                LOGGER.warn("❌ Intento de login con usuario inactivo: {}", email);
                SecurityContextHolder.clearContext();
                flash.addFlashAttribute("error", "Tu cuenta está desactivada. Contacta al administrador.");
                return "redirect:/usuario/login";
            }
            
            // Establecer sesión
            session.setAttribute("idUsuario", usuario.getId());
            session.setAttribute("usuario", usuario);
            
            // Redireccionar según rol (Manual de Roles)
            String redirectUrl = determinarRedirectPorRol(usuario.getRol());
            
            LOGGER.info("🎯 Redirigiendo usuario {} (rol: {}) a: {}", 
                       usuario.getNombre(), usuario.getRol().getCodigo(), redirectUrl);
            
            flash.addFlashAttribute("success", 
                "¡Bienvenido " + usuario.getNombre() + "! (" + usuario.getRol().getDescripcion() + ")");
            
            return "redirect:" + redirectUrl;
            
        } catch (Exception e) {
            LOGGER.error("💥 Error durante login success: {}", e.getMessage(), e);
            flash.addFlashAttribute("error", "Error interno durante el login");
            return "redirect:/usuario/login";
        }
    }
    
    /**
     * Determinar URL de redirección según rol del usuario
     * Implementa la jerarquía del Manual de Roles
     */
    private String determinarRedirectPorRol(RolUsuario rol) {
        switch (rol) {
            case CLIENTE:
                return "/home"; // Página principal para clientes
                
            case EMPLEADO:
                return "/empleado/panel"; // Panel de empleado
                
            case ADMIN_SUCURSAL:
                return "/admin-sucursal/panel"; // Panel de administrador de sucursal
                
            case GERENTE:
                return "/administrador/home"; // Panel de gerente general
                
            default:
                LOGGER.warn("⚠️ Rol no reconocido: {}, redirigiendo a home", rol);
                return "/home";
        }
    }
    
    /**
     * Logout seguro
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes flash) {
        try {
            // Obtener información del usuario antes de limpiar
            Object usuarioId = session.getAttribute("idUsuario");
            
            LOGGER.info("🚪 Logout de usuario ID: {}", usuarioId);
            
            // Limpiar sesión
            session.invalidate();
            
            // Limpiar contexto de seguridad
            SecurityContextHolder.clearContext();
            
            flash.addFlashAttribute("success", "Sesión cerrada exitosamente");
            
        } catch (Exception e) {
            LOGGER.error("💥 Error durante logout: {}", e.getMessage(), e);
        }
        
        return "redirect:/home";
    }
    
    /**
     * Endpoint para obtener información del usuario actual (AJAX)
     */
    @GetMapping("/current-user")
    @ResponseBody
    public Object getCurrentUser(HttpSession session) {
        try {
            Object userId = session.getAttribute("idUsuario");
            if (userId == null) {
                return new ErrorResponse("No hay usuario logueado");
            }
            
            Optional<Usuario> usuarioOpt = usuarioService.findById(Long.parseLong(userId.toString()));
            if (usuarioOpt.isEmpty()) {
                return new ErrorResponse("Usuario no encontrado");
            }
            
            Usuario usuario = usuarioOpt.get();
            return new UserResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getCodigo(),
                usuario.getRol().getDescripcion(),
                usuario.getActivo()
            );
            
        } catch (Exception e) {
            LOGGER.error("Error obteniendo usuario actual: {}", e.getMessage());
            return new ErrorResponse("Error interno");
        }
    }
    
    /**
     * Endpoint de prueba para verificar el sistema de login
     */
    @GetMapping("/test-login")
    @ResponseBody
    public String testLogin(HttpSession session) {
        StringBuilder result = new StringBuilder();
        result.append("🧪 Testing Sistema de Login BarberMusic&Spa\n\n");
        
        try {
            // Test 1: Verificar sesión actual
            Object userId = session.getAttribute("idUsuario");
            if (userId != null) {
                Optional<Usuario> usuarioOpt = usuarioService.findById(Long.parseLong(userId.toString()));
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    result.append("✅ Usuario logueado: ").append(usuario.getNombre())
                          .append(" (").append(usuario.getRol().getDescripcion()).append(")\n");
                } else {
                    result.append("❌ Usuario en sesión no encontrado en BD\n");
                }
            } else {
                result.append("ℹ️ No hay usuario logueado\n");
            }
            
            // Test 2: Verificar usuarios por rol
            result.append("\n📊 Estadísticas por rol:\n");
            for (RolUsuario rol : RolUsuario.values()) {
                long count = usuarioService.findAll().stream()
                    .filter(u -> u.getRol() == rol)
                    .count();
                result.append("   - ").append(rol.getDescripcion())
                      .append(": ").append(count).append(" usuarios\n");
            }
            
            // Test 3: Verificar usuarios con roles inválidos
            result.append("\n🔍 Usuarios con roles problemáticos:\n");
            usuarioService.findAll().stream()
                .filter(u -> {
                    try {
                        u.getRol(); // Intentar acceder al rol
                        return false;
                    } catch (Exception e) {
                        return true; // Si falla, es problemático
                    }
                })
                .forEach(u -> result.append("   - ").append(u.getEmail()).append(" (rol en BD: ").append(u.getRolString()).append(")\n"));
            
            result.append("\n🎉 Sistema de login funcionando correctamente!");
            
        } catch (Exception e) {
            result.append("❌ Error en test: ").append(e.getMessage());
            e.printStackTrace();
        }
        
        return result.toString();
    }
    
    /**
     * Crear usuario de prueba con contraseña BCrypt correcta
     */
    @GetMapping("/create-test-user")
    @ResponseBody
    public String createTestUser() {
        try {
            // Verificar si ya existe
            Optional<Usuario> existing = usuarioService.findByEmail("test@cliente.com");
            if (existing.isPresent()) {
                return "✅ Usuario de prueba ya existe: test@cliente.com / password: 123456";
            }
            
            // Crear nuevo usuario de prueba
            Usuario testUser = new Usuario();
            testUser.setNombre("Usuario Prueba");
            testUser.setEmail("test@cliente.com");
            testUser.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMye1NrwFldKHrvqEHO4K0XJIBLAYgOhLMa"); // "123456"
            testUser.setRol(RolUsuario.CLIENTE);
            testUser.setActivo(true);
            testUser.setTelefono("+52 999 123 4567");
            
            usuarioService.save(testUser);
            
            return "✅ Usuario de prueba creado exitosamente!\n" +
                   "📧 Email: test@cliente.com\n" +
                   "🔑 Password: 123456\n" +
                   "👤 Rol: CLIENTE\n\n" +
                   "Puedes usar estas credenciales para probar el login.";
            
        } catch (Exception e) {
            return "❌ Error creando usuario de prueba: " + e.getMessage();
        }
    }
    
    // Clases de respuesta para API
    private static class UserResponse {
        public final Long id;
        public final String nombre;
        public final String email;
        public final String rol;
        public final String rolDescripcion;
        public final Boolean activo;
        
        public UserResponse(Long id, String nombre, String email, String rol, String rolDescripcion, Boolean activo) {
            this.id = id;
            this.nombre = nombre;
            this.email = email;
            this.rol = rol;
            this.rolDescripcion = rolDescripcion;
            this.activo = activo;
        }
    }
    
    private static class ErrorResponse {
        public final String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}