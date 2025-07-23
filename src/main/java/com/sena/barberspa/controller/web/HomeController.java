package com.sena.barberspa.controller.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.barberspa.model.*;
import com.sena.barberspa.service.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/home")
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
    private static final String SESSION_USER_ID = "idUsuario";
    private static final String REDIRECT_LOGIN = "redirect:/usuario/login";

    @Autowired
    private IProductoService productoService;
    @Autowired
    private IServiciosService servicioService;
    @Autowired
    private ISucursalesService sucursalService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IOrdenService ordenService;
    @Autowired
    private IDetalleOrdenService detalleOrdenService;
    @Autowired
    private IAgendamientosService agendamientosService;

    private List<DetalleOrden> detalles = new ArrayList<>();
    private Orden orden = new Orden();

    @ModelAttribute
    public void addCommonAttributes(Model model, HttpSession session) {
        Object userIdObj = session.getAttribute(SESSION_USER_ID);
        if (userIdObj != null) {
            Long userId = Long.parseLong(userIdObj.toString());
            usuarioService.findById(userId).ifPresent(usuario -> {
                model.addAttribute("usuario", usuario);
                model.addAttribute("sesion", userId);
            });
        }
    }

    // Páginas principales
    @GetMapping({ "", "/" })
    public String home(Model model) {
        try {
            LOGGER.info("Loading home page...");
            model.addAttribute("productos", productoService.findAll());
            model.addAttribute("servicios", servicioService.findAll());
            model.addAttribute("sucursales", sucursalService.findAll());
            LOGGER.info("Home page loaded successfully");
            return "usuario/home";
        } catch (Exception e) {
            LOGGER.error("Error loading home page: {}", e.getMessage(), e);
            // Fallback: return a simple page without database data
            model.addAttribute("error", "Error cargando datos: " + e.getMessage());
            return "usuario/home";
        }
    }

    @GetMapping("/test-home")
    @ResponseBody
    public String testHome() {
        StringBuilder result = new StringBuilder();
        result.append("Testing HomeController...\n");

        try {
            result.append("Testing ProductoService...\n");
            List<Producto> productos = productoService.findAll();
            result.append("Productos count: ").append(productos.size()).append("\n");

            result.append("Testing ServiciosService...\n");
            List<Servicio> servicios = servicioService.findAll();
            result.append("Servicios count: ").append(servicios.size()).append("\n");

            result.append("Testing SucursalesService...\n");
            List<Sucursal> sucursales = sucursalService.findAll();
            result.append("Sucursales count: ").append(sucursales.size()).append("\n");

            result.append("All services working correctly!\n");

        } catch (Exception e) {
            result.append("Error: ").append(e.getMessage()).append("\n");
            result.append("Stack trace: ").append(e.getStackTrace()[0]).append("\n");
        }

        return result.toString();
    }

    @GetMapping("/mantenimiento")
    public String mantenimiento() {
        return "usuario/mantenimiento";
    }

    @GetMapping("/productosVista")
    public String productosVista(Model model) {
        model.addAttribute("productos", productoService.findAll());
        return "usuario/productosVista";
    }

    @GetMapping("/serviciosVista")
    public String serviciosVista(Model model) {
        model.addAttribute("servicios", servicioService.findAll());
        return "usuario/serviciosVista";
    }

    // Agendamientos
    @PostMapping("/save")
    public String saveAgendamiento(@RequestParam Long servicio,
            @RequestParam Long sucursal,
            @RequestParam String fechaHora,
            @RequestParam String mensaje,
            HttpSession session) throws IOException {

        Usuario usuario = getUsuarioFromSession(session)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Agendamiento agendamiento = new Agendamiento();
        LocalDateTime fechaInicio = parseDateTime(fechaHora);
        agendamiento.setFechaHoraInicio(fechaInicio);
        agendamiento.setFechaHoraFin(fechaInicio.plusHours(1)); // Duración predeterminada de 1 hora
        agendamiento.setNotasCliente(mensaje);
        agendamiento.setServicio(getServicio(servicio));
        agendamiento.setSucursal(getSucursal(sucursal));
        agendamiento.setEstado("PROGRAMADA");
        agendamiento.setClienteUsuario(usuario);
        agendamiento.setPrecioFinal(getServicio(servicio).getPrecioBase());

        agendamientosService.save(agendamiento);
        return "redirect:/home";
    }

    // Carrito de compras
    @GetMapping("productoHome/{id}")
    public String productoHome(@PathVariable Long id, Model model) {
        model.addAttribute("producto", getProducto(id));
        return "usuario/productoHome";
    }

    @PostMapping("/cart")
    public String addCart(@RequestParam Long id,
            @RequestParam Double cantidad,
            HttpSession session, Model model) {

        if (session.getAttribute(SESSION_USER_ID) == null) {
            return REDIRECT_LOGIN;
        }

        Producto producto = getProducto(id);
        addToCartIfNotExists(producto, cantidad);

        updateCartModel(model);
        return "usuario/carrito";
    }

    @GetMapping("/delete/cart/{id}")
    public String deleteProductoCart(@PathVariable Long id, Model model) {
        detalles.removeIf(d -> d.getProducto().getId().equals(id));
        updateCartModel(model);
        return "usuario/carrito";
    }

    @GetMapping("/getCart")
    public String getCart(Model model) {
        updateCartModel(model);
        return "usuario/carrito";
    }

    @GetMapping("/order")
    public String order(Model model) {
        model.addAttribute("cart", detalles);
        model.addAttribute("orden", orden);
        return "usuario/resumenorden";
    }

    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession session) {
        if (session.getAttribute(SESSION_USER_ID) == null) {
            return REDIRECT_LOGIN;
        }

        Usuario usuario = getUsuarioFromSession(session)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Orden ordenGuardada = createAndSaveOrder(usuario);
        session.setAttribute("ordenId", ordenGuardada.getId());

        return "redirect:/pagar/" + ordenGuardada.getId();
    }

    @PostMapping("/searchU")
    public String searchProducto(@RequestParam String nombreproducto, Model model) {
        model.addAttribute("productos", searchProducts(nombreproducto));
        return "usuario/productosVista";
    }

    // Métodos utilitarios privados
    private Optional<Usuario> getUsuarioFromSession(HttpSession session) {
        return Optional.ofNullable(session.getAttribute(SESSION_USER_ID))
                .map(Object::toString)
                .map(Long::parseLong)
                .flatMap(usuarioService::findById);
    }

    private Producto getProducto(Long id) {
        return productoService.get(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    private Servicio getServicio(Long id) {
        return servicioService.get(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
    }

    private Sucursal getSucursal(Long id) {
        return sucursalService.get(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
    }

    private LocalDateTime parseDateTime(String fechaHora) {
        return LocalDateTime.parse(fechaHora, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }

    private void addToCartIfNotExists(Producto producto, Double cantidad) {
        if (detalles.stream().noneMatch(d -> d.getProducto().getId().equals(producto.getId()))) {
            DetalleOrden detalle = new DetalleOrden();
            detalle.setCantidadInt(cantidad.intValue());
            java.math.BigDecimal precioBigDecimal = producto.getPrecio();
            detalle.setPrecioUnitarioHistorico(precioBigDecimal);
            detalle.setNombreProductoHistorico(producto.getNombre());
            java.math.BigDecimal cantidadBigDecimal = java.math.BigDecimal.valueOf(cantidad);
            detalle.setSubtotalLinea(precioBigDecimal.multiply(cantidadBigDecimal));
            detalle.setProducto(producto);
            detalles.add(detalle);
        }
    }

    private void updateCartModel(Model model) {
        java.math.BigDecimal sumaTotal = detalles.stream()
                .map(DetalleOrden::getSubtotalLinea)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        orden.setTotalOrden(sumaTotal);
        model.addAttribute("cart", detalles);
        model.addAttribute("orden", orden);
    }

    private Orden createAndSaveOrder(Usuario usuario) {
        orden.setFechaOrden(java.time.LocalDateTime.now());
        orden.setNumeroOrden(generarNumeroOrden());
        orden.setClienteUsuario(usuario);
        orden.setEstadoOrden("PENDIENTE_PAGO");
        orden.setSubtotal(calcularSubtotal());
        orden.setDescuentoTotal(java.math.BigDecimal.ZERO);
        orden.setImpuestosTotal(java.math.BigDecimal.ZERO);

        Orden ordenGuardada = ordenService.save(orden);

        detalles.forEach(dt -> {
            dt.setOrden(ordenGuardada);
            detalleOrdenService.save(dt);
        });

        // Limpiar carrito
        orden = new Orden();
        detalles.clear();

        return ordenGuardada;
    }

    private List<Producto> searchProducts(String nombre) {
        return productoService.findAll().stream()
                .filter(p -> p.getNombre().toUpperCase().contains(nombre.toUpperCase()))
                .collect(Collectors.toList());
    }

    private String generarNumeroOrden() {
        return "ORD-"
                + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    private java.math.BigDecimal calcularSubtotal() {
        return detalles.stream()
                .map(DetalleOrden::getSubtotalLinea)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}