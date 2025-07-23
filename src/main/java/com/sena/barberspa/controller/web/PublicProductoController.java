package com.sena.barberspa.controller.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sena.barberspa.model.Producto;
import com.sena.barberspa.service.IProductoService;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PublicProductoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicProductoController.class);

    @Autowired
    private IProductoService productoService;

    @GetMapping("/productosVista")
    public String productosVista(Model model) {
        try {
            LOGGER.info("Loading productos vista page...");
            List<Producto> productos = productoService.findAll();
            model.addAttribute("productos", productos);
            LOGGER.info("Productos vista loaded successfully with {} products", productos.size());
            return "usuario/productosVista";
        } catch (Exception e) {
            LOGGER.error("Error loading productos vista: {}", e.getMessage(), e);
            model.addAttribute("error", "Error cargando productos: " + e.getMessage());
            return "usuario/productosVista";
        }
    }

    @GetMapping("/productoHome/{id}")
    public String productoHome(@PathVariable Long id, Model model) {
        try {
            LOGGER.info("Loading producto home for ID: {}", id);
            Producto producto = productoService.get(id).orElse(null);
            if (producto != null) {
                model.addAttribute("producto", producto);
                LOGGER.info("Producto home loaded successfully for: {}", producto.getNombreproducto());
                return "usuario/productoHome";
            } else {
                LOGGER.warn("Producto not found with ID: {}", id);
                model.addAttribute("error", "Producto no encontrado");
                return "usuario/productosVista";
            }
        } catch (Exception e) {
            LOGGER.error("Error loading producto home: {}", e.getMessage(), e);
            model.addAttribute("error", "Error cargando producto: " + e.getMessage());
            return "usuario/productosVista";
        }
    }

    @PostMapping("/searchProductos")
    public String searchProducto(@RequestParam String nombreproducto, Model model) {
        try {
            LOGGER.info("Searching productos with term: {}", nombreproducto);
            List<Producto> productos = productoService.findAll().stream()
                    .filter(p -> p.getNombreproducto().toUpperCase().contains(nombreproducto.toUpperCase()))
                    .collect(Collectors.toList());
            model.addAttribute("productos", productos);
            model.addAttribute("searchTerm", nombreproducto);
            LOGGER.info("Search completed, found {} productos", productos.size());
            return "usuario/productosVista";
        } catch (Exception e) {
            LOGGER.error("Error searching productos: {}", e.getMessage(), e);
            model.addAttribute("error", "Error en la búsqueda: " + e.getMessage());
            return "usuario/productosVista";
        }
    }
}