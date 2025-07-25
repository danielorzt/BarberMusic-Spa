package com.sena.barberspa.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad que representa los favoritos de un usuario
 * Permite a los clientes marcar productos y servicios como favoritos
 * según el Manual de Roles BarberMusic&Spa
 */
@Entity
@Table(name = "favoritos")
public class Favorito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = true)
    private Producto producto;
    
    @ManyToOne
    @JoinColumn(name = "servicio_id", nullable = true)
    private Servicio servicio;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructores
    public Favorito() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Favorito(Usuario usuario, Producto producto) {
        this();
        this.usuario = usuario;
        this.producto = producto;
    }
    
    public Favorito(Usuario usuario, Servicio servicio) {
        this();
        this.usuario = usuario;
        this.servicio = servicio;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Servicio getServicio() {
        return servicio;
    }
    
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Métodos utilitarios
    public boolean esProducto() {
        return producto != null;
    }
    
    public boolean esServicio() {
        return servicio != null;
    }
    
    public String getNombreFavorito() {
        if (esProducto()) {
            return producto.getNombre();
        } else if (esServicio()) {
            return servicio.getNombre();
        }
        return "Favorito desconocido";
    }
    
    public String getTipoFavorito() {
        if (esProducto()) {
            return "PRODUCTO";
        } else if (esServicio()) {
            return "SERVICIO";
        }
        return "DESCONOCIDO";
    }
    
    @Override
    public String toString() {
        return "Favorito{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getNombre() : "null") +
                ", tipo=" + getTipoFavorito() +
                ", nombre=" + getNombreFavorito() +
                ", createdAt=" + createdAt +
                '}';
    }
}