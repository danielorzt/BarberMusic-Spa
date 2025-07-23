package com.sena.barberspa.model;

import jakarta.persistence.*;
import java.math.BigDecimal; // <-- IMPORT AGREGADO
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_orden")
    private String numeroOrden;

    @Column(name = "fecha_orden")
    private LocalDateTime fechaOrden;

    @Column(nullable = false)
    private BigDecimal subtotal; // Tipo de dato corregido a BigDecimal

    @Column(name = "descuento_total")
    private BigDecimal descuentoTotal; // Tipo de dato corregido a BigDecimal

    @Column(name = "impuestos_total")
    private BigDecimal impuestosTotal; // Tipo de dato corregido a BigDecimal

    @Column(name = "total_orden")
    private BigDecimal totalOrden; // Tipo de dato corregido a BigDecimal

    @Column(name = "estado_orden")
    private String estadoOrden;

    @Column(name = "notas_orden")
    private String notasOrden;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "cliente_usuario_id", nullable = false)
    private Usuario clienteUsuario;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> detalles;

    // Constructor vacío
    public Orden() {
    }

    // Método para actualizar las fechas antes de persistir o actualizar
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaOrden == null) {
            fechaOrden = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public LocalDateTime getFechaOrden() {
        return fechaOrden;
    }

    public void setFechaOrden(LocalDateTime fechaOrden) {
        this.fechaOrden = fechaOrden;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuentoTotal() {
        return descuentoTotal;
    }

    public void setDescuentoTotal(BigDecimal descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
    }

    public BigDecimal getImpuestosTotal() {
        return impuestosTotal;
    }

    public void setImpuestosTotal(BigDecimal impuestosTotal) {
        this.impuestosTotal = impuestosTotal;
    }

    public BigDecimal getTotalOrden() {
        return totalOrden;
    }

    public void setTotalOrden(BigDecimal totalOrden) {
        this.totalOrden = totalOrden;
    }

    public String getEstadoOrden() {
        return estadoOrden;
    }

    public void setEstadoOrden(String estadoOrden) {
        this.estadoOrden = estadoOrden;
    }

    public String getNotasOrden() {
        return notasOrden;
    }

    public void setNotasOrden(String notasOrden) {
        this.notasOrden = notasOrden;
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

    public Usuario getClienteUsuario() {
        return clienteUsuario;
    }

    public void setClienteUsuario(Usuario clienteUsuario) {
        this.clienteUsuario = clienteUsuario;
    }

    public List<DetalleOrden> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleOrden> detalles) {
        this.detalles = detalles;
    }

    // Convenience methods for backward compatibility
    public String getNumero() {
        return this.numeroOrden;
    }

    public void setNumero(String numero) {
        this.numeroOrden = numero;
    }

    public BigDecimal getTotal() {
        return this.totalOrden;
    }

    public void setTotal(BigDecimal total) {
        this.totalOrden = total;
    }

    public String getEstado() {
        return this.estadoOrden;
    }

    public void setEstado(String estado) {
        this.estadoOrden = estado;
    }
}