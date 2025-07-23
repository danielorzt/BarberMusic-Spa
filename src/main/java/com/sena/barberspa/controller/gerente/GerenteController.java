package com.sena.barberspa.controller.gerente;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sena.barberspa.model.*;
import com.sena.barberspa.service.*;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador para Gerente (Super Admin)
 * ROL: GERENTE/ADMIN_GENERAL
 * Gestión global: todas las sucursales, promociones, usuarios, auditoría
 */
@Controller
@RequestMapping("/administrador")
@PreAuthorize("hasRole('GERENTE') or hasRole('ADMIN_GENERAL')")
public class GerenteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GerenteController.class);

    @Autowired
    private ISucursalesService sucursalService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IPersonalService personalService;

    /**
     * Panel principal del gerente con vista global
     */
    @GetMapping({ "", "/", "/panel" })
    public String panel(Model model, HttpSession session) {
        try {
            Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
            Usuario gerente = usuarioService.findById(userId).orElse(null);

            if (gerente != null) {
                model.addAttribute("gerente", gerente);

                // Estadísticas globales (usar métodos existentes)
                model.addAttribute("totalSucursales", sucursalService.findAll().size());
                model.addAttribute("totalUsuarios", usuarioService.findAll().size());
                model.addAttribute("totalEmpleados", personalService.findAll().size());

                // Sucursales activas
                List<Sucursal> sucursales = sucursalService.findAll();
                model.addAttribute("sucursales", sucursales);
            }

            return "gerente/panel";
        } catch (Exception e) {
            LOGGER.error("Error en panel gerente: {}", e.getMessage());
            model.addAttribute("error", "Error cargando panel global");
            return "gerente/panel";
        }
    }

    // === GESTIÓN GLOBAL DE SUCURSALES ===

    @GetMapping("/sucursales")
    public String sucursales(Model model) {
        List<Sucursal> sucursales = sucursalService.findAll();
        model.addAttribute("sucursales", sucursales);
        return "gerente/sucursales";
    }

    @GetMapping("/sucursales/nueva")
    public String nuevaSucursal(Model model) {
        model.addAttribute("sucursal", new Sucursal());
        return "gerente/sucursal-form";
    }

    @PostMapping("/sucursales/guardar")
    public String guardarSucursal(@ModelAttribute Sucursal sucursal) {
        try {
            sucursalService.save(sucursal);
            LOGGER.info("Sucursal creada: {}", sucursal.getNombre());
        } catch (Exception e) {
            LOGGER.error("Error guardando sucursal: {}", e.getMessage());
        }
        return "redirect:/administrador/sucursales";
    }

    @PostMapping("/sucursales/desactivar/{id}")
    public String desactivarSucursal(@PathVariable Long id) {
        try {
            Sucursal sucursal = sucursalService.findById(id);
            if (sucursal != null) {
                sucursal.setActivo(false);
                sucursalService.save(sucursal);
                LOGGER.info("Sucursal desactivada: {}", sucursal.getNombre());
            }
        } catch (Exception e) {
            LOGGER.error("Error desactivando sucursal: {}", e.getMessage());
        }
        return "redirect:/administrador/sucursales";
    }

    // === GESTIÓN GLOBAL DE PROMOCIONES ===

    @GetMapping("/promociones")
    public String promociones(Model model) {
        // TODO: Implementar servicio de promociones
        return "gerente/promociones";
    }

    // === GESTIÓN GLOBAL DE USUARIOS ===

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "gerente/usuarios";
    }

    @GetMapping("/personal")
    public String personal(Model model) {
        List<Personal> empleados = personalService.findAll();

        model.addAttribute("empleados", empleados);
        model.addAttribute("sucursales", sucursalService.findAll());

        return "gerente/personal";
    }

    /**
     * Promover empleado a admin de sucursal
     */
    @PostMapping("/personal/promover-admin")
    public String promoverAdminSucursal(@RequestParam Long empleadoId,
            @RequestParam Long sucursalId,
            HttpSession session) {
        try {
            Long gerenteId = Long.parseLong(session.getAttribute("idUsuario").toString());
            Usuario gerente = usuarioService.findById(gerenteId).orElse(null);
            Usuario empleado = usuarioService.findById(empleadoId).orElse(null);
            Sucursal sucursal = sucursalService.findById(sucursalId);

            if (gerente != null && empleado != null && sucursal != null) {
                // Cambiar rol
                String rolAnterior = empleado.getRol();
                empleado.setRol("ADMIN_SUCURSAL");
                empleado.setSucursalPreferida(sucursal);
                usuarioService.save(empleado);

                // Actualizar registro de personal
                List<Personal> personalList = personalService.findAll();
                for (Personal p : personalList) {
                    if (p.getUsuario() != null && p.getUsuario().getId().equals(empleadoId)) {
                        p.setSucursal(sucursal);
                        personalService.save(p);
                        break;
                    }
                }

                // TODO: Implementar auditoría de roles

                LOGGER.info("Empleado {} promovido a ADMIN_SUCURSAL por gerente {}",
                        empleado.getNombre(), gerente.getNombre());
            }
        } catch (Exception e) {
            LOGGER.error("Error promoviendo admin sucursal: {}", e.getMessage());
        }

        return "redirect:/administrador/personal";
    }

    /**
     * Desactivar usuario (borrado lógico)
     */
    @PostMapping("/usuarios/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.findById(id).orElse(null);
            if (usuario != null) {
                usuario.setActivo(false);
                usuarioService.save(usuario);
                LOGGER.info("Usuario desactivado: {}", usuario.getNombre());
            }
        } catch (Exception e) {
            LOGGER.error("Error desactivando usuario: {}", e.getMessage());
        }
        return "redirect:/administrador/usuarios";
    }

    // === GESTIÓN DE ESPECIALIDADES ===

    @GetMapping("/especialidades")
    public String especialidades(Model model) {
        // TODO: Implementar servicio de especialidades
        return "gerente/especialidades";
    }

    // === GESTIÓN DE PREFERENCIAS MUSICALES ===

    @GetMapping("/musica")
    public String preferenciasMusica(Model model) {
        // TODO: Implementar servicio de preferencias musicales
        return "gerente/musica";
    }

    // === AUDITORÍA Y TRAZABILIDAD ===

    @GetMapping("/auditoria")
    public String auditoriaRoles(Model model) {
        // TODO: Implementar servicio de auditoría
        return "gerente/auditoria";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        // Reportes globales del sistema (usando métodos existentes)
        model.addAttribute("totalUsuarios", usuarioService.findAll().size());
        model.addAttribute("totalSucursales", sucursalService.findAll().size());

        return "gerente/reportes";
    }
}