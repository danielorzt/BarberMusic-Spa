package com.sena.barberspa.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IUsuarioService;
import com.sena.barberspa.service.UserDetailServiceImplement;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthTestController {

    private static final Logger logger = LoggerFactory.getLogger(AuthTestController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailServiceImplement userDetailService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/debug/test-login-form")
    @ResponseBody
    public String testLoginForm() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Test Login Form</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 40px; }
                        .form-group { margin: 10px 0; }
                        input[type="text"], input[type="password"] { width: 200px; padding: 5px; }
                        button { padding: 10px 20px; background: #007bff; color: white; border: none; cursor: pointer; }
                        .result { margin-top: 20px; padding: 10px; background: #f8f9fa; border: 1px solid #dee2e6; }
                    </style>
                </head>
                <body>
                    <h2>🧪 Authentication Test Form</h2>
                    <p>Use this form to test the authentication manually:</p>
                    
                    <form action="/debug/test-authenticate" method="post">
                        <div class="form-group">
                            <label>Email:</label><br>
                            <input type="text" name="email" value="test@barberspa.com" required>
                        </div>
                        <div class="form-group">
                            <label>Password:</label><br>
                            <input type="password" name="password" value="123456" required>
                        </div>
                        <div class="form-group">
                            <button type="submit">🔐 Test Authentication</button>
                        </div>
                    </form>
                    
                    <hr>
                    <h3>🔧 Quick Actions:</h3>
                    <a href="/debug/create-test-user" style="color: #007bff;">📝 Create Test User</a> | 
                    <a href="/debug/authentication" style="color: #007bff;">🔍 Debug Authentication</a> | 
                    <a href="/debug/security" style="color: #007bff;">🛡️ Debug Security</a>
                </body>
                </html>
                """;
    }

    @PostMapping("/debug/test-authenticate")
    @ResponseBody
    public String testAuthenticate(@RequestParam String email, @RequestParam String password, HttpSession session) {
        StringBuilder result = new StringBuilder();
        result.append("=== AUTHENTICATION TEST RESULTS ===\n\n");

        try {
            logger.info("🧪 Testing authentication for email: {}", email);
            result.append("1. Input Verification:\n");
            result.append("   - Email: ").append(email).append("\n");
            result.append("   - Password: ").append(password.length()).append(" characters\n\n");

            // Step 1: Check if user exists in database
            result.append("2. Database Check:\n");
            var userOpt = usuarioService.findByEmail(email);
            if (userOpt.isEmpty()) {
                result.append("   ❌ User NOT found in database\n");
                result.append("   💡 Create user first: /debug/create-test-user\n");
                return result.toString();
            }

            Usuario user = userOpt.get();
            result.append("   ✅ User found in database\n");
            result.append("   - User ID: ").append(user.getId()).append("\n");
            result.append("   - User name: ").append(user.getNombre()).append("\n");
            result.append("   - User role: ").append(user.getRol()).append("\n");
            result.append("   - User active: ").append(user.getActivo()).append("\n\n");

            // Step 2: Test password verification
            result.append("3. Password Verification:\n");
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            result.append("   - Password matches: ").append(passwordMatches).append("\n");
            if (!passwordMatches) {
                result.append("   ❌ Password verification failed\n");
                result.append("   💡 Expected password for test user is: 123456\n");
                return result.toString();
            }
            result.append("   ✅ Password verification successful\n\n");

            // Step 3: Test UserDetailsService loading
            result.append("4. UserDetailsService Test:\n");
            try {
                UserDetails userDetails = userDetailService.loadUserByUsername(email);
                result.append("   ✅ UserDetails loaded successfully\n");
                result.append("   - Username: ").append(userDetails.getUsername()).append("\n");
                result.append("   - Authorities: ").append(userDetails.getAuthorities()).append("\n");
                result.append("   - Enabled: ").append(userDetails.isEnabled()).append("\n\n");
            } catch (Exception e) {
                result.append("   ❌ UserDetailsService failed: ").append(e.getMessage()).append("\n");
                return result.toString();
            }

            // Step 4: Test Spring Security Authentication
            result.append("5. Spring Security Authentication Test:\n");
            try {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(email, password);
                Authentication authentication = authenticationManager.authenticate(authToken);
                
                result.append("   ✅ Authentication successful!\n");
                result.append("   - Principal: ").append(authentication.getPrincipal()).append("\n");
                result.append("   - Authorities: ").append(authentication.getAuthorities()).append("\n");
                result.append("   - Authenticated: ").append(authentication.isAuthenticated()).append("\n\n");

                // Step 5: Set up session manually (simulate what form login would do)
                result.append("6. Session Setup:\n");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                session.setAttribute("idUsuario", user.getId());
                session.setAttribute("usuario", user);
                
                result.append("   ✅ Session configured successfully\n");
                result.append("   - User ID in session: ").append(session.getAttribute("idUsuario")).append("\n");
                result.append("   - Spring Security context set: ").append(SecurityContextHolder.getContext().getAuthentication() != null).append("\n\n");

                result.append("🎉 AUTHENTICATION TEST PASSED!\n");
                result.append("💡 You can now navigate to protected routes like /usuario/perfil\n");

            } catch (AuthenticationException e) {
                result.append("   ❌ Spring Security authentication failed: ").append(e.getMessage()).append("\n");
                result.append("   🔍 This indicates an issue with the AuthenticationProvider configuration\n");
                return result.toString();
            }

        } catch (Exception e) {
            result.append("Unexpected error: ").append(e.getMessage()).append("\n");
            result.append("Stack trace: ").append(e.getStackTrace()[0]).append("\n");
        }

        return result.toString();
    }

    @GetMapping("/debug/test-protected-route")
    @ResponseBody
    public String testProtectedRoute(HttpSession session) {
        StringBuilder result = new StringBuilder();
        result.append("=== PROTECTED ROUTE TEST ===\n\n");

        try {
            // Test 1: Check session
            Object userIdObj = session.getAttribute("idUsuario");
            result.append("1. Session Check:\n");
            result.append("   - User ID in session: ").append(userIdObj).append("\n");

            // Test 2: Check Spring Security context
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            result.append("2. Spring Security Context:\n");
            result.append("   - Authentication: ").append(auth != null ? auth.getName() : "null").append("\n");
            result.append("   - Authorities: ").append(auth != null ? auth.getAuthorities() : "null").append("\n");
            result.append("   - Authenticated: ").append(auth != null ? auth.isAuthenticated() : "false").append("\n");

            // Test 3: Load user from database
            if (userIdObj != null) {
                result.append("3. Database User Load:\n");
                Long userId = Long.parseLong(userIdObj.toString());
                var userOpt = usuarioService.findById(userId);
                if (userOpt.isPresent()) {
                    Usuario user = userOpt.get();
                    result.append("   ✅ User loaded from database\n");
                    result.append("   - Name: ").append(user.getNombre()).append("\n");
                    result.append("   - Role: ").append(user.getRol()).append("\n");
                    result.append("   - Active: ").append(user.getActivo()).append("\n");
                } else {
                    result.append("   ❌ User not found in database\n");
                }
            }

            if (auth != null && auth.isAuthenticated() && userIdObj != null) {
                result.append("\n🎉 PROTECTED ROUTE ACCESS: GRANTED\n");
                result.append("💡 User should be able to access protected pages\n");
            } else {
                result.append("\n❌ PROTECTED ROUTE ACCESS: DENIED\n");
                result.append("💡 User authentication is incomplete\n");
            }

        } catch (Exception e) {
            result.append("Error: ").append(e.getMessage()).append("\n");
        }

        return result.toString();
    }
}