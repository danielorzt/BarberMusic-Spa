package com.sena.barberspa.controller.adminsucursal;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sena.barberspa.model.*;
import com.sena.barberspa.model.enums.RolUsuario;
import com.sena.barberspa.service.*;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador para Administradores de Sucursal
 * ROL: ADMIN_SUCURSAL
 * Gestiona una sucursal específica: productos, servicios, horarios, personal,
 * reseñas
 */
@Controller
@RequestMapping("/admin-sucursal")
@PreAuthorize("hasRole('ADMIN_SUCURSAL')")
public class AdminSucursalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminSucursalController.class);

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IServiciosService servicioService;

    @Autowired
    private ISucursalesService sucursalService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IPersonalService personalService;

    /**
     * Panel principal del administrador de sucursal
     */
    @GetMapping({ "", "/" })
    public String panel(Model model, HttpSession session) {
        try {
            Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
            Usuario admin = usuarioService.findById(userId).orElse(null);

            if (admin != null) {
                model.addAttribute("admin", admin);

                // Estadísticas generales
                model.addAttribute("totalProductos", productoService.findAll().size());
                model.addAttribute("totalServicios", servicioService.findAll().size());
                model.addAttribute("totalPersonal", personalService.findAll().size());
                model.addAttribute("totalSucursales", sucursalService.findAll().size());
            }

            return "admin-sucursal/panel";
        } catch (Exception e) {
            LOGGER.error("Error en panel admin sucursal: {}", e.getMessage());
            model.addAttribute("error", "Error cargando panel");
            return "admin-sucursal/panel";
        }
    }

    // === GESTIÓN DE PRODUCTOS ===

    @GetMapping("/productos")
    public String productos(Model model, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
        Usuario admin = usuarioService.findById(userId).orElse(null);

        if (admin != null) {
            List<Producto> productos = productoService.findAll();
            model.addAttribute("productos", productos);
            model.addAttribute("admin", admin);
        }

        return "admin-sucursal/productos";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProducto(Model model, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
        Usuario admin = usuarioService.findById(userId).orElse(null);

        model.addAttribute("producto", new Producto());
        model.addAttribute("admin", admin);
        return "admin-sucursal/producto-form";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, @RequestParam("precioString") String precioString,
            HttpSession session) {
        try {
            Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
            Usuario admin = usuarioService.findById(userId).orElse(null);

            if (admin != null) {
                // Convertir el precio recibido como String a BigDecimal
                if (precioString != null && !precioString.isEmpty()) {
                    producto.setPrecio(new java.math.BigDecimal(precioString.replace(",", ".")));
                }
                productoService.save(producto);
            }
        } catch (Exception e) {
            LOGGER.error("Error guardando producto: {}", e.getMessage());
        }

        return "redirect:/admin-sucursal/productos";
    }

    // === GESTIÓN DE SERVICIOS ===

    @GetMapping("/servicios")
    public String servicios(Model model, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
        Usuario admin = usuarioService.findById(userId).orElse(null);

        if (admin != null) {
            List<Servicio> servicios = servicioService.findAll();
            model.addAttribute("servicios", servicios);
            model.addAttribute("admin", admin);
        }

        return "admin-sucursal/servicios";
    }

    // === GESTIÓN DE HORARIOS ===

    @GetMapping("/horarios")
    public String horarios(Model model, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
        Usuario admin = usuarioService.findById(userId).orElse(null);

        if (admin != null) {
            model.addAttribute("admin", admin);
            model.addAttribute("sucursales", sucursalService.findAll());
        }

        return "admin-sucursal/horarios";
    }

    // === GESTIÓN DE PERSONAL ===

    @GetMapping("/personal")
    public String personal(Model model, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
        Usuario admin = usuarioService.findById(userId).orElse(null);

        if (admin != null) {
            List<Personal> personal = personalService.findAll();
            List<Usuario> clientes = usuarioService.findAll();

            model.addAttribute("personal", personal);
            model.addAttribute("clientes", clientes);
            model.addAttribute("admin", admin);
        }

        return "admin-sucursal/personal";
    }

    /**
     * Promover cliente a empleado en esta sucursal
     */
    @PostMapping("/personal/promover")
    public String promoverEmpleado(@RequestParam Long clienteId, HttpSession session) {
        try {
            Long adminId = Long.parseLong(session.getAttribute("idUsuario").toString());
            Usuario admin = usuarioService.findById(adminId).orElse(null);
            Usuario cliente = usuarioService.findById(clienteId).orElse(null);

            if (admin != null && cliente != null) {
                // Cambiar rol de cliente a empleado
                cliente.setRol(RolUsuario.EMPLEADO);
                usuarioService.save(cliente);

                // Crear registro en personal
                Personal nuevoEmpleado = new Personal();
                nuevoEmpleado.setUsuario(cliente);
                nuevoEmpleado.setTipoPersonal("ESTILISTA");
                nuevoEmpleado.setActivoEnEmpresa(true);
                personalService.save(nuevoEmpleado);

                LOGGER.info("Cliente {} promovido a empleado por admin {}",
                        cliente.getNombre(), admin.getNombre());
            }
        } catch (Exception e) {
            LOGGER.error("Error promoviendo empleado: {}", e.getMessage());
        }

        return "redirect:/admin-sucursal/personal";
    }

    // === GESTIÓN DE RESEÑAS ===

    @GetMapping("/resenas")
    public String resenas(Model model, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("idUsuario").toString());
        Usuario admin = usuarioService.findById(userId).orElse(null);

        if (admin != null) {
            model.addAttribute("admin", admin);
        }

        return "admin-sucursal/resenas";
    }
}