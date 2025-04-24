package com.sena.barberspa.controller.api;

import com.sena.barberspa.model.Sucursal;
import com.sena.barberspa.service.ISucursalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sucursales")
public class SucursalApiController {

    @Autowired
    private ISucursalesService sucursalService;

    @GetMapping
    public ResponseEntity<List<Sucursal>> listarSucursales() {
        List<Sucursal> sucursales = sucursalService.findAll();
        if (sucursales.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sucursales);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sucursal> obtenerSucursalPorId(@PathVariable Integer id) {
        Optional<Sucursal> sucursalOptional = sucursalService.get(id);
        return sucursalOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Sucursal> crearSucursal(@RequestBody Sucursal sucursal) {
        Sucursal nuevaSucursal = sucursalService.save(sucursal);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSucursal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sucursal> actualizarSucursal(@PathVariable Integer id, @RequestBody Sucursal sucursalDetalles) {
        Optional<Sucursal> sucursalOptional = sucursalService.get(id);
        if (sucursalOptional.isPresent()) {
            Sucursal sucursalExistente = sucursalOptional.get();
            sucursalExistente.setNombre(sucursalDetalles.getNombre());
            sucursalExistente.setDireccion(sucursalDetalles.getDireccion());
            sucursalExistente.setCiudad(sucursalDetalles.getCiudad());
            sucursalExistente.setHorarios(sucursalDetalles.getHorarios());
            sucursalExistente.setLink_maps(sucursalDetalles.getLink_maps());
            // Manejo de la imagen se debe hacer por separado
            sucursalService.update(sucursalExistente);
            return ResponseEntity.ok(sucursalExistente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSucursal(@PathVariable Integer id) {
        if (sucursalService.get(id).isPresent()) {
            sucursalService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}