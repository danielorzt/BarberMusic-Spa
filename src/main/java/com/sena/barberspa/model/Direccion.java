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
@Table(name = "direcciones")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "direccionable_id")
    private Long direccionableId; // ID de la entidad relacionada

    @Column(name = "direccionable_type")
    private String direccionableType; // Tipo de entidad (Sucursal, Usuario, etc.)

    private String direccion;

    private String colonia;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    private String ciudad;

    private String estado;

    private String referencias;

    @Column(name = "es_predeterminada")
    private Boolean esPredeterminada;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación polimórfica con Sucursal cuando sea necesario
    @ManyToOne
    @JoinColumn(name = "direccionable_id", insertable = false, updatable = false)
    private Sucursal sucursal;

    public Direccion() {
    }

    public Direccion(Long direccionableId, String direccionableType, String direccion, String colonia,
            String codigoPostal, String ciudad, String estado, String referencias, Boolean esPredeterminada) {
        this.direccionableId = direccionableId;
        this.direccionableType = direccionableType;
        this.direccion = direccion;
        this.colonia = colonia;
        this.codigoPostal = codigoPostal;
        this.ciudad = ciudad;
        this.estado = estado;
        this.referencias = referencias;
        this.esPredeterminada = esPredeterminada;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDireccionableType() {
        return direccionableType;
    }

    public void setDireccionableType(String direccionableType) {
        this.direccionableType = direccionableType;
    }

    public Long getDireccionableId() {
        return direccionableId;
    }

    public void setDireccionableId(Long direccionableId) {
        this.direccionableId = direccionableId;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getReferencias() {
        return referencias;
    }

    public void setReferencias(String referencias) {
        this.referencias = referencias;
    }

    public Boolean getEsPredeterminada() {
        return esPredeterminada;
    }

    public void setEsPredeterminada(Boolean esPredeterminada) {
        this.esPredeterminada = esPredeterminada;
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

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    @Override
    public String toString() {
        return "Direccion{" +
                "id=" + id +
                ", direccionableType='" + direccionableType + '\'' +
                ", direccionableId=" + direccionableId +
                ", direccion='" + direccion + '\'' +
                ", colonia='" + colonia + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", estado='" + estado + '\'' +
                ", codigoPostal='" + codigoPostal + '\'' +
                '}';
    }
}