package com.sena.barberspa.controller.api;

import com.sena.barberspa.model.Servicio;
import com.sena.barberspa.service.IServiciosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/servicios")
public class ServicioApiController {

    @Autowired
    private IServiciosService servicioService;

    @GetMapping
    public ResponseEntity<List<Servicio>> listarServicios() {
        List<Servicio> servicios = servicioService.findAll();
        if (servicios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(servicios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servicio> obtenerServicioPorId(@PathVariable Integer id) {
        Optional<Servicio> servicioOptional = servicioService.get(id);
        if (servicioOptional.isPresent()) {
            return ResponseEntity.ok(servicioOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Servicio> crearServicio(@RequestBody Servicio servicio) {
        Servicio nuevoServicio = servicioService.save(servicio);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoServicio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servicio> actualizarServicio(@PathVariable Integer id, @RequestBody Servicio servicioDetalles) {
        Optional<Servicio> servicioOptional = servicioService.get(id);
        if (servicioOptional.isPresent()) {
            Servicio servicioExistente = servicioOptional.get();
            servicioExistente.setNombre(servicioDetalles.getNombre());
            servicioExistente.setDescripcion(servicioDetalles.getDescripcion());
            servicioExistente.setPrecio(servicioDetalles.getPrecio());
            servicioExistente.setDuracion(servicioDetalles.getDuracion());
            // No modificamos el usuario ni la imagen para mantener integridad
            servicioService.update(servicioExistente);
            return ResponseEntity.ok(servicioExistente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable Integer id) {
        Optional<Servicio> servicioOptional = servicioService.get(id);
        if (servicioOptional.isPresent()) {
            servicioService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}