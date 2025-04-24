package com.sena.barberspa.controller.api;

import com.sena.barberspa.model.Producto;
import com.sena.barberspa.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/productos") // Prefijo para todos los endpoints de productos
public class ProductoApiController {

    @Autowired
    private IProductoService productoService;

    // GET /api/v1/productos - Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = productoService.findAll();
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build(); // Devuelve 204 No Content si no hay productos
        }
        return ResponseEntity.ok(productos); // Devuelve 200 OK con la lista de productos
    }

    // GET /api/v1/productos/{id} - Obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Integer id) {
        Optional<Producto> productoOptional = productoService.get(id);
        // productoOptional.map(ResponseEntity::ok) // Forma corta de hacer lo de abajo
        //                 .orElseGet(() -> ResponseEntity.notFound().build());
        if (productoOptional.isPresent()) {
            return ResponseEntity.ok(productoOptional.get()); // Devuelve 200 OK con el producto
        } else {
            return ResponseEntity.notFound().build(); // Devuelve 404 Not Found si no existe
        }
    }

    // POST /api/v1/productos - Crear un nuevo producto
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        // Aquí podrías añadir validaciones si es necesario
        Producto nuevoProducto = productoService.save(producto);
        // Considera devolver la URL del nuevo recurso creado en la cabecera 'Location'
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto); // Devuelve 201 Created
    }

    // PUT /api/v1/productos/{id} - Actualizar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Integer id, @RequestBody Producto productoDetalles) {
        Optional<Producto> productoOptional = productoService.get(id);
        if (productoOptional.isPresent()) {
            Producto productoExistente = productoOptional.get();
            // Actualiza los campos necesarios (evita sobrescribir el ID si viene en productoDetalles)
            productoExistente.setNombre(productoDetalles.getNombre());
            productoExistente.setDescripcion(productoDetalles.getDescripcion());
            productoExistente.setPrecio(productoDetalles.getPrecio());
            productoExistente.setCantidad(productoDetalles.getCantidad());
            // Asegúrate de manejar la imagen y otros campos si es necesario
            Producto productoActualizado = productoService.update(productoExistente); // Asumiendo que tienes un método update o usas save
            return ResponseEntity.ok(productoActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/v1/productos/{id} - Eliminar un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        // Primero verifica si existe para devolver 404 si no se encuentra
        Optional<Producto> productoOptional = productoService.get(id);
        if (productoOptional.isPresent()) {
            productoService.delete(id);
            return ResponseEntity.noContent().build(); // Devuelve 204 No Content indicando éxito sin cuerpo
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}