package com.sena.barberspa.controller.admin;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sena.barberspa.model.*;
import com.sena.barberspa.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/administrador")
@PreAuthorize("hasRole('ADMIN_GENERAL') or hasRole('GERENTE')")
public class AdminController {

    private final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private IProductoService productoService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IOrdenService ordenService;
    @Autowired
    private IServiciosService servicioService;
    @Autowired
    private ISucursalesService sucursalService;
    @Autowired
    private UploadFileService upload;
    @Autowired
    private IRecordatorioService recordatorioService;

    @ModelAttribute
    public void addGlobalAttributesToModel(Model model, HttpSession session) {
        Object idUsuarioObj = session.getAttribute("idUsuario");
        LOGGER.debug("Intentando añadir atributos globales para idUsuario en sesión: {}", idUsuarioObj);
        if (idUsuarioObj != null) {
            Long idUsuario = Long.parseLong(idUsuarioObj.toString());
            Usuario usuario = usuarioService.findById(idUsuario).orElse(null);
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
                try {
                    recordatorioService.procesarAgendamientosProximos(usuario, 3);
                } catch (Exception e) {
                    LOGGER.error("Error procesando agendamientos próximos para usuario {}", idUsuario, e);
                }

                List<Recordatorio> recordatorios = recordatorioService.findByUsuario(usuario);
                model.addAttribute("recordatorios", recordatorios);
                LOGGER.debug("Añadidos {} recordatorios para usuario {}", recordatorios.size(), idUsuario);

                long numeroDeOrdenes = ordenService.countAll();
                model.addAttribute("totalOrdenes", numeroDeOrdenes);
                LOGGER.debug("Añadido conteo total de órdenes: {}", numeroDeOrdenes);

            } else {
                LOGGER.warn("Usuario no encontrado para el idUsuario {} en sesión.", idUsuario);
            }
        } else {
            LOGGER.warn("No se encontró 'idUsuario' en la sesión.");
        }
    }

    @GetMapping("")
    public String showAdminHome(
            @ModelAttribute("searchTerm") String busqueda,
            Model model) {

        LOGGER.info("GET /administrador - Cargando dashboard. Término de búsqueda recibido: '{}'", busqueda);

        List<Producto> productos;
        List<Servicio> servicios;
        List<Sucursal> sucursales;

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            LOGGER.info("Realizando búsqueda filtrada para: {}", busqueda);
            String upperBusqueda = busqueda.toUpperCase();

            productos = productoService.findAll().stream()
                    .filter(p -> p.getNombreproducto().toUpperCase().contains(upperBusqueda))
                    .collect(Collectors.toList());
            servicios = servicioService.findAll().stream()
                    .filter(s -> s.getNombre().toUpperCase().contains(upperBusqueda))
                    .collect(Collectors.toList());
            sucursales = sucursalService.findAll().stream()
                    .filter(suc -> suc.getNombre().toUpperCase().contains(upperBusqueda))
                    .collect(Collectors.toList());

            model.addAttribute("busqueda", busqueda);
            LOGGER.info("Resultados filtrados - Productos: {}, Servicios: {}, Sucursales: {}", productos.size(),
                    servicios.size(), sucursales.size());

        } else {
            LOGGER.info("No se proporcionó término de búsqueda, cargando todos los items.");
            productos = productoService.findAll();
            servicios = servicioService.findAll();
            sucursales = sucursalService.findAll();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("servicios", servicios);
        model.addAttribute("sucursales", sucursales);

        return "administrador/home";
    }

    @PostMapping("/searchA")
    public String handleSearchPost(@RequestParam String busqueda, RedirectAttributes redirectAttributes) {
        LOGGER.info("POST /administrador/searchA - Recibida búsqueda para: '{}'", busqueda);
        redirectAttributes.addFlashAttribute("searchTerm", busqueda);
        return "redirect:/administrador";
    }

    @GetMapping("/ordenes")
    public String ordenes(Model model) {
        model.addAttribute("ordenes", ordenService.findAll());
        return "administrador/ordenes";
    }

    @GetMapping("/profile")
    public String editProfile(Model model, HttpSession session) {
        Object idUsuarioObj = session.getAttribute("idUsuario");
        if (idUsuarioObj == null) {
            LOGGER.warn("Acceso a perfil denegado - idUsuario no encontrado en sesión.");
            return "redirect:/login";
        }
        Long idUsuario = Long.parseLong(idUsuarioObj.toString());
        Usuario usuario = usuarioService.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
        model.addAttribute("usuario", usuario);
        return "administrador/profile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(HttpSession session,
            @RequestParam("nombre") String nombre,
            @RequestParam("email") String email,
            @RequestParam("telefono") String telefono,
            @RequestParam("direccion") String direccion,
            @RequestParam(value = "img", required = false) MultipartFile file) throws IOException {

        Object idUsuarioObj = session.getAttribute("idUsuario");
        if (idUsuarioObj == null) {
            LOGGER.warn("Actualización de perfil fallida - idUsuario no encontrado en sesión.");
            return "redirect:/login";
        }
        Long idUsuario = Long.parseLong(idUsuarioObj.toString());

        Usuario u = usuarioService.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        LOGGER.info("Actualizando perfil del usuario con ID {}", idUsuario);

        u.setNombre(nombre);
        u.setEmail(email);
        u.setTelefono(telefono);
        // u.setDireccion(direccion); // Comentado temporalmente - TODO: implementar
        // relación con direcciones

        if (file != null && !file.isEmpty()) {
            if (!file.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("Solo se permiten archivos de imagen");
            }

            String oldImage = u.getImagenPath();
            String nombreImagen = upload.saveImages(file, u.getNombre());
            u.setImagenPath(nombreImagen);

            if (oldImage != null && !oldImage.isEmpty() && !"default.jpg".equals(oldImage)) {
                upload.deleteImage(oldImage);
            }
        }

        usuarioService.update(u);
        LOGGER.info("Perfil actualizado correctamente para el usuario {}", nombre);

        return "redirect:/administrador/profile";
    }

    @GetMapping("/detalle/{id}")
    public String detalleorden(@PathVariable Long id, Model model) {
        LOGGER.info("GET /administrador/detalle/{} - Solicitando detalles de orden", id);
        Optional<Orden> ordenOpt = ordenService.findById(id);
        if (ordenOpt.isPresent()) {
            Orden orden = ordenOpt.get();
            model.addAttribute("orden", orden);
            model.addAttribute("detalles", orden.getDetalles());
            return "administrador/detalleorden";
        } else {
            LOGGER.warn("Orden con ID {} no encontrada.", id);
            return "redirect:/administrador/ordenes";
        }
    }

    @GetMapping("/nuevo")
    public String nuevoAdmin() {
        LOGGER.info("Redirigiendo desde /administrador/nuevo hacia /admin/usuarios");
        return "redirect:/admin/usuarios";
    }
}