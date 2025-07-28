package com.sena.barberspa.config.security;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.service.IUsuarioService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionPersistenceFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionPersistenceFilter.class);
    private static final String SESSION_USER_ID = "idUsuario";

    @Autowired
    private IUsuarioService usuarioService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Solo procesar si el servicio está disponible
            if (usuarioService == null) {
                filterChain.doFilter(request, response);
                return;
            }

            HttpSession session = request.getSession(false);
            
            if (session != null) {
                // Verificar si hay usuario en Spring Security pero no en la sesión HTTP
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                    
                    Object sessionUserId = session.getAttribute(SESSION_USER_ID);
                    if (sessionUserId == null) {
                        try {
                            // Sincronizar Spring Security con la sesión HTTP
                            Optional<Usuario> usuarioOpt = usuarioService.findByEmail(auth.getName());
                            if (usuarioOpt.isPresent()) {
                                Usuario usuario = usuarioOpt.get();
                                session.setAttribute(SESSION_USER_ID, usuario.getId());
                                session.setAttribute("usuario", usuario);
                                LOGGER.debug("🔄 SessionPersistenceFilter: Sincronizado usuario desde Spring Security: {}", 
                                            usuario.getNombre());
                            }
                        } catch (Exception e) {
                            LOGGER.warn("Error sincronizando usuario desde Spring Security: {}", e.getMessage());
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            LOGGER.error("Error en SessionPersistenceFilter: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // No filtrar recursos estáticos
        return path.startsWith("/assets/") || 
               path.startsWith("/css/") || 
               path.startsWith("/js/") || 
               path.startsWith("/img/") || 
               path.startsWith("/vendor/") ||
               path.startsWith("/static/") ||
               path.startsWith("/error");
    }
} 