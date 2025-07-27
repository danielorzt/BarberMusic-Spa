package com.sena.barberspa.controller.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.model.Sucursal;
import com.sena.barberspa.model.enums.RolUsuario;
import com.sena.barberspa.service.IUsuarioService;
import com.sena.barberspa.service.ISucursalesService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Controlador para rutas públicas según Manual de Roles BarberMusic&Spa
 * 
 * Maneja todas las rutas /publico/** que no requieren autenticación:
 * - Login y autenticación
 * - Registro de usuarios
 * - Recuperación de contraseña
 * - Páginas de error
 */
@Controller
@RequestMapping("/publico")
public class PublicoController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicoController.class);
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private ISucursalesService sucursalesService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    // ============ RUTAS DE LOGIN ============
    
    /**
     * Mostrar página de login
     */
    @GetMapping("/login")
    public String mostrarLogin(Model model, @RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout) {
        LOGGER.info("🔐 Acceso a /publico/login");
        
        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos");
        }
        
        if (logout != null) {
            model.addAttribute("success", "Sesión cerrada exitosamente");
        }
        
        return "publico/login";
    }
    
    // ============ RUTAS DE REGISTRO ============
    
    /**
     * Mostrar formulario de registro
     */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        LOGGER.info("📝 Acceso a /publico/registro");
        
        // Crear un usuario vacío para el formulario
        model.addAttribute("usuario", new Usuario());
        
        return "publico/registro";
    }
    
    /**
     * Procesar registro de nuevo usuario
     * Según manual: Todo usuario nuevo se registra con rol CLIENTE
     */
    @PostMapping("/registro")
    public String registrarUsuario(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            Model model,
            RedirectAttributes flash,
            HttpSession session) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            LOGGER.info("🆕 Iniciando registro de usuario: {}", usuario.getEmail());
            
            // 1. Validar errores de entrada
            if (result.hasErrors()) {
                LOGGER.warn("❌ Errores de validación en registro: {}", result.getAllErrors());
                model.addAttribute("usuario", usuario);
                model.addAttribute("error", "Por favor corrige los errores en el formulario");
                return "publico/registro";
            }
            
            // 2. Validar que el email no exista
            if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {
                LOGGER.warn("❌ Intento de registro con email duplicado: {}", usuario.getEmail());
                model.addAttribute("usuario", usuario);
                model.addAttribute("error", "Este email ya está registrado. Intenta con otro email o inicia sesión.");
                return "publico/registro";
            }
            
            // 3. Configurar usuario según manual de roles
            usuario.setRol(RolUsuario.CLIENTE); // ROL POR DEFECTO según manual
            usuario.setActivo(true);
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            
            // 3.1. Asignar sucursal por defecto (primera sucursal activa)
            try {
                List<Sucursal> sucursalesActivas = sucursalesService.findAll().stream()
                    .filter(s -> s.getActivo() != null && s.getActivo())
                    .collect(java.util.stream.Collectors.toList());
                
                if (!sucursalesActivas.isEmpty()) {
                    usuario.setSucursalPreferida(sucursalesActivas.get(0));
                    LOGGER.info("✅ Asignada sucursal por defecto: {} para usuario: {}", 
                               sucursalesActivas.get(0).getNombre(), usuario.getEmail());
                } else {
                    LOGGER.warn("⚠️ No hay sucursales activas disponibles para asignar por defecto");
                }
            } catch (Exception e) {
                LOGGER.error("❌ Error asignando sucursal por defecto: {}", e.getMessage());
                // Continuar sin sucursal - no es crítico para el registro
            }
            
            // 4. Guardar usuario
            Usuario usuarioGuardado = usuarioService.save(usuario);
            
            long totalTime = System.currentTimeMillis() - startTime;
            LOGGER.info("✅ Usuario registrado exitosamente: {} (ID: {}) en {}ms", 
                       usuarioGuardado.getEmail(), usuarioGuardado.getId(), totalTime);
            
            // 5. Autenticar automáticamente con Spring Security
            try {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(usuarioGuardado.getEmail(), usuario.getPassword());
                Authentication authentication = authenticationManager.authenticate(authToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Establecer sesión
                session.setAttribute("idUsuario", usuarioGuardado.getId());
                session.setAttribute("usuario", usuarioGuardado);
                
                LOGGER.info("✅ Auto-login exitoso para usuario registrado: {}", usuarioGuardado.getEmail());
                
            } catch (Exception authException) {
                LOGGER.warn("⚠️ Error en auto-login, usuario registrado pero no autenticado: {}", authException.getMessage());
                // El usuario fue registrado exitosamente, pero el auto-login falló
                // Redirigir al login con mensaje
                flash.addFlashAttribute("success", 
                    "¡Registro exitoso! Por favor inicia sesión con tus credenciales.");
                return "redirect:/publico/login";
            }
            
            // 6. Mensaje de éxito
            flash.addFlashAttribute("success", 
                "¡Registro exitoso! Bienvenido a BarberMusic&Spa, " + usuarioGuardado.getNombre());
            
            return "redirect:/home";
            
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            LOGGER.error("💥 Error durante registro después de {}ms: {}", totalTime, e.getMessage(), e);
            
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", "Error interno del servidor. Por favor, intenta nuevamente.");
            return "publico/registro";
        }
    }
    
    // ============ RUTAS DE RECUPERACIÓN DE CONTRASEÑA ============
    
    /**
     * Mostrar formulario de cambio de contraseña
     */
    @GetMapping("/cambiar-password")
    public String showChangePasswordForm(@RequestParam("token") String token, Model model) {
        LOGGER.info("🔑 Acceso a cambiar contraseña con token: {}", token);
        
        // TODO: Implementar validación de token
        model.addAttribute("token", token);
        return "publico/cambiar-password";
    }
    
    /**
     * Página de token inválido
     */
    @GetMapping("/token-invalido")
    public String tokenInvalido() {
        LOGGER.info("❌ Acceso a página de token inválido");
        return "publico/token-invalido";
    }
    
    // ============ API ENDPOINTS ============
    
    /**
     * API endpoint para validar email (AJAX)
     */
    @PostMapping("/validar-email")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validarEmail(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean existe = usuarioService.findByEmail(email).isPresent();
            
            response.put("disponible", !existe);
            response.put("mensaje", existe ? 
                "Este email ya está registrado" : 
                "Email disponible");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOGGER.error("Error validando email: {}", e.getMessage());
            response.put("disponible", false);
            response.put("mensaje", "Error validando email");
            response.put("status", "error");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // ============ ENDPOINTS DE PRUEBA ============
    
    /**
     * Endpoint de prueba para verificar rutas públicas
     */
    @GetMapping("/test")
    @ResponseBody
    public String testPublico(HttpSession session) {
        StringBuilder result = new StringBuilder();
        result.append("🧪 Testing Controlador Público BarberMusic&Spa\n\n");
        
        try {
            // Test 1: Verificar sesión actual
            Object userId = session.getAttribute("idUsuario");
            result.append("📋 Estado de sesión:\n");
            result.append("   - Usuario en sesión: ").append(userId != null ? userId : "No autenticado").append("\n");
            
            // Test 2: Verificar Spring Security
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            result.append("   - Spring Security: ").append(auth != null ? auth.getName() : "null").append("\n");
            result.append("   - Autenticado: ").append(auth != null ? auth.isAuthenticated() : false).append("\n");
            
            // Test 3: Estadísticas de usuarios
            result.append("\n📊 Estadísticas:\n");
            result.append("   - Total usuarios: ").append(usuarioService.findAll().size()).append("\n");
            
            for (RolUsuario rol : RolUsuario.values()) {
                long count = usuarioService.findAll().stream()
                    .filter(u -> u.getRol() == rol)
                    .count();
                result.append("   - ").append(rol.getDescripcion())
                      .append(": ").append(count).append(" usuarios\n");
            }
            
            result.append("\n✅ Controlador público funcionando correctamente!");
            
        } catch (Exception e) {
            result.append("❌ Error en test: ").append(e.getMessage());
        }
        
        return result.toString();
    }
}