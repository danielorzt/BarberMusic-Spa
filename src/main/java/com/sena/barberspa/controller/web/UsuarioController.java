package com.sena.barberspa.controller.web;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sena.barberspa.model.Agendamiento;
import com.sena.barberspa.model.Orden;
import com.sena.barberspa.model.Sucursal;
import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.model.enums.RolUsuario;
import com.sena.barberspa.service.EmailService;
import com.sena.barberspa.service.IAgendamientosService;
import com.sena.barberspa.service.IOrdenService;
import com.sena.barberspa.service.ISucursalesService;
import com.sena.barberspa.service.IUsuarioService;
import com.sena.barberspa.service.UploadFileService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

	private final Logger LOGGER = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private IAgendamientosService agendamientosService;

	@Autowired
	private ISucursalesService sucursalesService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UploadFileService upload;

	public static void storeResetToken(String token, String email) {
		resetTokens.put(token, email);
	}
	
	/**
	 * Utility method to get authenticated user ID from session or Spring Security
	 */
	private Long obtenerIdUsuarioAutenticado(HttpSession session) {
		LOGGER.info("🔍 obtenerIdUsuarioAutenticado: Iniciando verificación...");
		
		// 1. Intentar desde sesión HTTP
		Object userIdObj = session.getAttribute("idUsuario");
		LOGGER.info("🔍 HTTP Session idUsuario: {}", userIdObj);
		if (userIdObj != null) {
			Long userId = Long.parseLong(userIdObj.toString());
			LOGGER.info("✅ Usuario ID desde sesión HTTP: {}", userId);
			return userId;
		}
		
		// 2. Sincronizar desde Spring Security
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.info("🔍 Spring Security auth: {}, isAuth: {}", 
		           auth != null ? auth.getName() : "null", 
		           auth != null ? auth.isAuthenticated() : false);
		
		if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
			Optional<Usuario> usuarioOpt = usuarioService.findByEmail(auth.getName());
			if (usuarioOpt.isPresent()) {
				Usuario usuario = usuarioOpt.get();
				
				// Verificar y asignar sucursal por defecto si no tiene una
				verificarYAsignarSucursalPorDefecto(usuario);
				
				// Sincronizar sesión HTTP
				session.setAttribute("idUsuario", usuario.getId());
				session.setAttribute("usuario", usuario);
				LOGGER.info("✅ Sincronizando sesión para usuario: {} (ID: {})", usuario.getNombre(), usuario.getId());
				return usuario.getId();
			} else {
				LOGGER.warn("❌ Usuario con email {} no encontrado en BD", auth.getName());
			}
		}
		
		LOGGER.warn("❌ No se pudo obtener ID de usuario autenticado");
		return null;
	}
	
	/**
	 * Verifica si el usuario tiene sucursal asignada y asigna una por defecto si no tiene
	 */
	private void verificarYAsignarSucursalPorDefecto(Usuario usuario) {
		try {
			// Si el usuario ya tiene sucursal preferida, no hacer nada
			if (usuario.getSucursalPreferida() != null) {
				return;
			}
			
			LOGGER.info("🏢 Usuario {} no tiene sucursal asignada, buscando sucursal por defecto...", usuario.getEmail());
			
			// Buscar primera sucursal activa
			List<Sucursal> sucursalesActivas = sucursalesService.findAll().stream()
				.filter(s -> s.getActivo() != null && s.getActivo())
				.collect(java.util.stream.Collectors.toList());
			
			if (!sucursalesActivas.isEmpty()) {
				Sucursal sucursalPorDefecto = sucursalesActivas.get(0);
				usuario.setSucursalPreferida(sucursalPorDefecto);
				usuarioService.save(usuario); // Guardar cambios
				
				LOGGER.info("✅ Asignada sucursal por defecto: {} para usuario: {}", 
				           sucursalPorDefecto.getNombre(), usuario.getEmail());
			} else {
				LOGGER.warn("⚠️ No hay sucursales activas disponibles para asignar por defecto");
			}
			
		} catch (Exception e) {
			LOGGER.error("❌ Error verificando/asignando sucursal por defecto: {}", e.getMessage());
			// No lanzar excepción - esto no debe interrumpir el flujo de autenticación
		}
	}

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// Almacén temporal de tokens de restablecimiento (en producción, usar base de
	// datos)
	private static final ConcurrentHashMap<String, String> resetTokens = new ConcurrentHashMap<>();
	
	@ModelAttribute
	public void addCommonAttributes(Model model, HttpSession session) {
		try {
			// 1. Intentar obtener usuario de la sesión HTTP primero
			Object userIdObj = session.getAttribute("idUsuario");
			if (userIdObj != null) {
				Long userId = Long.parseLong(userIdObj.toString());
				Optional<Usuario> usuarioOpt = usuarioService.findById(userId);
				if (usuarioOpt.isPresent()) {
					Usuario usuario = usuarioOpt.get();
					model.addAttribute("usuario", usuario);
					model.addAttribute("sesion", userId);
					LOGGER.debug("✅ UsuarioController: Usuario cargado desde sesión HTTP: {} (ID: {})", usuario.getNombre(), userId);
					return;
				}
			}
			
			// 2. Fallback: Sincronizar desde Spring Security si está autenticado
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
				Optional<Usuario> usuarioOpt = usuarioService.findByEmail(auth.getName());
				if (usuarioOpt.isPresent()) {
					Usuario usuario = usuarioOpt.get();
					// Sincronizar sesión HTTP
					session.setAttribute("idUsuario", usuario.getId());
					session.setAttribute("usuario", usuario);
					
					model.addAttribute("usuario", usuario);
					model.addAttribute("sesion", usuario.getId());
					LOGGER.debug("✅ UsuarioController: Usuario sincronizado desde Spring Security: {} (ID: {})", usuario.getNombre(), usuario.getId());
					return;
				}
			}
			
			// 3. No hay usuario autenticado - esto es normal para páginas públicas
			LOGGER.debug("ℹ️ UsuarioController: No hay usuario autenticado en la sesión");
			
		} catch (Exception e) {
			LOGGER.warn("UsuarioController: Error loading user session data: {}", e.getMessage());
		}
	}


	@GetMapping("/acceder")
	public String acceder(HttpSession session) {
		try {
			LOGGER.info("Accediendo con sesión: {}", session.getAttribute("idUsuario"));

			if (session.getAttribute("idUsuario") == null) {
				LOGGER.warn("No hay usuario en sesión");
				return "redirect:/usuario/login";
			}

			Optional<Usuario> user = usuarioService
					.findById(Long.parseLong(session.getAttribute("idUsuario").toString()));

			if (user.isPresent()) {
				Usuario usuario = user.get();
				LOGGER.info("Usuario encontrado: {} con rol: {}", usuario.getNombre(), usuario.getRol());

				// Redirigir según el rol
				switch (usuario.getRol()) {
					case GERENTE:
						LOGGER.info("Redirigiendo gerente a dashboard");
						return "redirect:/administrador";
					case ADMIN_SUCURSAL:
						LOGGER.info("Redirigiendo admin sucursal a panel");
						return "redirect:/admin-sucursal";
					case EMPLEADO:
						LOGGER.info("Redirigiendo empleado a panel");
						return "redirect:/empleado";
					case CLIENTE:
					default:
						LOGGER.info("Redirigiendo cliente a home");
						return "redirect:/";
				}
			} else {
				LOGGER.warn("Usuario no encontrado en la base de datos");
				session.invalidate();
				return "redirect:/usuario/login";
			}
		} catch (Exception e) {
			LOGGER.error("Error en método acceder: {}", e.getMessage(), e);
			session.invalidate();
			return "redirect:/usuario/login";
		}
	}

	@GetMapping("/cerrar")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	/**
	 * Método para procesar la solicitud de restablecimiento de contraseña
	 */
	@PostMapping("/resetPassword")
	@ResponseBody
	public ResponseEntity<String> resetPassword(@RequestParam("resetEmail") String email) {
		try {
			// Verificar si el usuario existe
			Optional<Usuario> usuario = usuarioService.findByEmail(email);
			if (usuario.isPresent()) {
				// Generar token único
				String token = UUID.randomUUID().toString();

				// Guardar token en el mapa (en producción, usar base de datos)
				resetTokens.put(token, email);

				// Enviar correo de restablecimiento
				emailService.sendPasswordResetEmail(email);

				return ResponseEntity.ok("Correo enviado correctamente");
			} else {
				// Por seguridad, no informamos si el correo existe o no
				return ResponseEntity.ok("Correo enviado correctamente");
			}
		} catch (Exception e) {
			LOGGER.error("Error al procesar solicitud de restablecimiento: {}", e.getMessage());
			return ResponseEntity.badRequest().body("Error al procesar la solicitud");
		}
	}

	/**
	 * Método para mostrar el formulario de cambio de contraseña
	 */
	@GetMapping("/cambiarPassword")
	public String showChangePasswordForm(@RequestParam("token") String token, Model model) {
		// Verificar si el token es válido
		if (resetTokens.containsKey(token)) {
			model.addAttribute("token", token);
			return "publico/cambiar-password";
		} else {
			return "redirect:/usuario/token-invalido";
		}
	}

	/**
	 * Método para procesar el cambio de contraseña
	 */
	@PostMapping("/saveNewPassword")
	public String saveNewPassword(@RequestParam("token") String token,
			@RequestParam("password") String password,
			RedirectAttributes redirectAttributes) {
		// Verificar si el token es válido
		String email = resetTokens.get(token);
		if (email != null) {
			try {
				// Buscar usuario
				Optional<Usuario> optUser = usuarioService.findByEmail(email);
				if (optUser.isPresent()) {
					Usuario usuario = optUser.get();

					// Actualizar contraseña
					usuario.setPassword(passwordEncoder.encode(password));
					usuarioService.update(usuario);

					// Eliminar token usado
					resetTokens.remove(token);

					redirectAttributes.addFlashAttribute("success",
							"¡Tu contraseña ha sido actualizada correctamente! Ahora puedes iniciar sesión con tu nueva contraseña.");
					return "redirect:/usuario/login";
				}
			} catch (Exception e) {
				LOGGER.error("Error al cambiar contraseña: {}", e.getMessage());
			}
		}

		redirectAttributes.addFlashAttribute("error",
				"No se pudo cambiar la contraseña. El enlace ha expirado o es inválido.");
		return "redirect:/usuario/login";
	}

	@GetMapping("/token-invalido")
	public String tokenInvalido() {
		return "publico/token-invalido";
	}

	@GetMapping("/compras")
	public String showUserOrders(HttpSession session, Model model) {
		Long idUsuario = obtenerIdUsuarioAutenticado(session);
		if (idUsuario == null) {
			return "redirect:/usuario/login";
		}

		Usuario usuario = usuarioService.findById(idUsuario)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		List<Orden> ordenes = ordenService.findByUsuario(usuario);
		model.addAttribute("ordenes", ordenes);
		model.addAttribute("sesion", session.getAttribute("idUsuario"));

		return "cliente/compras";
	}

	@GetMapping("/compras/{id}")
	public String showOrderDetails(@PathVariable Long id, HttpSession session, Model model) {
		Long idUsuario = obtenerIdUsuarioAutenticado(session);
		if (idUsuario == null) {
			return "redirect:/usuario/login";
		}

		Orden orden = ordenService.findById(id).orElseThrow(() -> new RuntimeException("Orden no encontrada"));

		// Verificar que la orden pertenece al usuario
		if (!orden.getClienteUsuario().getId().equals(idUsuario)) {
			return "redirect:/usuario/compras";
		}

		model.addAttribute("detalles", orden.getDetalles());
		model.addAttribute("orden", orden);
		model.addAttribute("sesion", session.getAttribute("idUsuario"));

		return "cliente/detallecompra";
	}

	@GetMapping("/perfil")
	public String showProfile(HttpSession session, Model model) {
		// Intentar obtener usuario de la sesión o sincronizar desde Spring Security
		Long idUsuario = obtenerIdUsuarioAutenticado(session);
		if (idUsuario == null) {
			return "redirect:/usuario/login";
		}

		Usuario usuario = usuarioService.findById(idUsuario)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		// Obtener agendamientos del usuario
		List<Agendamiento> agendamientos = agendamientosService.findByUsuario(usuario);

		// Obtener órdenes del usuario
		List<Orden> ordenes = ordenService.findByUsuario(usuario);

		// Agregar los datos al modelo
		model.addAttribute("usuario", usuario);
		model.addAttribute("agendamientos", agendamientos);
		model.addAttribute("ordenes", ordenes);
		model.addAttribute("ordenesCount", ordenes.size());
		model.addAttribute("citasCount", agendamientos.size());
		model.addAttribute("sesion", session.getAttribute("idUsuario"));

		return "cliente/perfil";
	}

	@GetMapping("/editar")
	public String showEditForm(HttpSession session, Model model) {
		if (session.getAttribute("idUsuario") == null) {
			return "redirect:/usuario/login";
		}

		Usuario usuario = usuarioService.findById(Long.parseLong(session.getAttribute("idUsuario").toString()))
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		model.addAttribute("usuario", usuario);
		return "usuario/editar";
	}

	@PostMapping("/actualizar")
	public String updateProfile(Usuario usuario,
			@RequestParam(value = "img", required = false) MultipartFile file,
			HttpSession session,
			RedirectAttributes redirectAttributes) throws IOException {
		Long idUsuario = obtenerIdUsuarioAutenticado(session);
		if (idUsuario == null) {
			return "redirect:/usuario/login";
		}

		try {
			Usuario existingUser = usuarioService
					.findById(idUsuario)
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

			// Actualizar solo los campos permitidos
			existingUser.setNombre(usuario.getNombre());
			existingUser.setEmail(usuario.getEmail());
			existingUser.setTelefono(usuario.getTelefono());

			if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
				existingUser.setPassword(passwordEncoder.encode(usuario.getPassword()));
			}

			// Manejar la imagen de perfil si se subió una nueva
			if (file != null && !file.isEmpty()) {
				String nombreImagen = upload.saveImages(file, existingUser.getNombre());
				existingUser.setImagenPath(nombreImagen);
			}

			usuarioService.save(existingUser);
			redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
			return "redirect:/usuario/perfil";

		} catch (Exception e) {
			LOGGER.error("Error al actualizar perfil: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil");
			return "redirect:/usuario/editar";
		}
	}

	@GetMapping("/create-admin")
	@ResponseBody
	public String createTestAdmin() {
		try {
			// Verificar si el admin ya existe
			Optional<Usuario> existingAdmin = usuarioService.findByEmail("admin@barberspa.com");
			if (existingAdmin.isPresent()) {
				return "✅ Usuario admin ya existe: admin@barberspa.com<br>" +
						"Rol actual: " + existingAdmin.get().getRol() + "<br>" +
						"<a href='/usuario/login'>🔗 Ir al login</a>";
			}

			// Crear nuevo usuario admin
			Usuario admin = new Usuario();
			admin.setNombre("Admin Sistema");
			admin.setEmail("admin@barberspa.com");
			admin.setRol(RolUsuario.GERENTE);
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setActivo(true);
			admin.setTelefono("1234567890");

			usuarioService.save(admin);

			return "✅ Usuario admin creado exitosamente!<br>" +
					"Email: admin@barberspa.com<br>" +
					"Password: admin123<br>" +
					"Rol: GERENTE<br>" +
					"<a href='/usuario/login'>🔗 Ir al login</a>";

		} catch (Exception e) {
			return "❌ Error creando admin: " + e.getMessage();
		}
	}
}