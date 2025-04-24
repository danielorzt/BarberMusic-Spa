package com.sena.barberspa.controller.api;

import com.sena.barberspa.model.Agendamiento;
import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IAgendamientosService;
import com.sena.barberspa.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/agendamientos")
public class AgendamientoApiController {

    @Autowired
    private IAgendamientosService agendamientoService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Agendamiento>> listarAgendamientos() {
        List<Agendamiento> agendamientos = agendamientoService.findAll();
        if (agendamientos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(agendamientos);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Agendamiento>> listarAgendamientosPorUsuario(@PathVariable Integer usuarioId) {
        Optional<Usuario> usuarioOptional = usuarioService.findById(usuarioId);
        if (usuarioOptional.isPresent()) {
            List<Agendamiento> agendamientos = agendamientoService.findByUsuario(usuarioOptional.get());
            return ResponseEntity.ok(agendamientos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamiento> obtenerAgendamientoPorId(@PathVariable Integer id) {
        Optional<Agendamiento> agendamientoOptional = agendamientoService.get(id);
        if (agendamientoOptional.isPresent()) {
            return ResponseEntity.ok(agendamientoOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Agendamiento> crearAgendamiento(@RequestBody Agendamiento agendamiento) {
        // Validaciones básicas
        if (agendamiento.getUsuario() == null || agendamiento.getServicio() == null || agendamiento.getSucursal() == null) {
            return ResponseEntity.badRequest().build();
        }

        Agendamiento nuevoAgendamiento = agendamientoService.save(agendamiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAgendamiento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agendamiento> actualizarAgendamiento(@PathVariable Integer id, @RequestBody Agendamiento agendamientoDetalles) {
        Optional<Agendamiento> agendamientoOptional = agendamientoService.get(id);
        if (agendamientoOptional.isPresent()) {
            Agendamiento agendamientoExistente = agendamientoOptional.get();
            agendamientoExistente.setFechaHora(agendamientoDetalles.getFechaHora());
            agendamientoExistente.setEstado(agendamientoDetalles.getEstado());
            agendamientoExistente.setMensaje(agendamientoDetalles.getMensaje());
            // No modificamos relaciones para mantener integridad
            agendamientoService.update(agendamientoExistente);
            return ResponseEntity.ok(agendamientoExistente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAgendamiento(@PathVariable Integer id) {
        if (agendamientoService.get(id).isPresent()) {
            agendamientoService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}