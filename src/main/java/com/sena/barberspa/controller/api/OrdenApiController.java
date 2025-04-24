package com.sena.barberspa.controller.api;

import com.sena.barberspa.model.Orden;
import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IOrdenService;
import com.sena.barberspa.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/ordenes")
public class OrdenApiController {

    @Autowired
    private IOrdenService ordenService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Orden>> listarOrdenes() {
        List<Orden> ordenes = ordenService.findAll();
        if (ordenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Orden>> listarOrdenesPorUsuario(@PathVariable Integer usuarioId) {
        Optional<Usuario> usuarioOptional = usuarioService.findById(usuarioId);
        if (usuarioOptional.isPresent()) {
            List<Orden> ordenes = ordenService.findByUsuario(usuarioOptional.get());
            return ResponseEntity.ok(ordenes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orden> obtenerOrdenPorId(@PathVariable Integer id) {
        Optional<Orden> ordenOptional = ordenService.findById(id);
        if (ordenOptional.isPresent()) {
            return ResponseEntity.ok(ordenOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Orden> crearOrden(@RequestBody Orden orden) {
        // Generar número de orden
        String numeroOrden = ordenService.generarNumeroOrden();
        orden.setNumero(numeroOrden);

        Orden nuevaOrden = ordenService.save(orden);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaOrden);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Orden> actualizarOrden(@PathVariable Integer id, @RequestBody Orden ordenDetalles) {
        Optional<Orden> ordenOptional = ordenService.findById(id);
        if (ordenOptional.isPresent()) {
            Orden ordenExistente = ordenOptional.get();
            ordenExistente.setEstado(ordenDetalles.getEstado());
            // Actualizar otros campos relevantes

            ordenService.update(ordenExistente);
            return ResponseEntity.ok(ordenExistente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Integer id) {
        if (ordenService.findById(id).isPresent()) {
            // Aquí deberías asegurarte que también se eliminen los detalles de la orden
            // o manejar esa lógica según tu diseño

            // For now, just delete the order
            // ordenService.delete(id); // Asumiendo que tienes un método delete

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}