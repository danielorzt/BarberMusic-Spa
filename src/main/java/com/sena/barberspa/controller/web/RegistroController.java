package com.sena.barberspa.controller.web;

import java.util.HashMap;
import java.util.Map;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.model.enums.RolUsuario;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Controlador de Registro según Manual de Roles BarberMusic&Spa
 * 
 * Funcionalidades:
 * - Registro de nuevos usuarios con rol CLIENTE por defecto
 * - Validaciones robustas según esquema BD
 * - Manejo de errores y duplicados
 * - Integración con Spring Security
 */
@Controller
@RequestMapping("/usuario")
public class RegistroController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistroController.class);
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    /**
     * Mostrar formulario de registro
     */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        LOGGER.info("📝 Mostrando formulario de registro");
        
        // Crear un usuario vacío para el formulario
        model.addAttribute("usuario", new Usuario());
        
        return "usuario/registro";
    }
    
    /**
     * Procesar registro de nuevo usuario
     * Según manual: Todo usuario nuevo se registra con rol CLIENTE
     */
    @PostMapping("/save")
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
                return "usuario/registro";
            }
            
            // 2. Validar que el email no exista
            if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {
                LOGGER.warn("❌ Intento de registro con email duplicado: {}", usuario.getEmail());
                model.addAttribute("usuario", usuario);
                model.addAttribute("error", "Este email ya está registrado. Intenta con otro email o inicia sesión.");
                return "usuario/registro";
            }
            
            // 3. Configurar usuario según manual de roles
            usuario.setRol(RolUsuario.CLIENTE); // ROL POR DEFECTO según manual
            usuario.setActivo(true);
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            
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
                return "redirect:/usuario/login";
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
            return "usuario/registro";
        }
    }
    
    /**
     * API endpoint para validar email (AJAX)
     */
    @PostMapping("/validar-email")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validarEmail(String email) {
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
    
    /**
     * Endpoint de prueba para verificar el sistema de registro
     */
    @GetMapping("/test-registro")
    @ResponseBody
    public String testRegistro() {
        StringBuilder result = new StringBuilder();
        result.append("🧪 Testing Sistema de Registro BarberMusic&Spa\n\n");
        
        try {
            // Test 1: Contar usuarios actuales
            long totalUsuarios = usuarioService.findAll().size();
            result.append("✅ Total usuarios en BD: ").append(totalUsuarios).append("\n");
            
            // Test 2: Verificar roles disponibles
            result.append("✅ Roles disponibles:\n");
            for (RolUsuario rol : RolUsuario.values()) {
                result.append("   - ").append(rol.getCodigo())
                      .append(" (").append(rol.getDescripcion())
                      .append(", Nivel: ").append(rol.getNivelJerarquia()).append(")\n");
            }
            
            // Test 3: Verificar encoder de password
            String testPassword = "123456";
            String encoded = passwordEncoder.encode(testPassword);
            boolean matches = passwordEncoder.matches(testPassword, encoded);
            result.append("✅ Password encoder funcionando: ").append(matches).append("\n");
            
            result.append("\n🎉 Sistema de registro listo para usar!");
            
        } catch (Exception e) {
            result.append("❌ Error en test: ").append(e.getMessage());
        }
        
        return result.toString();
    }
}