package com.sena.barberspa.controller.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUsuarioService usuarioService;

    @PostMapping("/login")
    public String login(@RequestParam("username") String email,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            LOGGER.info("Intentando login para email: {}", email);

            // Autenticar con Spring Security
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Obtener el usuario de la base de datos
            Usuario usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Establecer la sesión
            session.setAttribute("idUsuario", usuario.getId());
            LOGGER.info("Login exitoso para usuario: {} con rol: {}", usuario.getNombre(), usuario.getRol());

            // Redirigir según el rol
            switch (usuario.getRol()) {
                case "GERENTE":
                    return "redirect:/administrador/";
                case "ADMIN_SUCURSAL":
                    return "redirect:/admin-sucursal/";
                case "EMPLEADO":
                    return "redirect:/empleado/";
                case "CLIENTE":
                default:
                    return "redirect:/home/";
            }

        } catch (Exception e) {
            LOGGER.error("Error en login: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Credenciales inválidas");
            return "redirect:/usuario/login?error=true";
        }
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("error", "Credenciales inválidas");
        return "usuario/login";
    }
}