package com.sena.barberspa.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "agendamientos")
public class Agendamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora_inicio")
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin")
    private LocalDateTime fechaHoraFin;

    @Column(name = "precio_final")
    private Double precioFinal;

    private String estado;

    @Column(name = "notas_cliente")
    private String notasCliente;

    @Column(name = "notas_internas")
    private String notasInternas;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relaciones con otras tablas - Optimizadas con FetchType.LAZY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_usuario_id", nullable = false)
    private Usuario clienteUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_id", nullable = true)
    private Personal personal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    // Constructor vacío
    public Agendamiento() {
    }

    // Constructor con parámetros
    public Agendamiento(Usuario clienteUsuario, Personal personal, Servicio servicio, Sucursal sucursal,
            LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, Double precioFinal, String estado,
            String notasCliente, String notasInternas) {
        this.clienteUsuario = clienteUsuario;
        this.personal = personal;
        this.servicio = servicio;
        this.sucursal = sucursal;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.precioFinal = precioFinal;
        this.estado = estado;
        this.notasCliente = notasCliente;
        this.notasInternas = notasInternas;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public Double getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(Double precioFinal) {
        this.precioFinal = precioFinal;
    }

    public String getNotasCliente() {
        return notasCliente;
    }

    public void setNotasCliente(String notasCliente) {
        this.notasCliente = notasCliente;
    }

    public String getNotasInternas() {
        return notasInternas;
    }

    public void setNotasInternas(String notasInternas) {
        this.notasInternas = notasInternas;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getClienteUsuario() {
        return clienteUsuario;
    }

    public void setClienteUsuario(Usuario clienteUsuario) {
        this.clienteUsuario = clienteUsuario;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    // Métodos de compatibilidad con código existente
    public LocalDateTime getFechaHora() {
        return fechaHoraInicio;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHoraInicio = fechaHora;
    }

    public String getMensaje() {
        return notasCliente;
    }

    public void setMensaje(String mensaje) {
        this.notasCliente = mensaje;
    }

    public Usuario getUsuario() {
        return clienteUsuario;
    }

    public void setUsuario(Usuario usuario) {
        this.clienteUsuario = usuario;
    }

    // Método toString - CORREGIDO para evitar StackOverflowError
    @Override
    public String toString() {
        return "Agendamiento{" +
                "id=" + id +
                ", fechaHoraInicio=" + fechaHoraInicio +
                ", fechaHoraFin=" + fechaHoraFin +
                ", estado='" + estado + '\'' +
                ", clienteUsuarioId=" + (clienteUsuario != null ? clienteUsuario.getId() : null) +
                ", personalId=" + (personal != null ? personal.getId() : null) +
                ", servicioId=" + (servicio != null ? servicio.getId() : null) +
                ", sucursalId=" + (sucursal != null ? sucursal.getId() : null) +
                ", precioFinal=" + precioFinal +
                '}';
    }
}
