package com.sena.barberspa.controller.api.v1;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.model.dto.AuthResponse;
import com.sena.barberspa.model.dto.LoginRequest;
import com.sena.barberspa.service.IUsuarioService;
import com.sena.barberspa.config.security.jwt.JwtProvider;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

        private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

        @Autowired
        private AuthenticationManagerBuilder authenticationManagerBuilder;

        @Autowired
        private JwtProvider jwtProvider;

        @Autowired
        private IUsuarioService usuarioService;

        @PostMapping("/login")
        public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
                try {
                        LOGGER.info("🔐 API Login attempt for email: {}", loginRequest.email());

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

                        // Verificar que el usuario esté activo
                        if (!usuario.getActivo() || usuario.isDeleted()) {
                                LOGGER.warn("❌ API Login attempt with inactive user: {}", loginRequest.email());
                                Map<String, Object> errorResponse = new HashMap<>();
                                errorResponse.put("error", "ACCOUNT_DISABLED");
                                errorResponse.put("message", "Tu cuenta está desactivada. Contacta al administrador.");
                                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
                        }

                        // 4. Generar el JWT
                        String jwt = jwtProvider.generateToken(usuario);

                        // 5. Devolver el JWT y los datos del usuario en la respuesta
                        AuthResponse authResponse = new AuthResponse(
                                        jwt,
                                        usuario.getId(), // El ID ya es Long
                                        usuario.getNombre(),
                                        usuario.getTelefono());

                        LOGGER.info("✅ API Login successful for user: {} (ID: {})", usuario.getNombre(), usuario.getId());
                        return ResponseEntity.ok(authResponse);

                } catch (BadCredentialsException e) {
                        LOGGER.warn("❌ API Login failed - Invalid credentials for: {}", loginRequest.email());
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("error", "INVALID_CREDENTIALS");
                        errorResponse.put("message", "Email o contraseña incorrectos");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

                } catch (AuthenticationException e) {
                        LOGGER.error("❌ API Login failed - Authentication error for {}: {}", loginRequest.email(), e.getMessage());
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("error", "AUTHENTICATION_FAILED");
                        errorResponse.put("message", "Error de autenticación");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

                } catch (Exception e) {
                        LOGGER.error("💥 API Login failed - Internal error for {}: {}", loginRequest.email(), e.getMessage(), e);
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("error", "INTERNAL_ERROR");
                        errorResponse.put("message", "Error interno del servidor");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                }
        }
}