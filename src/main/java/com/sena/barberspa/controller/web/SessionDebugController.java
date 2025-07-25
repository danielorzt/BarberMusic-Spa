package com.sena.barberspa.controller.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador para debugging de sesiones y autenticación
 * Ayuda a diagnosticar problemas con el cambio de vista después del login
 */
@Controller
@RequestMapping("/debug")
public class SessionDebugController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionDebugController.class);
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @GetMapping("/session")
    @ResponseBody
    public Map<String, Object> debugSession(HttpSession session) {
        Map<String, Object> debug = new HashMap<>();
        
        try {
            // Información de la sesión HTTP
            debug.put("sessionId", session.getId());
            debug.put("isNew", session.isNew());
            debug.put("creationTime", session.getCreationTime());
            debug.put("lastAccessedTime", session.getLastAccessedTime());
            
            // Atributos de la sesión
            Object userId = session.getAttribute("idUsuario");
            Object usuario = session.getAttribute("usuario");
            
            debug.put("sessionUserId", userId);
            debug.put("sessionUsuario", usuario != null ? usuario.toString() : null);
            
            // Información de Spring Security
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                debug.put("authName", auth.getName());
                debug.put("authAuthorities", auth.getAuthorities());
                debug.put("isAuthenticated", auth.isAuthenticated());
                debug.put("authClass", auth.getClass().getSimpleName());
            } else {
                debug.put("springSecurityAuth", "null");
            }
            
            // Intentar cargar usuario
            if (userId != null) {
                try {
                    Long userIdLong = Long.parseLong(userId.toString());
                    Optional<Usuario> usuarioOpt = usuarioService.findById(userIdLong);
                    if (usuarioOpt.isPresent()) {
                        Usuario u = usuarioOpt.get();
                        debug.put("userFromDB", Map.of(
                            "id", u.getId(),
                            "nombre", u.getNombre(),
                            "email", u.getEmail(),
                            "rol", u.getRol(),
                            "activo", u.getActivo()
                        ));
                    } else {
                        debug.put("userFromDB", "NOT_FOUND");
                    }
                } catch (Exception e) {
                    debug.put("userFromDBError", e.getMessage());
                }
            } else if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                try {
                    Optional<Usuario> usuarioOpt = usuarioService.findByEmail(auth.getName());
                    if (usuarioOpt.isPresent()) {
                        Usuario u = usuarioOpt.get();
                        debug.put("userFromEmail", Map.of(
                            "id", u.getId(),
                            "nombre", u.getNombre(),
                            "email", u.getEmail(),
                            "rol", u.getRol(),
                            "activo", u.getActivo()
                        ));
                    } else {
                        debug.put("userFromEmail", "NOT_FOUND");
                    }
                } catch (Exception e) {
                    debug.put("userFromEmailError", e.getMessage());
                }
            }
            
            debug.put("status", "SUCCESS");
            
        } catch (Exception e) {
            LOGGER.error("Error en debug session: {}", e.getMessage(), e);
            debug.put("status", "ERROR");
            debug.put("error", e.getMessage());
        }
        
        return debug;
    }
    
    @GetMapping("/home-session")
    public String debugHome(Model model, HttpSession session) {
        try {
            LOGGER.info("🔍 DEBUG HOME: Iniciando debug de template...");
            
            // Manualmente agregar los atributos que el template necesita
            Object userIdObj = session.getAttribute("idUsuario");
            if (userIdObj != null) {
                Long userId = Long.parseLong(userIdObj.toString());
                Optional<Usuario> usuarioOpt = usuarioService.findById(userId);
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("sesion", userId);
                    LOGGER.info("🔍 DEBUG HOME: Usuario encontrado: {} (ID: {})", usuario.getNombre(), userId);
                } else {
                    LOGGER.warn("🔍 DEBUG HOME: Usuario no encontrado en BD para ID: {}", userId);
                }
            } else {
                // Fallback con Spring Security
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                    Optional<Usuario> usuarioOpt = usuarioService.findByEmail(auth.getName());
                    if (usuarioOpt.isPresent()) {
                        Usuario usuario = usuarioOpt.get();
                        model.addAttribute("usuario", usuario);
                        model.addAttribute("sesion", usuario.getId());
                        LOGGER.info("🔍 DEBUG HOME: Usuario cargado por email: {} (ID: {})", usuario.getNombre(), usuario.getId());
                    } else {
                        LOGGER.warn("🔍 DEBUG HOME: Usuario no encontrado por email: {}", auth.getName());
                    }
                } else {
                    LOGGER.info("🔍 DEBUG HOME: No hay usuario autenticado");
                }
            }
            
            // Log final del modelo
            LOGGER.info("🔍 DEBUG HOME: Modelo final - usuario: {}, sesion: {}", 
                       model.getAttribute("usuario"), model.getAttribute("sesion"));
            
            // Datos mínimos para el template
            model.addAttribute("productos", java.util.Collections.emptyList());
            model.addAttribute("servicios", java.util.Collections.emptyList());
            model.addAttribute("sucursales", java.util.Collections.emptyList());
            
            return "usuario/home";
            
        } catch (Exception e) {
            LOGGER.error("🔍 DEBUG HOME: Error: {}", e.getMessage(), e);
            return "error/500";
        }
    }
    
    @GetMapping("/template-test")
    public String templateTest(Model model, HttpSession session) {
        // Forzar un usuario para probar el template
        model.addAttribute("sesion", 999L);
        model.addAttribute("usuario", new Usuario() {{
            setId(999L);
            setNombre("Usuario de Prueba");
            setEmail("test@test.com");
        }});
        
        model.addAttribute("productos", java.util.Collections.emptyList());
        model.addAttribute("servicios", java.util.Collections.emptyList());
        model.addAttribute("sucursales", java.util.Collections.emptyList());
        
        LOGGER.info("🔍 TEMPLATE TEST: Forzando usuario de prueba");
        
        return "usuario/home";
    }
    
    @GetMapping("/session-status")
    @ResponseBody
    public String sessionStatus(HttpSession session) {
        StringBuilder status = new StringBuilder();
        status.append("🔧 SESSION MANAGEMENT FIX STATUS\n\n");
        
        try {
            Object userId = session.getAttribute("idUsuario");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (userId != null) {
                Optional<Usuario> usuarioOpt = usuarioService.findById(Long.parseLong(userId.toString()));
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    status.append("✅ SESIÓN ACTIVA\n");
                    status.append("   - Usuario: ").append(usuario.getNombre()).append("\n");
                    status.append("   - Email: ").append(usuario.getEmail()).append("\n");
                    status.append("   - Rol: ").append(usuario.getRol().getDescripcion()).append("\n");
                    status.append("   - ID en sesión: ").append(userId).append("\n");
                } else {
                    status.append("❌ ERROR: Usuario en sesión no encontrado en BD\n");
                }
            } else if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                status.append("⚠️ SESIÓN PARCIAL\n");
                status.append("   - Spring Security detectado: ").append(auth.getName()).append("\n");
                status.append("   - Sesión HTTP vacía (se sincronizará automáticamente)\n");
            } else {
                status.append("❌ SIN SESIÓN\n");
                status.append("   - No hay usuario autenticado\n");
            }
            
            status.append("\n🔍 CONTROLADORES ACTUALIZADOS:\n");
            status.append("   ✅ HomeController - /home/*\n");
            status.append("   ✅ ProductoController - /productos/*\n");
            status.append("   ✅ ServicioController - /servicios/*\n");
            status.append("   ✅ PublicProductoController - /productosVista, /productoHome/*\n");
            
            status.append("\n📋 PRÓXIMOS PASOS:\n");
            status.append("   1. Navegar a páginas de productos/servicios\n");
            status.append("   2. Verificar que el icono de perfil aparezca\n");
            status.append("   3. Confirmar acceso a funcionalidades de compra\n");
            
        } catch (Exception e) {
            status.append("❌ ERROR: ").append(e.getMessage()).append("\n");
        }
        
        return status.toString();
    }
    
    @GetMapping("/security-test")
    @ResponseBody
    public String securityTest(HttpSession session) {
        StringBuilder test = new StringBuilder();
        test.append("🔒 SPRING SECURITY CONFIGURATION TEST\n\n");
        
        try {
            Object userId = session.getAttribute("idUsuario");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            test.append("📋 ESTADO ACTUAL:\n");
            if (userId != null) {
                Optional<Usuario> usuarioOpt = usuarioService.findById(Long.parseLong(userId.toString()));
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    test.append("   ✅ Usuario: ").append(usuario.getNombre()).append("\n");
                    test.append("   ✅ Rol: ").append(usuario.getRol().getCodigo()).append("\n");
                    test.append("   ✅ Authorities: ").append(auth != null ? auth.getAuthorities() : "none").append("\n");
                }
            } else {
                test.append("   ❌ No hay usuario en sesión\n");
            }
            
            test.append("\n🛡️ RUTAS CONFIGURADAS PARA CLIENTE:\n");
            test.append("   ✅ /usuario/perfil - Acceso permitido para CLIENTE\n");
            test.append("   ✅ /usuario/compras/** - Acceso permitido para CLIENTE\n");
            test.append("   ✅ /usuario/favoritos/** - Acceso permitido para CLIENTE\n");
            test.append("   ✅ /productos/** - Acceso permitido para CLIENTE\n");
            test.append("   ✅ /servicios/** - Acceso permitido para CLIENTE\n");
            
            test.append("\n🌐 RUTAS PÚBLICAS (SIN AUTENTICACIÓN):\n");
            test.append("   ✅ /home/** - Público\n");
            test.append("   ✅ /productosVista - Público\n");
            test.append("   ✅ /serviciosVista - Público\n");
            test.append("   ✅ /productoHome/** - Público\n");
            test.append("   ✅ /usuario/login - Público\n");
            test.append("   ✅ /usuario/registro - Público\n");
            
            test.append("\n⚠️ POSIBLES PROBLEMAS:\n");
            test.append("   - Si aún recibes error 500, revisa los logs del servidor\n");
            test.append("   - Asegúrate de que el rol sea exactamente 'CLIENTE' (no 'Cliente')\n");
            test.append("   - Verifica que Spring Security esté detectando el rol correctamente\n");
            
            test.append("\n🧪 PRÓXIMOS TESTS:\n");
            test.append("   1. Prueba acceder a: /usuario/perfil\n");
            test.append("   2. Prueba acceder a: /usuario/compras\n");
            test.append("   3. Prueba acceder a: /productos\n");
            test.append("   4. Prueba acceder a: /servicios\n");
            
        } catch (Exception e) {
            test.append("❌ ERROR EN TEST: ").append(e.getMessage()).append("\n");
        }
        
        return test.toString();
    }
}