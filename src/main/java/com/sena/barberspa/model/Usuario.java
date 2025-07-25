package com.sena.barberspa.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sena.barberspa.model.enums.RolUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
	@Column(name = "nombre", nullable = false)
	private String nombre;

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "El email debe tener un formato válido")
	@Size(max = 255, message = "El email no puede exceder 255 caracteres")
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "email_verified_at")
	private LocalDateTime emailVerifiedAt;

	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "imagen_path")
	private String imagenPath;

	@Size(max = 25, message = "El teléfono no puede exceder 25 caracteres")
	@Column(name = "telefono")
	private String telefono;

	@Convert(converter = com.sena.barberspa.model.converter.RolUsuarioConverter.class)
	@Column(name = "rol", nullable = false, length = 50)
	private RolUsuario rol = RolUsuario.CLIENTE; // Valor por defecto

	@Column(name = "activo", nullable = false)
	private Boolean activo = true; // Valor por defecto

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	// Relación con Sucursal preferida
	@ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = "sucursal_preferida_id")
	@JsonIgnore
	private Sucursal sucursalPreferida;

	// Relación con Música Preferencia
	@ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = "musica_preferencia_navegacion_id")
	@JsonIgnore
	private MusicaPreferenciasNavegacion musicaPreferencia;

	// Relación con Ordenes - @JsonIgnore para evitar recursión infinita
	@OneToMany(mappedBy = "clienteUsuario")
	@JsonIgnore
	private List<Orden> ordenes;

	// Relación con Personal (si es empleado) - @JsonIgnore para evitar recursión infinita
	@OneToMany(mappedBy = "usuario")
	@JsonIgnore
	private List<Personal> personalRegistros;

	// Constructor vacío
	public Usuario() {
	}

	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		if (createdAt == null) {
			createdAt = now;
		}
		if (updatedAt == null) {
			updatedAt = now;
		}
		if (rol == null) {
			rol = RolUsuario.CLIENTE;
		}
		if (activo == null) {
			activo = true;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	// Constructor con parámetros
	public Usuario(Long id, String nombre, String email, String telefono,
			RolUsuario rol, String password, Boolean activo, String imagenPath,
			LocalDateTime emailVerifiedAt) {
		this.id = id;
		this.nombre = nombre;
		this.email = email;
		this.telefono = telefono;
		this.rol = rol;
		this.password = password;
		this.activo = activo;
		this.imagenPath = imagenPath;
		this.emailVerifiedAt = emailVerifiedAt;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	// Getters y setters
	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getImagenPath() {
		return imagenPath;
	}

	public void setImagenPath(String imagenPath) {
		this.imagenPath = imagenPath;
	}

	public LocalDateTime getEmailVerifiedAt() {
		return emailVerifiedAt;
	}

	public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
		this.emailVerifiedAt = emailVerifiedAt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public RolUsuario getRol() {
		return rol;
	}

	public void setRol(RolUsuario rol) {
		this.rol = rol;
	}

	// Método de compatibilidad para código existente
	public String getRolString() {
		return rol != null ? rol.getCodigo() : RolUsuario.CLIENTE.getCodigo();
	}

	public void setRolString(String rolString) {
		this.rol = RolUsuario.fromCodigo(rolString);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Personal> getPersonalRegistros() {
		return personalRegistros;
	}

	public void setPersonalRegistros(List<Personal> personalRegistros) {
		this.personalRegistros = personalRegistros;
	}

	public List<Orden> getOrdenes() {
		return ordenes;
	}

	public void setOrdenes(List<Orden> ordenes) {
		this.ordenes = ordenes;
	}

	// Getters y setters para Sucursal Preferida
	public Sucursal getSucursalPreferida() {
		return sucursalPreferida;
	}

	public void setSucursalPreferida(Sucursal sucursalPreferida) {
		this.sucursalPreferida = sucursalPreferida;
	}

	// Getters y setters para Música Preferencia
	public MusicaPreferenciasNavegacion getMusicaPreferencia() {
		return musicaPreferencia;
	}

	public void setMusicaPreferencia(MusicaPreferenciasNavegacion musicaPreferencia) {
		this.musicaPreferencia = musicaPreferencia;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	// Métodos de utilidad para borrado lógico
	public boolean isDeleted() {
		return deletedAt != null;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
		this.activo = false;
	}

	public void restore() {
		this.deletedAt = null;
		this.activo = true;
	}

	// Métodos de utilidad para roles
	public boolean isCliente() {
		return rol == RolUsuario.CLIENTE;
	}

	public boolean isEmpleado() {
		return rol == RolUsuario.EMPLEADO;
	}

	public boolean isAdminSucursal() {
		return rol == RolUsuario.ADMIN_SUCURSAL;
	}

	public boolean isGerente() {
		return rol == RolUsuario.GERENTE;
	}

	public boolean puedeGestionar(Usuario otroUsuario) {
		return this.rol.tienePermisoSobre(otroUsuario.getRol());
	}

	// Métodos de compatibilidad para código antiguo
	public String getEstado() {
		return this.activo != null && this.activo ? "activo" : "inactivo";
	}

	public void setEstado(String estado) {
		this.activo = "activo".equalsIgnoreCase(estado);
	}

	public String getImagen() {
		return this.imagenPath;
	}

	public void setImagen(String imagen) {
		this.imagenPath = imagen;
	}

	public String getDireccion() {
		// Por ahora retornamos un valor predeterminado
		// TODO: Implementar relación con direcciones si es necesario
		return "Dirección no especificada";
	}

	public void setDireccion(String direccion) {
		// Por ahora no hace nada
		// TODO: Implementar relación con direcciones si es necesario
	}

	// Alias para sucursal (compatibility)
	public Sucursal getSucursal() {
		return this.sucursalPreferida;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursalPreferida = sucursal;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nombre=" + nombre + ", email=" + email + ", rol=" + rol + ", activo=" + activo
				+ "]";
	}

	// Métodos de UserDetails
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String roleName = rol != null ? rol.getCodigo() : RolUsuario.CLIENTE.getCodigo();
		return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.activo;
	}
}
