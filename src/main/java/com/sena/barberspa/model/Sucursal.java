package com.sena.barberspa.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "sucursales")
public class Sucursal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	@Column(name = "imagen_path")
	private String imagenPath;

	@Column(name = "telefono_contacto")
	private String telefonoContacto;

	@Column(name = "email_contacto")
	private String emailContacto;

	@Column(name = "link_maps")
	private String linkMaps;

	private Double latitud;

	private Double longitud;

	private Boolean activo;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// Relaciones - @JsonIgnore para evitar recursión infinita al cargar sucursales
	@OneToMany(mappedBy = "sucursal")
	@JsonIgnore
	private List<HorarioSucursal> horarios;

	@OneToMany(mappedBy = "sucursal")
	@JsonIgnore
	private List<Direccion> direcciones;

	// Constructor vacío
	public Sucursal() {
	}

	// Constructor con parámetros
	public Sucursal(String nombre, String imagenPath, String telefonoContacto, String emailContacto,
			String linkMaps, Double latitud, Double longitud, Boolean activo) {
		this.nombre = nombre;
		this.imagenPath = imagenPath;
		this.telefonoContacto = telefonoContacto;
		this.emailContacto = emailContacto;
		this.linkMaps = linkMaps;
		this.latitud = latitud;
		this.longitud = longitud;
		this.activo = activo;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	// Getters y Setters

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

	public String getTelefonoContacto() {
		return telefonoContacto;
	}

	public void setTelefonoContacto(String telefonoContacto) {
		this.telefonoContacto = telefonoContacto;
	}

	public String getEmailContacto() {
		return emailContacto;
	}

	public void setEmailContacto(String emailContacto) {
		this.emailContacto = emailContacto;
	}

	public String getLinkMaps() {
		return linkMaps;
	}

	public void setLinkMaps(String linkMaps) {
		this.linkMaps = linkMaps;
	}

	public Double getLatitud() {
		return latitud;
	}

	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}

	public Double getLongitud() {
		return longitud;
	}

	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}

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

	public List<HorarioSucursal> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<HorarioSucursal> horarios) {
		this.horarios = horarios;
	}

	public List<Direccion> getDirecciones() {
		return direcciones;
	}

	public void setDirecciones(List<Direccion> direcciones) {
		this.direcciones = direcciones;
	}

	// Métodos de compatibilidad para código antiguo
	public String getImagen() {
		return this.imagenPath;
	}

	public void setImagen(String imagen) {
		this.imagenPath = imagen;
	}

	public String getEstado() {
		return this.activo != null && this.activo ? "ACTIVO" : "INACTIVO";
	}

	public void setEstado(String estado) {
		this.activo = "ACTIVO".equalsIgnoreCase(estado);
	}

	// Método toString para depuración
	@Override
	public String toString() {
		return "Sucursal [id=" + id + ", nombre=" + nombre + ", imagenPath=" + imagenPath + ", telefonoContacto="
				+ telefonoContacto
				+ ", emailContacto=" + emailContacto + ", linkMaps=" + linkMaps + ", activo=" + activo + "]";
	}
}
