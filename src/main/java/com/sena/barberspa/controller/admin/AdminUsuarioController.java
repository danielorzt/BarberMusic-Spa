package com.sena.barberspa.controller.admin;

import com.sena.barberspa.model.Recordatorio;
import com.sena.barberspa.service.IOrdenService;
import com.sena.barberspa.service.IRecordatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.model.enums.RolUsuario;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRecordatorioService recordatorioService;
    @Autowired
    private IOrdenService ordenService;

    // Método para agregar el usuario a todos los modelos
    @ModelAttribute
    public void addUsuarioToModel(Model model, HttpSession session) {
        Object idUsuarioObj = session.getAttribute("idUsuario");
        if (idUsuarioObj != null) {
            Long idUsuario = Long.parseLong(idUsuarioObj.toString());
            Usuario usuario = usuarioService.findById(idUsuario).orElse(null);
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
                // Procesar agendamientos próximos y convertirlos en recordatorios
                recordatorioService.procesarAgendamientosProximos(usuario, 3);

                // Obtener recordatorios para mostrar en la barra lateral
                List<Recordatorio> recordatorios = recordatorioService.findByUsuario(usuario);
                model.addAttribute("recordatorios", recordatorios);

                // Contar las órdenes utilizando el servicio
                long numeroDeOrdenes = ordenService.countAll();

                // Añadir el conteo al modelo
                model.addAttribute("totalOrdenes", numeroDeOrdenes);
            }
        }
    }

    @GetMapping("")
    public String listarUsuarios(Model model, HttpSession session) {
        // Obtener el ID del usuario actual
        Long idUsuarioActual = Long.parseLong(session.getAttribute("idUsuario").toString());

        // Obtener todos los usuarios
        List<Usuario> todosUsuarios = usuarioService.findAll();

        // Filtrar la lista para excluir al usuario actual
        List<Usuario> usuarios = todosUsuarios.stream()
                .filter(u -> !u.getId().equals(idUsuarioActual))
                .collect(Collectors.toList());

        model.addAttribute("usuarios", usuarios);
        return "administrador/usuarios";
    }

    @PostMapping("/cambiar-rol/{id}")
    @PreAuthorize("hasRole('ADMIN_GENERAL')")
    public String cambiarRol(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.findById(id).orElse(null);

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/usuarios";
        }

        // Cambiar el rol del usuario (CLIENTE <-> GERENTE)
        if (RolUsuario.CLIENTE.equals(usuario.getRol())) {
            usuario.setRol(RolUsuario.GERENTE);
            redirectAttributes.addFlashAttribute("success",
                    "Usuario " + usuario.getNombre() + " ahora es administrador general");
        } else if (RolUsuario.GERENTE.equals(usuario.getRol())) {
            usuario.setRol(RolUsuario.CLIENTE);
            redirectAttributes.addFlashAttribute("success",
                    "Usuario " + usuario.getNombre() + " ahora es usuario regular");
        }

        usuarioService.update(usuario);
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN_GENERAL')")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}