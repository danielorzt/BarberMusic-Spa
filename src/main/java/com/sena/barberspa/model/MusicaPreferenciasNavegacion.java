package com.sena.barberspa.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "musica_preferencias_navegacion")
public class MusicaPreferenciasNavegacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_opcion")
    private String nombreOpcion;

    private String descripcion;

    @Column(name = "stream_url_ejemplo")
    private String streamUrlEjemplo;

    private Boolean activo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MusicaPreferenciasNavegacion() {
    }

    public MusicaPreferenciasNavegacion(String nombreOpcion, String descripcion, String streamUrlEjemplo,
            Boolean activo) {
        this.nombreOpcion = nombreOpcion;
        this.descripcion = descripcion;
        this.streamUrlEjemplo = streamUrlEjemplo;
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

    public String getNombreOpcion() {
        return nombreOpcion;
    }

    public void setNombreOpcion(String nombreOpcion) {
        this.nombreOpcion = nombreOpcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getStreamUrlEjemplo() {
        return streamUrlEjemplo;
    }

    public void setStreamUrlEjemplo(String streamUrlEjemplo) {
        this.streamUrlEjemplo = streamUrlEjemplo;
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

    @Override
    public String toString() {
        return "MusicaPreferenciasNavegacion{" +
                "id=" + id +
                ", nombreOpcion='" + nombreOpcion + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                '}';
    }
}