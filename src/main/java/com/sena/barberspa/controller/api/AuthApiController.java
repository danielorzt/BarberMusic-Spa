package com.sena.barberspa.controller.api;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Email y contraseña son requeridos");
        }

        Optional<Usuario> usuarioOptional = usuarioService.findByEmail(email);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                // Crear un objeto de respuesta sin exponer datos sensibles
                Map<String, Object> response = new HashMap<>();
                response.put("id", usuario.getId());
                response.put("nombre", usuario.getNombre());
                response.put("email", usuario.getEmail());
                response.put("tipo", usuario.getTipo());
                response.put("direccion", usuario.getDireccion());
                response.put("telefono", usuario.getTelefono());
                // No incluir password

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody Usuario usuario) {
        // Verificar si el email ya está registrado
        if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El email ya está registrado");
        }

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setTipo("USER"); // Por defecto todos son USER

        Usuario nuevoUsuario = usuarioService.save(usuario);

        // Crear respuesta sin datos sensibles
        Map<String, Object> response = new HashMap<>();
        response.put("id", nuevoUsuario.getId());
        response.put("nombre", nuevoUsuario.getNombre());
        response.put("email", nuevoUsuario.getEmail());
        response.put("mensaje", "Usuario registrado correctamente");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}