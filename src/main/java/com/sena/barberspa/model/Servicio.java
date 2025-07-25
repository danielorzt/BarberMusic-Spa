package com.sena.barberspa.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "servicios")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "imagen_path")
    private String imagen;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "precio_base")
    private Double precio;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Categoria categoria;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "especialidad_requerida_id", nullable = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Especialidad especialidadRequerida;

    // Constructor vacío
    public Servicio() {
    }

    // Constructor con parámetros
    public Servicio(String descripcion, String imagen, String nombre, Double precio,
            Integer duracionMinutos, Boolean activo, Categoria categoria,
            Especialidad especialidadRequerida) {
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.nombre = nombre;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;
        this.activo = activo;
        this.categoria = categoria;
        this.especialidadRequerida = especialidadRequerida;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Especialidad getEspecialidadRequerida() {
        return especialidadRequerida;
    }

    public void setEspecialidadRequerida(Especialidad especialidadRequerida) {
        this.especialidadRequerida = especialidadRequerida;
    }

    // Métodos de compatibilidad con código existente
    public Double getPrecioBase() {
        return precio;
    }

    public void setPrecioBase(Double precioBase) {
        this.precio = precioBase;
    }

    public Usuario getUsuario() {
        return null; // Los servicios no tienen usuario asociado en la nueva estructura
    }

    public void setUsuario(Usuario usuario) {
        // No hacer nada, los servicios no tienen usuario asociado
    }

    @Override
    public String toString() {
        return "Servicio{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", imagen='" + imagen + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", duracionMinutos=" + duracionMinutos +
                ", activo=" + activo +
                ", categoriaId=" + (categoria != null ? categoria.getId() : null) +
                ", especialidadRequeridaId=" + (especialidadRequerida != null ? especialidadRequerida.getId() : null) +
                '}';
    }
}
