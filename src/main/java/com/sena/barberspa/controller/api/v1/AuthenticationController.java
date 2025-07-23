package com.sena.barberspa.controller.api.v1;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.model.dto.AuthResponse;
import com.sena.barberspa.model.dto.LoginRequest;
import com.sena.barberspa.service.IUsuarioService;
import com.sena.barberspa.config.security.jwt.JwtProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

        @Autowired
        private AuthenticationManagerBuilder authenticationManagerBuilder;

        @Autowired
        private JwtProvider jwtProvider;

        @Autowired
        private IUsuarioService usuarioService;

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
                // 1. Crear el token de autenticación con las credenciales
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginRequest.email(), loginRequest.password());

                // 2. Autenticar al usuario
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 3. Obtener el usuario desde el servicio para tener el objeto completo
                Usuario usuario = usuarioService.findByEmail(loginRequest.email())
                                .orElseThrow(() -> new IllegalStateException(
                                                "Usuario no encontrado después de la autenticación"));

                // 4. Generar el JWT
                String jwt = jwtProvider.generateToken(usuario);

                // 5. Devolver el JWT y los datos del usuario en la respuesta
                AuthResponse authResponse = new AuthResponse(
                                jwt,
                                usuario.getId().longValue(), // Convertir Integer a Long
                                usuario.getNombre(), // Usar solo getNombre()
                                usuario.getTelefono());

                return ResponseEntity.ok(authResponse);
        }
}