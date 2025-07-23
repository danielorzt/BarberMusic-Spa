package com.sena.barberspa.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador raíz para manejar redirecciones principales
 * y el flujo post-login según roles
 */
@Controller
public class RootController {

    @Autowired
    private IUsuarioService usuarioService;

    /**
     * Página de inicio - redirige a home para clientes no autenticados
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    /**
     * Maneja la redirección después del login exitoso según el rol del usuario
     */
    @GetMapping("/login-success")
    public String loginSuccess(HttpSession session) {
        Object userIdObj = session.getAttribute("idUsuario");
        if (userIdObj != null) {
            Long userId = Long.parseLong(userIdObj.toString());
            Usuario usuario = usuarioService.findById(userId).orElse(null);
            if (usuario != null) {
                switch (usuario.getRol()) {
                    case "ADMIN_GENERAL":
                    case "GERENTE":
                        return "redirect:/administrador/";
                    case "ADMIN_SUCURSAL":
                        return "redirect:/admin-sucursal/";
                    case "EMPLEADO":
                        return "redirect:/empleado/";
                    case "CLIENTE":
                    default:
                        return "redirect:/home";
                }
            }
        }
        return "redirect:/home";
    }
}