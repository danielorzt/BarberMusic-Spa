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
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombreproducto;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "imagen_path")
    private String imagen;

    @Column(name = "precio")
    private java.math.BigDecimal precio;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "sku")
    private String sku;

    @Column(name = "activo")
    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = true)
    private Categoria categoria;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructor vacío
    public Producto() {
    }

    // Constructor con parámetros
    public Producto(String nombreproducto, String descripcion, String imagen, java.math.BigDecimal precio,
            Integer stock, String sku, Boolean activo, Categoria categoria) {
        this.nombreproducto = nombreproducto;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.precio = precio;
        this.stock = stock;
        this.sku = sku;
        this.activo = activo;
        this.categoria = categoria;
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

    public String getNombreproducto() {
        return nombreproducto;
    }

    public void setNombreproducto(String nombreproducto) {
        this.nombreproducto = nombreproducto;
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

    public java.math.BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(java.math.BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    // Métodos de compatibilidad con código existente
    public String getNombre() {
        return nombreproducto;
    }

    public void setNombre(String nombre) {
        this.nombreproducto = nombre;
    }

    public Usuario getUsuario() {
        return null; // Los productos no tienen usuario asociado en la nueva estructura
    }

    public void setUsuario(Usuario usuario) {
        // No hacer nada, los productos no tienen usuario asociado
    }

    @Override
    public String toString() {
        return "Producto [id=" + id + ", nombreproducto=" + nombreproducto + ", descripcion=" + descripcion
                + ", imagen=" + imagen + ", precio=" + precio + ", stock=" + stock + ", sku=" + sku + ", activo="
                + activo + ", categoriaId=" + (categoria != null ? categoria.getId() : null) + "]";
    }
}
