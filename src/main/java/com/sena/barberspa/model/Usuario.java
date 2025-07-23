package com.sena.barberspa.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	private String email;

	@Column(name = "email_verified_at")
	private LocalDateTime emailVerifiedAt;

	private String password;

	@Column(name = "imagen_path")
	private String imagenPath;

	private String telefono;

	private String rol; // CLIENTE, EMPLEADO, ADMIN_SUCURSAL, GERENTE

	@Column(name = "activo")
	private Boolean activo;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// Relación con Sucursal preferida
	@ManyToOne
	@JoinColumn(name = "sucursal_preferida_id")
	private Sucursal sucursalPreferida;

	// Relación con Música Preferencia
	@ManyToOne
	@JoinColumn(name = "musica_preferencia_navegacion_id")
	private MusicaPreferenciasNavegacion musicaPreferencia;

	// Relación con Ordenes
	@OneToMany(mappedBy = "clienteUsuario")
	private List<Orden> ordenes;

	// Relación con Personal (si es empleado)
	@OneToMany(mappedBy = "usuario")
	private List<Personal> personalRegistros;

	// Constructor vacío
	public Usuario() {
	}

	// Constructor con parámetros
	public Usuario(Long id, String nombre, String email, String telefono,
			String rol, String password, Boolean activo, String imagenPath,
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

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
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
}
