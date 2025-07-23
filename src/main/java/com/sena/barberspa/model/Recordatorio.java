package com.sena.barberspa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recordatorios")
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private String mensaje;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "fecha_recordatorio")
    private LocalDateTime fechaRecordatorio;

    private boolean activo;
    private boolean fijado;
    private String estado;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "agendamiento_id")
    private Agendamiento agendamiento;

    public Recordatorio() {
    }

    // Constructor para recordatorios manuales
    public Recordatorio(String titulo, String descripcion, LocalDateTime fechaHora, Usuario usuario) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.usuario = usuario;
        this.activo = true;
        this.fijado = false;
    }

    // Constructor para recordatorios de agendamientos
    public Recordatorio(Agendamiento agendamiento, Usuario usuario) {
        this.titulo = "Cita: " + agendamiento.getServicio().getNombre();
        this.descripcion = "Cliente: " + agendamiento.getUsuario().getNombre() +
                " - Sucursal: " + agendamiento.getSucursal().getNombre();
        this.fechaHora = agendamiento.getFechaHora();
        this.agendamiento = agendamiento;
        this.usuario = usuario;
        this.activo = true;
        this.fijado = false;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public LocalDateTime getFechaRecordatorio() {
        return fechaRecordatorio;
    }

    public void setFechaRecordatorio(LocalDateTime fechaRecordatorio) {
        this.fechaRecordatorio = fechaRecordatorio;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isFijado() {
        return fijado;
    }

    public void setFijado(boolean fijado) {
        this.fijado = fijado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Agendamiento getAgendamiento() {
        return agendamiento;
    }

    public void setAgendamiento(Agendamiento agendamiento) {
        this.agendamiento = agendamiento;
    }

    // Métodos de compatibilidad con código existente
    public LocalDateTime getFechaHoraRecordatorio() {
        return fechaRecordatorio != null ? fechaRecordatorio : fechaHora;
    }

    public void setFechaHoraRecordatorio(LocalDateTime fechaHoraRecordatorio) {
        this.fechaRecordatorio = fechaHoraRecordatorio;
    }

    @Override
    public String toString() {
        return "Recordatorio{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", fechaHora=" + fechaHora +
                ", fechaRecordatorio=" + fechaRecordatorio +
                ", activo=" + activo +
                ", fijado=" + fijado +
                ", estado='" + estado + '\'' +
                '}';
    }
}