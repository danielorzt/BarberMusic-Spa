package com.sena.barberspa.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.model.enums.RolUsuario;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminDebugController {

    private static final Logger logger = LoggerFactory.getLogger(AdminDebugController.class);

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/debug/admin-test")
    @ResponseBody
    public String debugAdminAccess(HttpSession session) {
        StringBuilder debug = new StringBuilder();
        debug.append("<h2>🔍 DEBUG: Admin Access Test</h2><br>");

        try {
            // Get authentication info
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            debug.append("<b>Authentication:</b> ").append(auth != null ? auth.getName() : "null").append("<br>");
            debug.append("<b>Authorities:</b> ").append(auth != null ? auth.getAuthorities() : "null").append("<br>");
            debug.append("<b>Authenticated:</b> ").append(auth != null ? auth.isAuthenticated() : "false")
                    .append("<br><br>");

            // Get user from session
            Object userIdObj = session.getAttribute("idUsuario");
            debug.append("<b>Session User ID:</b> ").append(userIdObj).append("<br>");

            if (userIdObj != null) {
                Long userId = Long.parseLong(userIdObj.toString());
                Optional<Usuario> usuarioOpt = usuarioService.findById(userId);
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    debug.append("<b>User Name:</b> ").append(usuario.getNombre()).append("<br>");
                    debug.append("<b>User Email:</b> ").append(usuario.getEmail()).append("<br>");
                    debug.append("<b>User Role:</b> ").append(usuario.getRol()).append("<br>");
                    debug.append("<b>User Active:</b> ").append(usuario.getActivo()).append("<br><br>");

                    // Check if should have admin access
                    RolUsuario rol = usuario.getRol();
                    boolean shouldHaveAdminAccess = RolUsuario.GERENTE.equals(rol) || RolUsuario.ADMIN_SUCURSAL.equals(rol);
                    debug.append("<b>Should have admin access:</b> ").append(shouldHaveAdminAccess).append("<br>");

                    if (shouldHaveAdminAccess) {
                        debug.append("<span style='color: green;'>✅ Usuario debería tener acceso admin</span><br>");
                        debug.append(
                                "<a href='/administrador' target='_blank'>🔗 Probar acceso a /administrador</a><br>");
                    } else {
                        debug.append("<span style='color: red;'>❌ Usuario NO debería tener acceso admin</span><br>");
                        debug.append(
                                "💡 Para obtener acceso admin, el rol debe ser ADMIN_GENERAL o ADMIN_SUCURSAL<br>");
                    }
                } else {
                    debug.append("<span style='color: red;'>❌ Usuario no encontrado en base de datos</span><br>");
                }
            } else {
                debug.append("<span style='color: orange;'>⚠️ No hay usuario en sesión</span><br>");
                debug.append("<a href='/usuario/login'>🔗 Ir a Login</a><br>");
            }

        } catch (Exception e) {
            debug.append("<span style='color: red;'>❌ Error: ").append(e.getMessage()).append("</span><br>");
            logger.error("Error in debug admin access", e);
        }

        return debug.toString();
    }

    @GetMapping("/debug/create-admin")
    @ResponseBody
    public String createTestAdmin() {
        try {
            // Check if admin already exists
            Optional<Usuario> existingAdmin = usuarioService.findByEmail("admin@barberspa.com");
            if (existingAdmin.isPresent()) {
                return "✅ Admin user already exists: admin@barberspa.com<br>" +
                        "Current role: " + existingAdmin.get().getRol() + "<br>" +
                        "<a href='/debug/admin-test'>🔗 Test admin access</a>";
            }

            // Create new admin user
            Usuario admin = new Usuario();
            admin.setNombre("Admin Sistema");
            admin.setEmail("admin@barberspa.com");
            admin.setRol(RolUsuario.GERENTE);
            admin.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"); // admin123
            admin.setActivo(true);
            admin.setTelefono("1234567890");

            usuarioService.save(admin);

            return "✅ Admin user created successfully!<br>" +
                    "Email: admin@barberspa.com<br>" +
                    "Password: admin123<br>" +
                    "Role: ADMIN_GENERAL<br>" +
                    "<a href='/usuario/login'>🔗 Login as admin</a>";

        } catch (Exception e) {
            return "❌ Error creating admin: " + e.getMessage();
        }
    }
}