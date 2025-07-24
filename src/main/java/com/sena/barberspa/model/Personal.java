package com.sena.barberspa.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "personal")
public class Personal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true) // Cambiado a nullable = true
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_asignada_id") // Nombre correcto de la columna
    private Sucursal sucursalAsignada; // Nombre corregido del campo

    @Column(name = "tipo_personal")
    private String tipoPersonal; // ADMIN_SUCURSAL, ESTILISTA, MASAJISTA, etc.

    @Column(name = "numero_empleado")
    private String numeroEmpleado;

    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;

    @Column(name = "activo_en_empresa")
    private Boolean activoEnEmpresa;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación muchos a muchos con Especialidades
    @ManyToMany
    @JoinTable(
        name = "especialidad_personal",
        joinColumns = @JoinColumn(name = "personal_id"),
        inverseJoinColumns = @JoinColumn(name = "especialidad_id")
    )
    private List<Especialidad> especialidades;

    public Personal() {
    }

    public Personal(Usuario usuario, Sucursal sucursalAsignada, String tipoPersonal, String numeroEmpleado,
            LocalDate fechaContratacion, Boolean activoEnEmpresa) {
        this.usuario = usuario;
        this.sucursalAsignada = sucursalAsignada;
        this.tipoPersonal = tipoPersonal;
        this.numeroEmpleado = numeroEmpleado;
        this.fechaContratacion = fechaContratacion;
        this.activoEnEmpresa = activoEnEmpresa;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

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

    public Sucursal getSucursalAsignada() {
        return sucursalAsignada;
    }

    public void setSucursalAsignada(Sucursal sucursalAsignada) {
        this.sucursalAsignada = sucursalAsignada;
    }

    public String getTipoPersonal() {
        return tipoPersonal;
    }

    public void setTipoPersonal(String tipoPersonal) {
        this.tipoPersonal = tipoPersonal;
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public Boolean getActivoEnEmpresa() {
        return activoEnEmpresa;
    }

    public void setActivoEnEmpresa(Boolean activoEnEmpresa) {
        this.activoEnEmpresa = activoEnEmpresa;
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

    public List<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<Especialidad> especialidades) {
        this.especialidades = especialidades;
    }

    // Métodos de compatibilidad para código antiguo
    public String getEstado() {
        return this.activoEnEmpresa != null && this.activoEnEmpresa ? "activo" : "inactivo";
    }

    public void setEstado(String estado) {
        this.activoEnEmpresa = "activo".equalsIgnoreCase(estado);
    }

    // Alias para sucursal (compatibility)
    public Sucursal getSucursal() {
        return this.sucursalAsignada;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursalAsignada = sucursal;
    }

    @Override
    public String toString() {
        return "Personal{" +
                "id=" + id +
                ", usuarioId=" + (usuario != null ? usuario.getId() : null) +
                ", sucursalId=" + (sucursalAsignada != null ? sucursalAsignada.getId() : null) +
                ", tipoPersonal='" + tipoPersonal + '\'' +
                ", numeroEmpleado='" + numeroEmpleado + '\'' +
                ", fechaContratacion=" + fechaContratacion +
                ", activoEnEmpresa=" + activoEnEmpresa +
                '}';
    }
}