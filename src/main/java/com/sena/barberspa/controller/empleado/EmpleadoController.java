package com.sena.barberspa.controller.empleado;

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
 * Controlador para empleados - Gestión de agenda y órdenes asignadas
 * ROL: EMPLEADO
 */
@Controller
@RequestMapping("/empleado")
@PreAuthorize("hasRole('EMPLEADO')")
public class EmpleadoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpleadoController.class);

    @Autowired
    private IAgendamientosService agendamientosService;

    @Autowired
    private IOrdenService ordenService;

    @Autowired
    private IRecordatorioService recordatoriosService;

    @Autowired
    private IUsuarioService usuarioService;

    /**
     * Panel principal del empleado
     */
    @GetMapping({ "", "/" })
    public String panel(Model model, HttpSession session) {
        try {
            Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
            Usuario empleado = usuarioService.findById(userId).orElse(null);

            if (empleado != null) {
                model.addAttribute("empleado", empleado);

                // Citas asignadas al empleado (usar método existente)
                List<Agendamiento> citasAsignadas = agendamientosService.findAll();
                model.addAttribute("citas", citasAsignadas);

                // Órdenes pendientes (usar método existente)
                List<Orden> ordenesPendientes = ordenService.findAll();
                model.addAttribute("ordenes", ordenesPendientes);

                // Recordatorios del empleado (usar método existente)
                List<Recordatorio> recordatorios = recordatoriosService.findAll();
                model.addAttribute("recordatorios", recordatorios);
            }

            return "empleado/panel";
        } catch (Exception e) {
            LOGGER.error("Error en panel empleado: {}", e.getMessage());
            model.addAttribute("error", "Error cargando panel del empleado");
            return "empleado/panel";
        }
    }

    /**
     * Gestión de agenda del empleado
     */
    @GetMapping("/agenda")
    public String agenda(Model model, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
        List<Agendamiento> agenda = agendamientosService.findAll();
        model.addAttribute("agendamientos", agenda);
        return "empleado/agenda";
    }

    /**
     * Actualizar estado de una cita
     */
    @PostMapping("/agenda/actualizar")
    public String actualizarCita(@RequestParam Long agendamientoId,
            @RequestParam String nuevoEstado) {
        try {
            Agendamiento agendamiento = agendamientosService.findById(agendamientoId);
            if (agendamiento != null) {
                agendamiento.setEstado(nuevoEstado);
                agendamientosService.save(agendamiento);
            }
        } catch (Exception e) {
            LOGGER.error("Error actualizando cita: {}", e.getMessage());
        }
        return "redirect:/empleado/agenda";
    }

    /**
     * Ver órdenes para preparación
     */
    @GetMapping("/ordenes")
    public String ordenes(Model model, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
        Usuario empleado = usuarioService.findById(userId).orElse(null);

        if (empleado != null) {
            List<Orden> ordenes = ordenService.findAll();
            model.addAttribute("ordenes", ordenes);
        }

        return "empleado/ordenes";
    }

    /**
     * Gestión de recordatorios personales
     */
    @GetMapping("/recordatorios")
    public String recordatorios(Model model, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
        List<Recordatorio> recordatorios = recordatoriosService.findAll();
        model.addAttribute("recordatorios", recordatorios);
        return "empleado/recordatorios";
    }

    /**
     * Crear nuevo recordatorio
     */
    @PostMapping("/recordatorios/crear")
    public String crearRecordatorio(@RequestParam String titulo,
            @RequestParam String mensaje,
            @RequestParam String fechaRecordatorio,
            HttpSession session) {
        try {
            Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
            Usuario empleado = usuarioService.findById(userId).orElse(null);

            if (empleado != null) {
                Recordatorio recordatorio = new Recordatorio();
                recordatorio.setTitulo(titulo);
                recordatorio.setDescripcion(mensaje);
                recordatorio.setFechaHoraRecordatorio(java.time.LocalDateTime.parse(fechaRecordatorio));
                recordatorio.setUsuario(empleado);
                recordatorio.setActivo(true);

                recordatoriosService.save(recordatorio);
            }
        } catch (Exception e) {
            LOGGER.error("Error creando recordatorio: {}", e.getMessage());
        }

        return "redirect:/empleado/recordatorios";
    }
}