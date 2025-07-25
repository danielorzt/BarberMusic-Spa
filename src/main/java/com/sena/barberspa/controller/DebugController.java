package com.sena.barberspa.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;
import com.sena.barberspa.model.Producto;
import com.sena.barberspa.service.IProductoService;
import com.sena.barberspa.model.Servicio;
import com.sena.barberspa.service.IServiciosService;
import com.sena.barberspa.model.Sucursal;
import com.sena.barberspa.service.ISucursalesService;
import com.sena.barberspa.service.UserDetailServiceImplement;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Controller
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IServiciosService servicioService;

    @Autowired
    private ISucursalesService sucursalService;
    
    @Autowired
    private UserDetailServiceImplement userDetailService;
    
    @Autowired
    private AuthenticationProvider authenticationProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/debug/security")
    @ResponseBody
    public String debugSecurity(HttpSession session) {
        StringBuilder debug = new StringBuilder();

        // Get current authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        debug.append("Authentication: ").append(auth != null ? auth.getName() : "null").append("\n");
        debug.append("Authorities: ").append(auth != null ? auth.getAuthorities() : "null").append("\n");
        debug.append("Authenticated: ").append(auth != null ? auth.isAuthenticated() : "false").append("\n");

        // Get user from session
        Object userIdObj = session.getAttribute("idUsuario");
        debug.append("Session User ID: ").append(userIdObj).append("\n");

        if (userIdObj != null) {
            try {
                Long userId = Long.parseLong(userIdObj.toString());
                Usuario usuario = usuarioService.findById(userId).orElse(null);
                if (usuario != null) {
                    debug.append("User Name: ").append(usuario.getNombre()).append("\n");
                    debug.append("User Email: ").append(usuario.getEmail()).append("\n");
                    debug.append("User Role: ").append(usuario.getRol()).append("\n");
                    debug.append("User Status: ").append(usuario.getActivo()).append("\n");
                } else {
                    debug.append("User not found in database\n");
                }
            } catch (Exception e) {
                debug.append("Error getting user: ").append(e.getMessage()).append("\n");
            }
        }

        return debug.toString();
    }

    @GetMapping("/debug/database")
    @ResponseBody
    public String debugDatabase() {
        StringBuilder debug = new StringBuilder();

        try {
            // Test ProductoService
            debug.append("Testing ProductoService...\n");
            List<Producto> productos = productoService.findAll();
            debug.append("Productos count: ").append(productos.size()).append("\n");

            // Test ServiciosService
            debug.append("Testing ServiciosService...\n");
            List<Servicio> servicios = servicioService.findAll();
            debug.append("Servicios count: ").append(servicios.size()).append("\n");

            // Test SucursalesService
            debug.append("Testing SucursalesService...\n");
            List<Sucursal> sucursales = sucursalService.findAll();
            debug.append("Sucursales count: ").append(sucursales.size()).append("\n");

            debug.append("All database tests passed successfully!\n");

        } catch (Exception e) {
            debug.append("Database error: ").append(e.getMessage()).append("\n");
            debug.append("Stack trace: ").append(e.getStackTrace()[0]).append("\n");
        }

        return debug.toString();
    }

    @GetMapping("/debug/home")
    @ResponseBody
    public String debugHome() {
        StringBuilder debug = new StringBuilder();

        try {
            debug.append("Testing HomeController services...\n");

            // Test each service individually
            try {
                List<Producto> productos = productoService.findAll();
                debug.append("✓ Productos loaded: ").append(productos.size()).append("\n");
            } catch (Exception e) {
                debug.append("✗ Productos error: ").append(e.getMessage()).append("\n");
            }

            try {
                List<Servicio> servicios = servicioService.findAll();
                debug.append("✓ Servicios loaded: ").append(servicios.size()).append("\n");
            } catch (Exception e) {
                debug.append("✗ Servicios error: ").append(e.getMessage()).append("\n");
            }

            try {
                List<Sucursal> sucursales = sucursalService.findAll();
                debug.append("✓ Sucursales loaded: ").append(sucursales.size()).append("\n");
            } catch (Exception e) {
                debug.append("✗ Sucursales error: ").append(e.getMessage()).append("\n");
            }

        } catch (Exception e) {
            debug.append("General error: ").append(e.getMessage()).append("\n");
            debug.append("Stack trace: ").append(e.getStackTrace()[0]).append("\n");
        }

        return debug.toString();
    }

    @GetMapping("/debug/usuario-issue")
    @ResponseBody
    public String debugUsuarioIssue() {
        StringBuilder debug = new StringBuilder();

        try {
            debug.append("=== DEBUG USUARIO ISSUE ===\n");

            // Test 1: Check if any service is trying to find Usuario with id 0
            debug.append("1. Testing ProductoService.findAll()...\n");
            try {
                List<Producto> productos = productoService.findAll();
                debug.append("   ✓ Productos loaded: ").append(productos.size()).append("\n");

                // Check if any producto has a relationship that might cause the issue
                for (Producto p : productos) {
                    debug.append("   - Producto ID: ").append(p.getId()).append(", Nombre: ")
                            .append(p.getNombreproducto()).append("\n");
                }
            } catch (Exception e) {
                debug.append("   ✗ Productos error: ").append(e.getMessage()).append("\n");
                debug.append("   Stack trace: ").append(e.getStackTrace()[0]).append("\n");
            }

            // Test 2: Check if the issue is in the repository layer
            debug.append("\n2. Testing direct repository access...\n");
            try {
                // This will help us see if the issue is in the service layer or repository
                debug.append("   Testing repository directly...\n");
                // We'll add repository tests here if needed
            } catch (Exception e) {
                debug.append("   ✗ Repository error: ").append(e.getMessage()).append("\n");
            }

            // Test 3: Check database connection and basic queries
            debug.append("\n3. Testing basic database connectivity...\n");
            try {
                // Test a simple count query
                debug.append("   Testing simple count queries...\n");
                // Add simple count tests here
            } catch (Exception e) {
                debug.append("   ✗ Database connectivity error: ").append(e.getMessage()).append("\n");
            }

        } catch (Exception e) {
            debug.append("General error: ").append(e.getMessage()).append("\n");
            debug.append("Stack trace: ").append(e.getStackTrace()[0]).append("\n");
        }

        return debug.toString();
    }

    @GetMapping("/debug/authentication")
    @ResponseBody
    public String debugAuthentication() {
        StringBuilder debug = new StringBuilder();
        
        try {
            debug.append("=== AUTHENTICATION DEBUG ===\n\n");
            
            // Test 1: Check AuthenticationProvider
            debug.append("1. AuthenticationProvider Status:\n");
            debug.append("   - Provider class: ").append(authenticationProvider.getClass().getName()).append("\n");
            debug.append("   - Provider available: ").append(authenticationProvider != null).append("\n\n");
            
            // Test 2: Check UserDetailsService
            debug.append("2. UserDetailsService Status:\n");
            debug.append("   - Service class: ").append(userDetailService.getClass().getName()).append("\n");
            debug.append("   - Service available: ").append(userDetailService != null).append("\n\n");
            
            // Test 3: Check PasswordEncoder
            debug.append("3. PasswordEncoder Status:\n");
            debug.append("   - Encoder class: ").append(passwordEncoder.getClass().getName()).append("\n");
            debug.append("   - Encoder available: ").append(passwordEncoder != null).append("\n\n");
            
            // Test 4: Test loading a user (try with a common email)
            debug.append("4. Testing UserDetailsService load:\n");
            try {
                // Try to load a test user - let's see if there are any users in the DB
                List<Usuario> usuarios = usuarioService.findAll();
                debug.append("   - Total users in DB: ").append(usuarios.size()).append("\n");
                
                if (!usuarios.isEmpty()) {
                    Usuario testUser = usuarios.get(0);
                    debug.append("   - Testing with user: ").append(testUser.getEmail()).append("\n");
                    debug.append("   - User role: ").append(testUser.getRol()).append("\n");
                    debug.append("   - User active: ").append(testUser.getActivo()).append("\n");
                    
                    try {
                        UserDetails userDetails = userDetailService.loadUserByUsername(testUser.getEmail());
                        debug.append("   ✓ UserDetails loaded successfully\n");
                        debug.append("   - Username: ").append(userDetails.getUsername()).append("\n");
                        debug.append("   - Authorities: ").append(userDetails.getAuthorities()).append("\n");
                        debug.append("   - Account non-expired: ").append(userDetails.isAccountNonExpired()).append("\n");
                        debug.append("   - Account non-locked: ").append(userDetails.isAccountNonLocked()).append("\n");
                        debug.append("   - Credentials non-expired: ").append(userDetails.isCredentialsNonExpired()).append("\n");
                        debug.append("   - Enabled: ").append(userDetails.isEnabled()).append("\n");
                    } catch (Exception e) {
                        debug.append("   ✗ Error loading UserDetails: ").append(e.getMessage()).append("\n");
                    }
                } else {
                    debug.append("   - No users found in database\n");
                }
            } catch (Exception e) {
                debug.append("   ✗ Error accessing users: ").append(e.getMessage()).append("\n");
            }
            
            debug.append("\n=== END AUTHENTICATION DEBUG ===\n");
            
        } catch (Exception e) {
            debug.append("General error: ").append(e.getMessage()).append("\n");
            debug.append("Stack trace: ").append(e.getStackTrace()[0]).append("\n");
        }
        
        return debug.toString();
    }

    @GetMapping("/debug/create-test-user")
    @ResponseBody
    public String createTestUser() {
        StringBuilder debug = new StringBuilder();
        
        try {
            debug.append("=== CREATING TEST USER ===\n\n");
            
            String testEmail = "test@barberspa.com";
            String testPassword = "123456";
            
            // Check if user already exists
            Optional<Usuario> existingUser = usuarioService.findByEmail(testEmail);
            if (existingUser.isPresent()) {
                debug.append("✓ Test user already exists: ").append(testEmail).append("\n");
                Usuario user = existingUser.get();
                debug.append("   - User ID: ").append(user.getId()).append("\n");
                debug.append("   - User name: ").append(user.getNombre()).append("\n");
                debug.append("   - User role: ").append(user.getRol()).append("\n");
                debug.append("   - User active: ").append(user.getActivo()).append("\n");
                return debug.toString();
            }
            
            // Create new test user
            Usuario testUser = new Usuario();
            testUser.setNombre("Usuario Test");
            testUser.setEmail(testEmail);
            testUser.setPassword(passwordEncoder.encode(testPassword));
            testUser.setRol(com.sena.barberspa.model.enums.RolUsuario.CLIENTE);
            testUser.setActivo(true);
            testUser.setTelefono("1234567890");
            
            Usuario savedUser = usuarioService.save(testUser);
            
            debug.append("✓ Test user created successfully!\n");
            debug.append("   - Email: ").append(testEmail).append("\n");
            debug.append("   - Password: ").append(testPassword).append("\n");
            debug.append("   - User ID: ").append(savedUser.getId()).append("\n");
            debug.append("   - Role: ").append(savedUser.getRol()).append("\n");
            
            debug.append("\n🔗 You can now test login at: /usuario/login\n");
            
        } catch (Exception e) {
            debug.append("Error creating test user: ").append(e.getMessage()).append("\n");
            debug.append("Stack trace: ").append(e.getStackTrace()[0]).append("\n");
        }
        
        return debug.toString();
    }
}