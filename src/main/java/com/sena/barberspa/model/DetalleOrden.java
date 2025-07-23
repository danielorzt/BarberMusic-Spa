package com.sena.barberspa.model;

import java.math.BigDecimal;
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
@Table(name = "detalle_ordenes")
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_producto_historico")
    private String nombreProductoHistorico;

    private Integer cantidad;

    @Column(name = "precio_unitario_historico")
    private BigDecimal precioUnitarioHistorico;

    @Column(name = "subtotal_linea")
    private BigDecimal subtotalLinea;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Constructor vacío
    public DetalleOrden() {
    }

    // Constructor con parámetros
    public DetalleOrden(String nombreProductoHistorico, Integer cantidad, BigDecimal precioUnitarioHistorico,
            BigDecimal subtotalLinea, Orden orden, Producto producto) {
        this.nombreProductoHistorico = nombreProductoHistorico;
        this.cantidad = cantidad;
        this.precioUnitarioHistorico = precioUnitarioHistorico;
        this.subtotalLinea = subtotalLinea;
        this.orden = orden;
        this.producto = producto;
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

    public String getNombreProductoHistorico() {
        return nombreProductoHistorico;
    }

    public void setNombreProductoHistorico(String nombreProductoHistorico) {
        this.nombreProductoHistorico = nombreProductoHistorico;
    }

    public Integer getCantidadInt() {
        return cantidad;
    }

    public void setCantidadInt(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitarioHistorico() {
        return precioUnitarioHistorico;
    }

    public void setPrecioUnitarioHistorico(BigDecimal precioUnitarioHistorico) {
        this.precioUnitarioHistorico = precioUnitarioHistorico;
    }

    public BigDecimal getSubtotalLinea() {
        return subtotalLinea;
    }

    public void setSubtotalLinea(BigDecimal subtotalLinea) {
        this.subtotalLinea = subtotalLinea;
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

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    // Métodos de compatibilidad con código existente
    public String getNombre() {
        return nombreProductoHistorico;
    }

    public void setNombre(String nombre) {
        this.nombreProductoHistorico = nombre;
    }

    public Double getCantidad() {
        return cantidad != null ? cantidad.doubleValue() : null;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad != null ? cantidad.intValue() : null;
    }

    public Double getPrecio() {
        return precioUnitarioHistorico != null ? precioUnitarioHistorico.doubleValue() : null;
    }

    public void setPrecio(Double precio) {
        this.precioUnitarioHistorico = precio != null ? BigDecimal.valueOf(precio) : null;
    }

    public Double getTotal() {
        return subtotalLinea != null ? subtotalLinea.doubleValue() : null;
    }

    public void setTotal(Double total) {
        this.subtotalLinea = total != null ? BigDecimal.valueOf(total) : null;
    }

    @Override
    public String toString() {
        return "DetalleOrden{" +
                "id=" + id +
                ", nombreProductoHistorico='" + nombreProductoHistorico + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitarioHistorico=" + precioUnitarioHistorico +
                ", subtotalLinea=" + subtotalLinea +
                '}';
    }
}
